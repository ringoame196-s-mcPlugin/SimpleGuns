package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object StandardGunManager : GunManager() {
    override fun displayAmmo(player: Player, gunItem: ItemStack) {
        val meta = gunItem.itemMeta
        val currentAmmo = meta.gun.ammo
        val maxAmmo = meta.gun.maxAmmo

        val color = getAmmoColor(currentAmmo, maxAmmo)

        val totalBars = 10
        val percentage = if (maxAmmo > 0) currentAmmo.toDouble() / maxAmmo.toDouble() else 0.0
        val filledBars = (percentage * totalBars).toInt().coerceIn(0, totalBars)
        val emptyBars = totalBars - filledBars

        val barString = "│".repeat(filledBars)
        val emptyString = "│".repeat(emptyBars)

        val message = "§7Ammo: §8[$color$barString§8$emptyString§8] $color$currentAmmo §7/ §f$maxAmmo"
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(message))
    }

    private fun getAmmoColor(current: Int, max: Int): ChatColor {
        if (max <= 0) return ChatColor.WHITE
        val ratio = current.toDouble() / max.toDouble()
        return when {
            ratio > 0.5 -> ChatColor.GREEN
            ratio > 0.2 -> ChatColor.YELLOW
            else -> ChatColor.RED
        }
    }

    public override fun shot(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val meta = gunItem.itemMeta ?: return
        val currentAmmo = meta.gun.ammo

        if (currentAmmo <= 0) {
            player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 1f, 1.5f)
            player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                *TextComponent.fromLegacyText("§c§lNO AMMO! RELOAD!")
            )
            return
        }

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
            player.world.spawnParticle(
                Particle.CRIT,
                point,
                1,
                0.0, 0.0, 0.0, 0.0
            )
        }

        val result = player.world.rayTraceEntities(
            eyeLocation,
            direction,
            maxDistance
        ) { entity -> entity != player && entity is LivingEntity }

        if (result != null) {
            val targetEntity = result.hitEntity as LivingEntity
            targetEntity.damage(gun.damage, player)
            hitDirection(player)
        }

        meta.gun.reduceAmmo(1)
        gunItem.itemMeta = meta

        player.inventory.setItemInMainHand(gunItem)

        displayAmmo(player, gunItem)
    }

    /**
     * 【一括リロード】
     * 必要なコスト分（全弾補充に必要な弾数）をオフハンドから一度に減らして全回復する
     */
    fun reloadAll(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val ammoItem = player.inventory.itemInOffHand

        val ammoMeta = ammoItem.itemMeta ?: return
        val ammoId = ammoMeta.gun.id

        val reloadAmmo = gun.ammoList.firstOrNull { it.id == ammoId } ?: return

        val gunMeta = gunItem.itemMeta ?: return
        val currentAmmo = gunMeta.gun.ammo
        val maxAmmo = gunMeta.gun.maxAmmo
        val missingAmmo = maxAmmo - currentAmmo

        // 既に満タンならリロードしない
        if (missingAmmo <= 0) return

        // 全回復に必要なコスト（1発コスト × 不足分）
        val neededCost = reloadAmmo.ammoCost * missingAmmo
        if (ammoItem.amount < neededCost) return

        // コスト分消費
        if (neededCost > 0) {
            ammoItem.amount -= neededCost
        }

        // 弾数を満タンに更新
        gunMeta.gun.ammo = maxAmmo
        gunItem.itemMeta = gunMeta

        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1f)
        displayAmmo(player, gunItem)
    }

    /**
     * 【1発ずつリロード】
     * 1回の実行で1発分（ammoCost）だけ消費して 1 発補充する（ショットガンや手動装テン向け）
     */
    fun reloadSingle(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val ammoItem = player.inventory.itemInOffHand

        val ammoMeta = ammoItem.itemMeta ?: return
        val ammoId = ammoMeta.gun.id

        val reloadAmmo = gun.ammoList.firstOrNull { it.id == ammoId } ?: return

        val gunMeta = gunItem.itemMeta ?: return
        val currentAmmo = gunMeta.gun.ammo
        val maxAmmo = gunMeta.gun.maxAmmo

        // 既に満タンならリロードしない
        if (currentAmmo >= maxAmmo) return

        // 1発分のコストチェック
        val ammoCost = reloadAmmo.ammoCost
        if (ammoItem.amount < ammoCost) return

        // 1発分のコストを消費
        if (ammoCost > 0) {
            ammoItem.amount -= ammoCost
        }

        // 弾数を 1 加算
        val newAmmo = currentAmmo + 1
        gunMeta.gun.ammo = newAmmo
        gunItem.itemMeta = gunMeta

        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1.3f)
        displayAmmo(player, gunItem)
    }
}
