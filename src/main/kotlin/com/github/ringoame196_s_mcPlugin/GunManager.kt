package com.github.ringoame196_s_mcPlugin

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class GunManager {

    protected abstract fun displayAmmo(player: Player, gunItem: ItemStack)
    protected abstract fun shot(player: Player, gun: Gun)

    fun hitDirection(player: Player) {
        val sound = Sound.ENTITY_ARROW_HIT_PLAYER
        player.playSound(player.location, sound, 1f, 1f)
    }
}
