package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.GunItem
import com.github.ringoame196_s_mcPlugin.GunManager
import com.github.ringoame196_s_mcPlugin.LeftClickable
import com.github.ringoame196_s_mcPlugin.RightClickable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class GunEvents(gunItemList: List<GunItem>) : Listener {
    private val gunItemMap: Map<String, GunItem> = gunItemList.associateBy { it.id }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val gunItem = e.item ?: return
        val gunId = GunManager.getGunId(gunItem) ?: return
        val gun = gunItemMap[gunId] ?: return

        when (e.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                if (gun is RightClickable) {
                    gun.onRightClick(player)
                }
            }
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                if (gun is LeftClickable) {
                    gun.onLeftClick(player)
                }
            }
            else -> {}
        }
    }
}
