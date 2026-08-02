package com.github.ringoame196_s_mcPlugin

import org.bukkit.inventory.ItemStack

object GunItemManager {
    fun makeGunItem(gun: GunItem): ItemStack {
        val gunItem = ItemStack(gun.material)
        val meta = gunItem.itemMeta ?: return gunItem
        meta.setDisplayName(gun.displayName)
        meta.gun.id = gun.id
        gunItem.itemMeta = meta
        return gunItem
    }
}
