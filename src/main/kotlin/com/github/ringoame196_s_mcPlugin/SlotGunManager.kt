package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotGunManager : GunManager() {

    override fun displayAmmo(player: Player, gunItem: ItemStack) {
        val meta = gunItem.itemMeta ?: return
        val currentSlot = meta.gun.currentSlot
        val slots = meta.gun.slots

        val builder = StringBuilder()

        for (i in slots.indices) {
            val hasAmmo = slots[i] != 0
            val icon = if (hasAmmo) "●" else "○"

            if (i == currentSlot) {
                builder.append("§e[$icon]§r ") // 選択中（黄色）
            } else {
                builder.append("§7$icon§r ") // それ以外（グレー）
            }
        }

        val message = builder.toString().trimEnd()
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(message))
    }

    /**
     * 手動でシリンダーを回す処理
     */
    fun next(slotGun: SlotGun, gunItem: ItemStack, player: Player) {
        val meta = gunItem.itemMeta ?: return
        val gunMeta = meta.gun

        val nextSlot = (gunMeta.currentSlot + 1) % slotGun.slot
        gunMeta.currentSlot = nextSlot
        gunItem.itemMeta = gunMeta.rawMeta

        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 1.5f)
        displayAmmo(player, gunItem)
        player.inventory.setItemInMainHand(gunItem)
    }

    /**
     * 弾薬チェック & 発砲時のスロット消費・空撃ち回転処理
     */
    override fun consumeAmmoOrDryFire(player: Player, gunItem: ItemStack, gunMeta: GunMeta, gun: Gun): Boolean {
        val slotGun = gun as SlotGun
        val cylinderSlots = gunMeta.slots
        val currentSlot = gunMeta.currentSlot

        if (currentSlot !in cylinderSlots.indices) return false

        val ammoId = cylinderSlots[currentSlot]

        // 【空撃ち時】
        if (ammoId == 0) {
            handleNoAmmo(player) // カチッと音を鳴らし、シリンダーを1つ進める
            return false // 射撃不可（レイキャストを行わない）
        }

        // 【実弾発砲時】
        // 1. スロットを空にする
        cylinderSlots[currentSlot] = 0
        gunMeta.slots = cylinderSlots

        // 2. 次のスロットへ進める
        if (slotGun.autoReload) {
            val nextSlot = (currentSlot + 1) % cylinderSlots.size
            gunMeta.currentSlot = nextSlot
        }

        // 3. 総残弾数も減らす
        gunMeta.reduceAmmo(1)

        // 4. メタデータをアイテムに書き戻す
        gunItem.itemMeta = gunMeta.rawMeta
        player.inventory.setItemInMainHand(gunItem)

        return true // 射撃許可
    }

    /**
     * 空撃ち（Dry Fire）時の挙動
     */
    override fun handleNoAmmo(player: Player) {
        val gunItem = player.inventory.itemInMainHand

        // 空撃ち音
        player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 1f, 1.8f)

        // アクションバー表示更新
        displayAmmo(player, gunItem)
    }

    override fun removeAmmo(gunItem: ItemStack, player: Player) {
        // consumeAmmoOrDryFire 側でスロット・ammo共に減算・書き込み済みのため空実装でOK
    }

    /**
     * 発射エントリーポイント
     */
    public override fun shot(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand

        // 親クラス（GunManager）のテンプレート処理を呼び出すだけ！
        shot(player, gunItem, gun)
    }

    /**
     * 1発ずつリロードする処理
     */
    fun reloadSingle(player: Player, gun: SlotGun) {
        val gunItem = player.inventory.itemInMainHand
        val ammoItem = player.inventory.itemInOffHand

        val ammoMeta = ammoItem.itemMeta ?: return
        val ammoId = ammoMeta.gun.id ?: return

        val reloadAmmo = gun.ammoList.firstOrNull { it.id == ammoId } ?: return
        if (ammoItem.amount < reloadAmmo.ammoCost) return

        // クールタイム中なら実行しない
        if (isAmmoOnCooldown(player, ammoItem)) return

        val gunMeta = gunItem.itemMeta?.gun ?: return
        val cylinderSlots = gunMeta.slots

        // 空いている最初のスロットを探す (0 の場所)
        val emptyIndex = cylinderSlots.indexOfFirst { it == 0 }
        if (emptyIndex == -1) return // 満タンならリロードしない

        // 弾薬を消費してスロットに装テン
        if (reloadAmmo.ammoCost > 0) {
            ammoItem.amount -= reloadAmmo.ammoCost
        }
        cylinderSlots[emptyIndex] = 1
        gunMeta.slots = cylinderSlots

        gunItem.itemMeta = gunMeta.rawMeta
        player.inventory.setItemInMainHand(gunItem)

        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1.4f)
        displayAmmo(player, gunItem)
        setAmmoCooldown(player, ammoItem, reloadAmmo.cooldownSeconds)
    }
}
