package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class SimpleGun(name: String, plugin: Plugin) : GunItem, Gun, LeftClickable, RightClickable {
    override val id = "simple_gun"
    override val displayName = name
    override val material = Material.IRON_HOE
    override val maxAmmon = 5
    override val item = GunManager.makeGun(this, maxAmmon)
    override val recipe: CraftingRecipe by lazy { createRecipe(plugin) }
    override val firingRangeDistance = 8.0
    override val damage = 2.0

    override fun onLeftClick(player: Player, gunItem: ItemStack) {
        reload()
    }

    override fun onRightClick(player: Player, gunItem: ItemStack) {
        shot(player, gunItem)
    }

    override fun shot(player: Player, gunItem: ItemStack) {
        GunManager.shot(player, this, gunItem)
    }

    override fun reload() {
    }

    private fun createRecipe(plugin: Plugin): CraftingRecipe {
        val key = NamespacedKey(plugin, "${id}_shapeless")
        return ShapelessRecipe(key, item).apply {
            addIngredient(Material.IRON_INGOT)
            addIngredient(Material.COBBLESTONE)
            addIngredient(Material.GUNPOWDER)
        }
    }
}
