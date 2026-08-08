package com.github.ringoame196_s_mcPlugin.managers

import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration

class ConfigManager(private val config: FileConfiguration) {
    val isCraftingEnabled: Boolean
        get() = config.getBoolean("enable-crafting", true)

    fun getDisplayName(id: String): String {
        val rawName = config.getString("names.$id") ?: id
        return ChatColor.translateAlternateColorCodes('&', rawName)
    }
}
