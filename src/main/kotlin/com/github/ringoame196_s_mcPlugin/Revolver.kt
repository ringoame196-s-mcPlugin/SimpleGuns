package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin

class Revolver(override val displayName: String, plugin: Plugin, override val ammoList: List<Ammo>) : LeftClickable, RightClickable, HasSlotGun {
    override val id = "revolver"
    override val material = Material.IRON_AXE
    override val maxAmmo = 5
    override val slot = 5
    override val firingRangeDistance = 8.0
    override val damage = 6.0

    // lazy で遅延初期化
    override val item: ItemStack by lazy { SlotGunManager.makeGunItem(this) }
    override val recipe: CraftingRecipe by lazy { createRecipe(plugin) }

    override fun onLeftClick(player: Player, gunItem: ItemStack) {
        if (player.isSneaking) {
            reload(player)
        } else {
            next(player, gunItem)
        }
    }

    private fun next(player: Player, gunItem: ItemStack) {
        SlotGunManager.next(this, gunItem, player)
    }

    override fun onRightClick(player: Player, gunItem: ItemStack) {
        shot(player)
    }

    override fun shot(player: Player) {
        val gunItem = player.inventory.itemInMainHand
        val selectSlot = gunItem.itemMeta.gun.selectSlot

        SlotGunManager.shot(player, this, selectSlot)
    }

    override fun reload(player: Player) {
        GunManager.reload(player, this)
    }

    private fun createRecipe(plugin: Plugin): CraftingRecipe {
        val key = NamespacedKey(plugin, "${id}_shapeless")
        return ShapelessRecipe(key, item).apply {
            addIngredient(Material.IRON_INGOT)
            addIngredient(Material.COBBLESTONE)
            addIngredient(Material.GUNPOWDER)
            addIngredient(Material.GUNPOWDER)
        }
    }
}
