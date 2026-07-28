package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.events.GunEvents
import com.github.ringoame196_s_mcPlugin.message.MessageManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    private val plugin = this
    override fun onEnable() {
        super.onEnable()
        GunManager.plugin = plugin
        val messageManager = MessageManager(plugin)

        val ammon = Ammo("弾", plugin)
        val pistol = Pistol("シンプルガン", plugin, ammon)

        val gunItemList = listOf(pistol, ammon)
        RecipeManager.registerRecipes(gunItemList)

        server.pluginManager.registerEvents(GunEvents(gunItemList), plugin)
        val command = getCommand("simpleguns")
        command!!.setExecutor(Command(gunItemList, messageManager))
    }
}
