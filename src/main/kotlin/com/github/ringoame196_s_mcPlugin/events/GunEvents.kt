package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.models.Gun
import com.github.ringoame196_s_mcPlugin.models.GunItem
import com.github.ringoame196_s_mcPlugin.interfaces.LeftClickable
import com.github.ringoame196_s_mcPlugin.interfaces.RightClickable
import com.github.ringoame196_s_mcPlugin.interfaces.StopUsing
import com.github.ringoame196_s_mcPlugin.utils.gun
import io.papermc.paper.event.player.PlayerStopUsingItemEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class GunEvents(gunItemList: List<GunItem>) : Listener {
    private val gunItemMap: Map<String, GunItem> = gunItemList.associateBy { it.id }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val gunItem = e.item ?: return
        val gun = getGun(e.item ?: return)
        if (gun !is Gun) return
        if (e.hand != EquipmentSlot.HAND) return

        when (e.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                if (gun is RightClickable) {
                    gun.onRightClick(player, gunItem)
                    e.isCancelled = true
                }
            }
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                if (gun is LeftClickable) {
                    gun.onLeftClick(player, gunItem)
                    e.isCancelled = true
                }
            }
            else -> {}
        }
    }

    @EventHandler
    fun onPlayerStopUsingItem(e: PlayerStopUsingItemEvent) {
        val player = e.player
        val gunItem = e.item ?: return
        val gun = getGun(e.item ?: return)
        if (gun !is Gun) return
        if (gun is StopUsing) {
            gun.onStopUsing(player, gunItem)
        }
    }

    @EventHandler
    fun onPlayerItemHeld(e: PlayerItemHeldEvent) {
        val player = e.player
        val newSlot = e.newSlot
        val gunItem = player.inventory.getItem(newSlot) ?: return
        val gun = getGun(gunItem)
        if (gun is Gun) {
            gun.gunManager.displayAmmo(player, gunItem)
        }
    }

    private fun getGun(item: ItemStack): Gun? {
        val meta = item.itemMeta ?: return null
        val gunId = meta.gun.id ?: return null
        return gunItemMap[gunId] as? Gun
    }
}
