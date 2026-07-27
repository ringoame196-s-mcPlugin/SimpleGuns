package com.github.ringoame196_s_mcPlugin

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
    private const val GUN_AMMON = "gun_ammon"
    private val nameKey by lazy { NamespacedKey(plugin, GUN_ID) }
    private val ammonKey by lazy { NamespacedKey(plugin, GUN_AMMON) }

    fun makeGun(gun: GunItem): ItemStack {
        val gunItem = ItemStack(gun.material)
        val meta = gunItem.itemMeta ?: return gunItem
        meta.setDisplayName(gun.displayName)

        setGunId(meta, gun.id)
        gunItem.itemMeta = meta
        return gunItem
    }

    fun makeGun(gun: GunItem, maxAmmon: Int): ItemStack {
        val gunItem = makeGun(gun)
        val meta = gunItem.itemMeta ?: return gunItem
        setAmmon(meta, maxAmmon)
        displayAmmon(meta, maxAmmon)
        gunItem.itemMeta = meta
        return gunItem
    }

    private fun setGunId(meta: ItemMeta, gunId: String) {
        meta.persistentDataContainer.set(nameKey, PersistentDataType.STRING, gunId)
    }

    fun getGunId(meta: ItemMeta): String? {
        return meta.persistentDataContainer.get(nameKey, PersistentDataType.STRING)
    }

    fun setAmmon(meta: ItemMeta, ammon: Int) {
        meta.persistentDataContainer.set(ammonKey, PersistentDataType.INTEGER, ammon)
    }

    fun removeGunAmmon(meta: ItemMeta, value: Int) {
        val ammon = getGunAmmon(meta) ?: return
        // 0 未満にならないようにガード
        setAmmon(meta, (ammon - value).coerceAtLeast(0))
    }

    fun getGunAmmon(meta: ItemMeta): Int? {
        return meta.persistentDataContainer.get(ammonKey, PersistentDataType.INTEGER)
    }

    fun displayAmmon(meta: ItemMeta, maxAmmon: Int, player: Player? = null) {
        val ammon = getGunAmmon(meta) ?: 0
        meta.lore = listOf("$ammon/$maxAmmon")
    }

    fun shot(player: Player, gun: Gun, gunItem: ItemStack) {
        val meta = gunItem.itemMeta ?: return
        val currentAmmon = getGunAmmon(meta) ?: 0

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
        removeGunAmmon(meta, 1)
        displayAmmon(meta, gun.maxAmmon, player)
        gunItem.itemMeta = meta

        player.inventory.setItemInMainHand(gunItem)
    }

    fun hitDirection(player: Player) {
        val sound = Sound.ENTITY_ARROW_HIT_PLAYER
        player.playSound(player.location, sound, 1f, 1f)
    }

    fun reload() {
    }
}
