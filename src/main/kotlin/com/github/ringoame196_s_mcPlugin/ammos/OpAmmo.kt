package com.github.ringoame196_s_mcPlugin.ammos

import com.github.ringoame196_s_mcPlugin.managers.GunItemManager
import com.github.ringoame196_s_mcPlugin.models.Ammo
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import kotlin.lazy

class OpAmmo : Ammo {
    override val id = "op_ammo"
    override val material = Material.IRON_NUGGET
    override val item: ItemStack by lazy { createItem() }
    override val recipe = null
    override val ammoCost: Int = 0
    override val cooldownSeconds = 0.0

    private fun createItem(): ItemStack {
        val item = GunItemManager.makeGunItem(this)
        item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1)
        return item
    }
}
