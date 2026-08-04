package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.Gun
import com.github.ringoame196_s_mcPlugin.GunItem
import com.github.ringoame196_s_mcPlugin.LeftClickable
import com.github.ringoame196_s_mcPlugin.RightClickable
import com.github.ringoame196_s_mcPlugin.gun
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import kotlin.collections.get

class GunEvents(gunItemList: List<GunItem>) : Listener {
    private val gunItemMap: Map<String, GunItem> = gunItemList.associateBy { it.id }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val gunItem = e.item ?: return
        val gunId = gunItem.itemMeta.gun.id
        val gun = gunItemMap[gunId] ?: return
        if (gun !is Gun) return
        e.isCancelled = true
        if (e.hand != EquipmentSlot.HAND) return

        when (e.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                if (gun is RightClickable) {
                    gun.onRightClick(player, gunItem)
                }
            }
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                if (gun is LeftClickable) {
                    gun.onLeftClick(player, gunItem)
                }
            }
            else -> {}
        }
    }

    @EventHandler
    fun onPlayerItemHeld(e: PlayerItemHeldEvent) {
        val player = e.player
        val newSlot = e.newSlot
        val item = player.inventory.getItem(newSlot) ?: return
        val gunId = item.itemMeta.gun.id
        val gun = gunItemMap[gunId] ?: return
        if (gun is Gun) {
            gun.gunManager.displayAmmo(player, item)
        }
    }

    private fun getGun(item: ItemStack): Gun? {
        val meta = item.itemMeta ?: return null
        val gunId = meta.gun.id ?: return null
        return gunItemMap[gunId] as? Gun
    }
}
