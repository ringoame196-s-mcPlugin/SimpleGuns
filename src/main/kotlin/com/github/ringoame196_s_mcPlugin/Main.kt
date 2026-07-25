package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.events.Events
import com.github.ringoame196_s_mcPlugin.message.MessageManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    private val plugin = this
    override fun onEnable() {
        super.onEnable()
        GunManager.plugin = plugin
        val messageManager = MessageManager(plugin)
        val gunList = listOf<GunItem>(SimpleGun("シンプルガン", plugin), Ammo("弾", plugin))
        RecipeManager.registerRecipes(gunList)

        server.pluginManager.registerEvents(Events(), plugin)
        val command = getCommand("simpleguns")
        command!!.setExecutor(Command(gunList, messageManager))
    }
}
