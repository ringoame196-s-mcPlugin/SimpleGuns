package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotGunManager : GunManager() {
    override fun displayAmmo(player: Player, gunItem: ItemStack) {
        val meta = gunItem.itemMeta
        val currentSlot = meta.gun.currentSlot
        val slots = meta.gun.slots

        val builder = StringBuilder()

        for (i in slots.indices) {
            val hasAmmo = slots[i] != 0
            val icon = if (hasAmmo) "●" else "○"

            if (i == currentSlot) {
                builder.append("§e[$icon]§r ") // 選択中のスロットを強調（黄色）
            } else {
                builder.append("§7$icon§r ") // それ以外（グレー）
            }
        }

        val message = builder.toString().trimEnd()
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(message))
    }

    // 手動でシリンダーを回す処理（キー操作や右クリック等）
    fun next(hasSlotGun: HasSlotGun, gunItem: ItemStack, player: Player) {
        val meta = gunItem.itemMeta ?: return
        val gunMeta = meta.gun

        val nextSlot = (gunMeta.currentSlot + 1) % hasSlotGun.slot
        gunMeta.currentSlot = nextSlot
        gunItem.itemMeta = gunMeta.rawMeta

        // カチャッとシリンダーを回す音
        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 1.5f)

        // アクションバーにシリンダー状態を表示
        displayAmmo(player, gunItem)
        player.inventory.setItemInMainHand(gunItem)
    }

    // 発射処理
    public override fun shot(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val meta = gunItem.itemMeta ?: return
        val gunMeta = meta.gun

        val cylinderSlots = gunMeta.slots
        val currentSlot = gunMeta.currentSlot

        if (currentSlot !in cylinderSlots.indices) return

        val ammoId = cylinderSlots[currentSlot]

        // 空撃ち判定 (カチッ)
        if (ammoId == 0) {
            player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 1f, 1.8f)

            // シリンダーを1つ進める
            val nextSlot = (currentSlot + 1) % cylinderSlots.size
            gunMeta.currentSlot = nextSlot
            gunItem.itemMeta = gunMeta.rawMeta
            player.inventory.setItemInMainHand(gunItem)

            // 表示更新
            displayAmmo(player, gunItem)
            return
        }

        // --- 射撃演出・レイキャスト処理（省略なし）---
        val sound = Sound.ENTITY_FIREWORK_ROCKET_BLAST
        player.world.playSound(player.location, sound, 1f, 1f)

        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction
        val blockHit = player.world.rayTraceBlocks(eyeLocation, direction, gun.firingRangeDistance)
        val maxDistance = blockHit?.hitPosition?.distance(eyeLocation.toVector()) ?: gun.firingRangeDistance

        val step = 0.5
        val steps = (maxDistance / step).toInt()
        for (i in 1..steps) {
            val point = eyeLocation.clone().add(direction.clone().multiply(i * step))
            player.world.spawnParticle(Particle.CRIT, point, 1, 0.0, 0.0, 0.0, 0.0)
        }

        val result = player.world.rayTraceEntities(eyeLocation, direction, maxDistance) { entity ->
            entity != player && entity is LivingEntity
        }
        if (result != null) {
            val targetEntity = result.hitEntity as LivingEntity
            targetEntity.damage(gun.damage, player)
            hitDirection(player)
        }

        // 撃ったスロットを空にする & 次のスロットへ
        cylinderSlots[currentSlot] = 0
        gunMeta.slots = cylinderSlots

        val nextSlot = (currentSlot + 1) % cylinderSlots.size
        gunMeta.currentSlot = nextSlot

        // 総残弾数の減少（必要に応じて）
        gunMeta.reduceAmmo(1)

        gunItem.itemMeta = gunMeta.rawMeta
        player.inventory.setItemInMainHand(gunItem)

        // 画面にシリンダー表示
        displayAmmo(player, gunItem)
    }

    // 1発ずつリロードする処理
    fun reloadSingle(player: Player, gun: HasSlotGun) {
        val gunItem = player.inventory.itemInMainHand
        val ammoItem = player.inventory.itemInOffHand

        val ammoMeta = ammoItem.itemMeta ?: return
        val ammoId = ammoMeta.gun.id ?: return

        val reloadAmmo = gun.ammoList.firstOrNull { it.id == ammoId } ?: return
        if (ammoItem.amount < reloadAmmo.ammoCost) return

        val gunMeta = gunItem.itemMeta?.gun ?: return
        val cylinderSlots = gunMeta.slots

        // 空いている最初のスロットを探す (0 の場所)
        val emptyIndex = cylinderSlots.indexOfFirst { it == 0 }

        // 満タンならリロードしない
        if (emptyIndex == -1) return

        // 弾薬を消費してスロットに装填 (1 = 通常弾、必要なら ammoId に応じた数値)
        if (reloadAmmo.ammoCost > 0) {
            ammoItem.amount -= reloadAmmo.ammoCost
        }
        cylinderSlots[emptyIndex] = 1
        gunMeta.slots = cylinderSlots

        gunItem.itemMeta = gunMeta.rawMeta
        player.inventory.setItemInMainHand(gunItem)

        // 装填音 (チャキンッ)
        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1.4f)

        // シリンダー表示
        displayAmmo(player, gunItem)
    }
}
