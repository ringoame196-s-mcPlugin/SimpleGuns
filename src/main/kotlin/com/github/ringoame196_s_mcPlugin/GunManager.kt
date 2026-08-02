package com.github.ringoame196_s_mcPlugin

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class GunManager {

    protected abstract fun displayAmmo(player: Player, gunItem: ItemStack)
    protected abstract fun shot(player: Player, gun: Gun)

    protected abstract fun consumeAmmoOrDryFire(
        player: Player,
        gunItem: ItemStack,
        gunMeta: GunMeta
    ): Boolean

    protected abstract fun handleNoAmmo(player: Player)

    protected abstract fun removeAmmo(gunItem: ItemStack, player: Player)

    fun shot(player: Player, gunItem: ItemStack, gun: Gun) {
        // 弾薬のチェック＆消費に失敗した（撃てない）場合は中断
        val gunMeta = gunItem.itemMeta.gun
        if (!consumeAmmoOrDryFire(player, gunItem, gunMeta)) {
            handleNoAmmo(player)
            return
        }

        // 成功した場合のみ発砲処理を行う
        executeRaycastShot(player, gun)
        removeAmmo(gunItem, player)
        displayAmmo(player, gunItem)
    }

    private fun executeRaycastShot(player: Player, gun: Gun) {
        val sound = Sound.ENTITY_FIREWORK_ROCKET_BLAST
        player.world.playSound(player.location, sound, 1f, 1f)

        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction

        // ブロックとの衝突判定
        val blockHit = player.world.rayTraceBlocks(eyeLocation, direction, gun.firingRangeDistance)
        val maxDistance = blockHit?.hitPosition?.distance(eyeLocation.toVector()) ?: gun.firingRangeDistance

        // 弾道パーティクル描画
        val step = 0.5
        val steps = (maxDistance / step).toInt()
        for (i in 1..steps) {
            val point = eyeLocation.clone().add(direction.clone().multiply(i * step))
            player.world.spawnParticle(Particle.CRIT, point, 1, 0.0, 0.0, 0.0, 0.0)
        }

        // エンティティ判定 & ダメージ処理
        val result = player.world.rayTraceEntities(eyeLocation, direction, maxDistance) { entity ->
            entity != player && entity is LivingEntity
        }

        if (result != null) {
            val targetEntity = result.hitEntity as LivingEntity
            targetEntity.damage(gun.damage, player)
            hitDirection(player)
        }
    }

    fun hitDirection(player: Player) {
        val sound = Sound.ENTITY_ARROW_HIT_PLAYER
        player.playSound(player.location, sound, 1f, 1f)
    }
}
