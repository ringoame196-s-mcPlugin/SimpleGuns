package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
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
}
