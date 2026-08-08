package com.github.ringoame196_s_mcPlugin.managers

import com.github.ringoame196_s_mcPlugin.models.Gun
import com.github.ringoame196_s_mcPlugin.models.GunItem
import com.github.ringoame196_s_mcPlugin.models.SlotGun
import com.github.ringoame196_s_mcPlugin.models.StandardGun
import com.github.ringoame196_s_mcPlugin.utils.gun
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object GunItemManager {
    lateinit var configManager: ConfigManager

    fun makeGunItem(gun: GunItem): ItemStack {
        val gunItem = ItemStack(gun.material)
        val meta = gunItem.itemMeta ?: return gunItem

        val displayName = configManager.getDisplayName(gun.id)
        meta.setDisplayName(displayName)

        meta.gun.id = gun.id

        if (gun is Gun) {
            setZeroDamage(meta)
        }

        when (gun) {
            is StandardGun -> setupStandardGunItem(gun, meta)
            is SlotGun -> setupHasSlotGunItem(gun, meta)
        }
        gunItem.itemMeta = meta

        return gunItem
    }

    private fun setupStandardGunItem(gun: StandardGun, meta: ItemMeta) {
        val maxAmmo = gun.maxAmmo
        meta.gun.ammo = maxAmmo
        meta.gun.maxAmmo = maxAmmo
    }

    private fun setupHasSlotGunItem(gun: SlotGun, meta: ItemMeta) {
        meta.gun.currentSlot = 0

        meta.gun.slots = IntArray((gun).slot) { 1 }
    }

    private fun setZeroDamage(meta: ItemMeta) {
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE)

        val zeroModifier = AttributeModifier(
            "zero_attack_damage",
            0.0, // 加算値を 0 に指定
            AttributeModifier.Operation.ADD_NUMBER
        )
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroModifier)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
    }
}
