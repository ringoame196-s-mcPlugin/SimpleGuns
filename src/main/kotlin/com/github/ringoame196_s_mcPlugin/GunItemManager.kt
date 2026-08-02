package com.github.ringoame196_s_mcPlugin

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object GunItemManager {
    fun makeGunItem(gun: GunItem): ItemStack {
        val gunItem = ItemStack(gun.material)
        val meta = gunItem.itemMeta ?: return gunItem
        meta.setDisplayName(gun.displayName)
        meta.gun.id = gun.id

        when (gun) {
            is StandardGun -> setupStandardGunItem(gun, meta)
            is HasSlotGun -> setupHasSlotGunItem(gun, meta)
        }
        gunItem.itemMeta = meta

        return gunItem
    }

    private fun setupStandardGunItem(gun: StandardGun, meta: ItemMeta) {
        val maxAmmo = gun.maxAmmo
        meta.gun.ammo = maxAmmo
        meta.gun.maxAmmo = maxAmmo
    }

    private fun setupHasSlotGunItem(gun: HasSlotGun, meta: ItemMeta) {
        meta.gun.currentSlot = 0

        // 初期状態は空のシリンダー
        meta.gun.slots = IntArray((gun).slot) { 0 }
    }
}
