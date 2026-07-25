package com.github.ringoame196_s_mcPlugin

import org.bukkit.entity.Player

interface Gun {
    val firingRangeDistance: Double
    val damage: Double

    fun shot(player: Player)
    fun reload()
}
