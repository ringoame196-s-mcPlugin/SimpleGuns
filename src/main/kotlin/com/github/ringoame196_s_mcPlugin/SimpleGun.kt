package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class SimpleGun(name: String, plugin: Plugin) : Gun {
    override val id = "simple_gun"
    override val gun = GunManager.makeGun(Material.IRON_HOE, id, name)
    override val recipe: CraftingRecipe by lazy { createRecipe(plugin) }

    private fun createRecipe(plugin: Plugin): CraftingRecipe {
        val key = NamespacedKey(plugin, "${id}_shapeless")
        return ShapelessRecipe(key, gun).apply {
            addIngredient(Material.IRON_INGOT)
            addIngredient(Material.COBBLESTONE)
            addIngredient(Material.GUNPOWDER)
        }
    }
}
