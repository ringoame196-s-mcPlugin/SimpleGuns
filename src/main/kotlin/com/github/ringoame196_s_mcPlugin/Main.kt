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

        val ironAmmo = IronAmmo("鉄弾", plugin)
        val opAmmo = OpAmmo("OP弾")
        val pistol = Pistol("シンプルガン", plugin, listOf(ironAmmo, opAmmo))

        val gunItemList = listOf(
            pistol,
            ironAmmo,
            opAmmo
        )
        RecipeManager.registerRecipes(gunItemList)

        server.pluginManager.registerEvents(GunEvents(gunItemList), plugin)
        val command = getCommand("simpleguns")
        command!!.setExecutor(Command(gunItemList, messageManager))
    }
}
