package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

object GunManager {
    lateinit var plugin: JavaPlugin
    private const val GUN_ID = "gun_id"
    private val nameKey by lazy { NamespacedKey(plugin, GUN_ID) }

    fun makeGun(material: Material, gunId: String, displayName: String): ItemStack {
        val gun = ItemStack(material)
        val meta = gun.itemMeta

        setGunId(meta, gunId)
        meta.setDisplayName(displayName)

        gun.setItemMeta(meta)
        return gun
    }

    private fun setGunId(meta: ItemMeta, gunId: String) {
        meta.persistentDataContainer.set(nameKey, PersistentDataType.STRING, gunId)
    }

    fun getGunId(gun: ItemStack): String? {
        val meta = gun.itemMeta
        return meta.persistentDataContainer.get(nameKey, PersistentDataType.STRING)
    }

    fun shot(player: Player): LivingEntity? {
        val firingRangeDistance = 5.0
        val sound = Sound.ENTITY_FIREWORK_ROCKET_BLAST
        player.world.playSound(player.location, sound, 1f, 1f)

        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction

        val blockHit = player.world.rayTraceBlocks(eyeLocation, direction, firingRangeDistance)
        val maxDistance = blockHit?.hitPosition?.distance(eyeLocation.toVector()) ?: firingRangeDistance

        val step = 0.5 // パーティクルの間隔
        val steps = (maxDistance / step).toInt()

        for (i in 1..steps) {
            val point = eyeLocation.clone().add(direction.clone().multiply(i * step))
            player.world.spawnParticle(
                Particle.CRIT, // 他に FLAME, REDSTONE などもおすすめ
                point,
                1,
                0.0, 0.0, 0.0, 0.0
            )
        }

        val result = player.world.rayTraceEntities(
            eyeLocation,
            direction,
            maxDistance
        ) { entity -> entity != player && entity is LivingEntity } ?: return null
        return result.hitEntity as LivingEntity
    }

    fun reload() {
    }
}
