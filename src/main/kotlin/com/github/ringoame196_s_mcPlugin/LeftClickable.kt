package com.github.ringoame196_s_mcPlugin

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface LeftClickable {
    fun onLeftClick(player: Player, gunItem: ItemStack)
}
