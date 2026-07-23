package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SimpleGun(private val name: String) : Gun {
    override val id = "simple_gun"
    override val gun: ItemStack = ItemStack(Material.IRON_HOE).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(this@SimpleGun.name)
        }
    }
}
