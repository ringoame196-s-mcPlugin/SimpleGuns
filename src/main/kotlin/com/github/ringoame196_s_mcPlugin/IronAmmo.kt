package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class IronAmmo(name: String, plugin: Plugin) : Ammo {
    override val id = "iron_ammo"
    override val displayName = name
    override val material = Material.IRON_NUGGET
    override val item = GunManager.makeGunItem(this)
    override val recipe: CraftingRecipe by lazy { createRecipe(plugin) }

    private fun createRecipe(plugin: Plugin): CraftingRecipe {
        val key = NamespacedKey(plugin, "${id}_shapeless")
        return ShapelessRecipe(key, item).apply {
            addIngredient(Material.GUNPOWDER)
            addIngredient(Material.IRON_NUGGET)
        }
    }
}
