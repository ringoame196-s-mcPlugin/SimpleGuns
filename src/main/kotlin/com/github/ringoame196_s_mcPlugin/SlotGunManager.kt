package com.github.ringoame196_s_mcPlugin

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotGunManager {
    fun makeGunItem(gun: GunItem): ItemStack {
        val item = GunManager.makeGunItem(gun)
        val meta = item.itemMeta
        meta.gun.selectSlot = 1
        item.setItemMeta(meta)
        return item
    }

    fun getGunSlots() {
    }

    fun next(hasSlotGun: HasSlotGun, gunItem: ItemStack, player: Player) {
        val meta = gunItem.itemMeta
        var selectSlot = meta.gun.selectSlot + 1
        if (selectSlot > hasSlotGun.slot) selectSlot = 1
        meta.gun.selectSlot = selectSlot
        gunItem.setItemMeta(meta)

        val message = "$selectSlot"
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(message))
        player.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 1f)
        player.inventory.setItemInMainHand(gunItem)
    }

    fun shot(player: Player, gunItem: GunItem, slot: Int) {
    }
}
