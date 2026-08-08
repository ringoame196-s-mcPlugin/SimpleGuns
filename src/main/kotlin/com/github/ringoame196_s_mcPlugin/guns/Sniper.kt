package com.github.ringoame196_s_mcPlugin.guns

import com.github.ringoame196_s_mcPlugin.interfaces.LeftClickable
import com.github.ringoame196_s_mcPlugin.interfaces.StopUsing
import com.github.ringoame196_s_mcPlugin.managers.GunItemManager
import com.github.ringoame196_s_mcPlugin.managers.SlotGunManager
import com.github.ringoame196_s_mcPlugin.models.Ammo
import com.github.ringoame196_s_mcPlugin.models.SlotGun
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class Sniper(plugin: Plugin, override val ammoList: List<Ammo>) :
    LeftClickable,
    SlotGun,
    StopUsing {
    override val id = "sniper"
    override val material = Material.SPYGLASS
    override val slot = 5
    override val firingRangeDistance = 20.0
    override val damage = 8.0
    override val gunManager = SlotGunManager
    override val autoReload = false

    // lazy で遅延初期化
    override val item: ItemStack by lazy { GunItemManager.makeGunItem(this) }
    override val recipe: CraftingRecipe by lazy { createRecipe(plugin) }

    override fun onLeftClick(player: Player, gunItem: ItemStack) {
        if (player.isSneaking) {
            reload(player)
        } else {
            next(player, gunItem)
        }
    }

    override fun onStopUsing(player: Player, gunItem: ItemStack) {
        if (player.isSneaking) {
            shot(player)
        }
    }

    override fun shot(player: Player) {
        gunManager.shot(player, this)
    }

    override fun reload(player: Player) {
        gunManager.reloadSingle(player, this)
    }

    private fun next(player: Player, gunItem: ItemStack) {
        gunManager.next(this, gunItem, player)
    }

    private fun createRecipe(plugin: Plugin): CraftingRecipe {
        val key = NamespacedKey(plugin, "${id}_shapeless")
        return ShapelessRecipe(key, item).apply {
            addIngredient(Material.IRON_INGOT)
            addIngredient(Material.COBBLESTONE)
            addIngredient(Material.TNT)
        }
    }
}
