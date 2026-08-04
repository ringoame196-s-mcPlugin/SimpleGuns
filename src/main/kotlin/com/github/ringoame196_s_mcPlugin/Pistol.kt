package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class Pistol(override val displayName: String, plugin: Plugin, override val ammoList: List<Ammo>) : StandardGun, LeftClickable, RightClickable {
    override val id = "pistol"
    override val material = Material.IRON_HOE
    override val maxAmmo = 15
    override val firingRangeDistance = 8.0
    override val damage = 2.0

    // lazy で遅延初期化
    override val item: ItemStack by lazy { GunItemManager.makeGunItem(this) }
    override val recipe: CraftingRecipe by lazy { createRecipe(plugin) }

    override fun onLeftClick(player: Player, gunItem: ItemStack) {
        shot(player)
    }

    override fun onRightClick(player: Player, gunItem: ItemStack) {
        reload(player)
    }

    override fun shot(player: Player) {
        gunManager.shot(player, this)
    }

    override fun reload(player: Player) {
        if (player.isSneaking) {
            gunManager.reloadAll(player, this)
        } else {
            gunManager.reloadSingle(player, this)
        }
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
