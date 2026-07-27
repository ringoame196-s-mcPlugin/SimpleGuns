package com.github.ringoame196_s_mcPlugin

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Gun {
    val firingRangeDistance: Double
    val damage: Double
    val maxAmmon: Int

    fun shot(player: Player, gunItem: ItemStack)
    fun reload()
}
