package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import kotlin.lazy

class OpAmmo(name: String) : Ammo {
    override val id = "op_ammo"
    override val displayName = name
    override val material = Material.IRON_NUGGET
    override val item: ItemStack by lazy { createItem() }
    override val recipe = null

    private fun createItem(): ItemStack {
        val item = GunManager.makeGunItem(this)
        item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1)
        return item
    }
}
