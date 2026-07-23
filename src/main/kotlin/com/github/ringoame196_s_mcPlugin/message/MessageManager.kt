package com.github.ringoame196_s_mcPlugin.message

import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader

class MessageManager(
    private val plugin: JavaPlugin
) {

    private val fileName = "messages.yml"
    private val file = File(plugin.dataFolder, fileName)

    private lateinit var messages: YamlConfiguration
    private val warnedKeys = mutableSetOf<String>()

    init {
        // 初回のみ resources からコピー
        plugin.saveResource(fileName, false)
        reload()
    }

    /**
     * messages.yml を再読み込みし、
     * resources 側で追加されたキーを自動で追記します。
     */
    fun reload() {
        messages = YamlConfiguration.loadConfiguration(file)

        val defaults = plugin.getResource(fileName)?.use {
            YamlConfiguration.loadConfiguration(InputStreamReader(it, Charsets.UTF_8))
        } ?: return

        messages.setDefaults(defaults)
        messages.options().copyDefaults(true)
        messages.save(file)
    }

    /**
     * resources/messages.yml で強制上書きします。
     * 開発用。
     */
    fun overwriteMessages() {
        plugin.saveResource(fileName, true)
        reload()
    }

    fun contains(path: String): Boolean {
        return messages.contains(path)
    }

    fun get(
        path: String,
        vararg placeholders: Pair<String, String>
    ): String {

        if (!messages.contains(path)) {
            if (warnedKeys.add(path)) {
                plugin.logger.warning("Message key '$path' was not found.")
            }
            return path
        }

        var text = messages.getString(path).orEmpty()

        placeholders.forEach { (key, value) ->
            text = text.replace(key, value)
        }

        return ChatColor.translateAlternateColorCodes('&', text)
    }
}
