package com.github.ringoame196_s_mcPlugin

import org.bukkit.entity.Player

interface Gun {
    val firingRangeDistance: Double
    val damage: Double
    val maxAmmo: Int
    val ammo: GunItem

    fun shot(player: Player)
    fun reload(player: Player)
}
