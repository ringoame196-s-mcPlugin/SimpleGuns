package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotGunManager {

    fun makeGunItem(gun: GunItem): ItemStack {
        val item = GunManager.makeGunItem(gun)
        val meta = item.itemMeta ?: return item
        meta.gun.selectSlot = 0
        // 初期状態は空のシリンダー
        meta.gun.slots = IntArray((gun as? HasSlotGun)?.slot ?: 6) { 0 }
        item.itemMeta = meta
        return item
    }

    // 手動でシリンダーを回す処理（キー操作や右クリック等）
    fun next(hasSlotGun: HasSlotGun, gunItem: ItemStack, player: Player) {
        val meta = gunItem.itemMeta ?: return
        val gunMeta = meta.gun

        val nextSlot = (gunMeta.selectSlot + 1) % hasSlotGun.slot
        gunMeta.selectSlot = nextSlot
        gunItem.itemMeta = gunMeta.rawMeta

        // カチャッとシリンダーを回す音
        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 1.5f)

        // アクションバーにシリンダー状態を表示
        displayCylinderStatus(player, gunMeta.slots, nextSlot)
        player.inventory.setItemInMainHand(gunItem)
    }

    // 発射処理
    fun shotRevolver(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val meta = gunItem.itemMeta ?: return
        val gunMeta = meta.gun

        val cylinderSlots = gunMeta.slots
        val currentSlot = gunMeta.selectSlot

        if (currentSlot !in cylinderSlots.indices) return

        val ammoId = cylinderSlots[currentSlot]

        // 空撃ち判定 (カチッ)
        if (ammoId == 0) {
            player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 1f, 1.8f)

            // シリンダーを1つ進める
            val nextSlot = (currentSlot + 1) % cylinderSlots.size
            gunMeta.selectSlot = nextSlot
            gunItem.itemMeta = gunMeta.rawMeta
            player.inventory.setItemInMainHand(gunItem)

            // 表示更新
            displayCylinderStatus(player, cylinderSlots, nextSlot)
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
            GunManager.hitDirection(player)
        }

        // 撃ったスロットを空にする & 次のスロットへ
        cylinderSlots[currentSlot] = 0
        gunMeta.slots = cylinderSlots

        val nextSlot = (currentSlot + 1) % cylinderSlots.size
        gunMeta.selectSlot = nextSlot

        // 総残弾数の減少（必要に応じて）
        gunMeta.reduceAmmo(1)

        gunItem.itemMeta = gunMeta.rawMeta
        player.inventory.setItemInMainHand(gunItem)

        // 画面にシリンダー表示
        displayCylinderStatus(player, cylinderSlots, nextSlot)
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
        displayCylinderStatus(player, cylinderSlots, gunMeta.selectSlot)
    }

    fun displayCylinderStatus(player: Player, slots: IntArray, currentSlot: Int) {
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
}
