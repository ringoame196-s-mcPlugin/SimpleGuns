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
    private const val GUN_ID = "gun_item_id"
    private const val GUN_AMMO = "gun_ammo"
    private val nameKey by lazy { NamespacedKey(plugin, GUN_ID) }
    private val ammonKey by lazy { NamespacedKey(plugin, GUN_AMMO) }

    fun makeGunItem(gun: GunItem): ItemStack {
        val gunItem = ItemStack(gun.material)
        val meta = gunItem.itemMeta ?: return gunItem
        meta.setDisplayName(gun.displayName)

        setGunItemId(meta, gun.id)
        gunItem.itemMeta = meta
        return gunItem
    }

    fun makeGunItem(gun: GunItem, maxAmmon: Int): ItemStack {
        val gunItem = makeGunItem(gun)
        val meta = gunItem.itemMeta ?: return gunItem
        setAmmo(meta, maxAmmon)
        displayAmmo(meta, maxAmmon)
        gunItem.itemMeta = meta
        return gunItem
    }

    private fun setGunItemId(meta: ItemMeta, gunId: String) {
        meta.persistentDataContainer.set(nameKey, PersistentDataType.STRING, gunId)
    }

    fun getGunItemId(meta: ItemMeta): String? {
        return meta.persistentDataContainer.get(nameKey, PersistentDataType.STRING)
    }

    fun setAmmo(meta: ItemMeta, ammo: Int) {
        meta.persistentDataContainer.set(ammonKey, PersistentDataType.INTEGER, ammo)
    }

    fun removeGunAmmo(meta: ItemMeta, value: Int) {
        val ammo = getGunAmmo(meta) ?: return
        // 0 未満にならないようにガード
        setAmmo(meta, (ammo - value).coerceAtLeast(0))
    }

    fun getGunAmmo(meta: ItemMeta): Int? {
        return meta.persistentDataContainer.get(ammonKey, PersistentDataType.INTEGER)
    }

    fun displayAmmo(meta: ItemMeta, maxAmmon: Int) {
        val ammon = getGunAmmo(meta) ?: 0
        meta.lore = listOf("$ammon/$maxAmmon")
    }

    fun shot(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val meta = gunItem.itemMeta ?: return
        val currentAmmon = getGunAmmo(meta) ?: 0

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
        removeGunAmmo(meta, 1)
        displayAmmo(meta, gun.maxAmmo)
        gunItem.itemMeta = meta

        player.inventory.setItemInMainHand(gunItem)
    }

    fun hitDirection(player: Player) {
        val sound = Sound.ENTITY_ARROW_HIT_PLAYER
        player.playSound(player.location, sound, 1f, 1f)
    }

    fun reload(player: Player, gun: Gun) {
        val gunItem = player.inventory.itemInMainHand
        val ammonItem = player.inventory.itemInOffHand

        if (!gun.ammoList.map { it.id }.contains(getGunItemId(ammonItem.itemMeta))) {
            return
        }

        ammonItem.amount -= 1
        player.inventory.setItemInOffHand(ammonItem)

        val meta = gunItem.itemMeta
        setAmmo(meta, gun.maxAmmo)
        displayAmmo(meta, gun.maxAmmo)
        gunItem.itemMeta = meta

        player.inventory.setItemInMainHand(gunItem)
    }
}
