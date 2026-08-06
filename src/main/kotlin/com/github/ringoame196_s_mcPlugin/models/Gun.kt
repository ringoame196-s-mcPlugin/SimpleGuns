package com.github.ringoame196_s_mcPlugin.models

import com.github.ringoame196_s_mcPlugin.managers.GunManager
import org.bukkit.entity.Player

interface Gun : GunItem {
    val firingRangeDistance: Double
    val damage: Double
    val ammoList: List<Ammo>
    val gunManager: GunManager

    fun shot(player: Player)
    fun reload(player: Player)
}
