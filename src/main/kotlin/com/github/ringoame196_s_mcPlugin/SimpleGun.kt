package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class SimpleGun(name: String, plugin: Plugin) : GunItem, Gun, LeftClickable, RightClickable {
    override val id = "simple_gun"
    override val item = GunManager.makeGun(Material.IRON_HOE, id, name)
    override val recipe: CraftingRecipe by lazy { createRecipe(plugin) }
    override val firingRangeDistance = 8.0
    override val damage = 2.0

    override fun onLeftClick(player: Player) {
        reload()
    }

    override fun onRightClick(player: Player) {
        shot(player)
    }

    override fun shot(player: Player) {
        GunManager.shot(player, this)
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
