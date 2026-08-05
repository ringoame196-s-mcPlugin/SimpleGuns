package com.github.ringoame196_s_mcPlugin

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface StopUsing {
    fun onStopUsing(player: Player, gunItem: ItemStack)
}
