package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin

object GunManager {
    lateinit var plugin: JavaPlugin

    fun makeGunItem(gun: GunItem): ItemStack {
        val gunItem = ItemStack(gun.material)
        val meta = gunItem.itemMeta ?: return gunItem
        meta.setDisplayName(gun.displayName)
        meta.gun.id = gun.id
        gunItem.itemMeta = meta
        return gunItem
    }

    fun makeGunItem(gun: GunItem, maxAmmon: Int): ItemStack {
        val gunItem = makeGunItem(gun)
        val meta = gunItem.itemMeta ?: return gunItem
        meta.gun.ammo = maxAmmon
        displayAmmo(meta, maxAmmon)
        gunItem.itemMeta = meta
        return gunItem
    }

    fun displayAmmo(meta: ItemMeta, maxAmmon: Int) {
        val ammon = meta.gun.ammo
        meta.lore = listOf("$ammon/$maxAmmon")
    }

    fun shot(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val meta = gunItem.itemMeta ?: return
        val currentAmmon = meta.gun.ammo

        // 弾切れ判定
        if (currentAmmon <= 0) {
            player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 1f, 1f)
            return
        }

        // 射撃音
        val sound = Sound.ENTITY_FIREWORK_ROCKET_BLAST
        player.world.playSound(player.location, sound, 1f, 1f)

        // レイキャスト処理
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

        // エンティティ命中判定
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

        // 弾薬減算とメタの更新
        meta.gun.reduceAmmo(1)
        displayAmmo(meta, gun.maxAmmo)
        gunItem.itemMeta = meta

        player.inventory.setItemInMainHand(gunItem)

        val updatedAmmo = meta.gun.ammo
        displayAmmo(player, updatedAmmo)
    }

    private fun displayAmmo(player: Player, currentAmmon: Int) {
        val message = "${ChatColor.GOLD}Ammo: $currentAmmon"
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(message))
    }

    fun hitDirection(player: Player) {
        val sound = Sound.ENTITY_ARROW_HIT_PLAYER
        player.playSound(player.location, sound, 1f, 1f)
    }

    fun reload(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val ammoItem = player.inventory.itemInOffHand

        val ammoMeta = ammoItem.itemMeta ?: return
        val ammoId = ammoMeta.gun.id

        val reloadAmmo = gun.ammoList.firstOrNull { it.id == ammoId } ?: return

        val ammoCost = reloadAmmo.ammoCost
        if (ammoItem.amount < ammoCost) return

        val gunMeta = gunItem.itemMeta ?: return

        if (ammoCost > 0) {
            ammoItem.amount -= ammoCost
        }

        gunMeta.gun.ammo = gun.maxAmmo
        displayAmmo(gunMeta, gun.maxAmmo)
        gunItem.itemMeta = gunMeta

        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1f)
        displayAmmo(player, gun.maxAmmo)
    }
}
