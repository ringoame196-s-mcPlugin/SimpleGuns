package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.events.GunEvents
import com.github.ringoame196_s_mcPlugin.message.MessageManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        // 外部クラスから Main.plugin でアクセスできるようにする
        lateinit var plugin: Main
            private set
    }

    override fun onEnable() {
        super.onEnable()
        plugin = this

        val messageManager = MessageManager(plugin)

        val ironAmmo = IronAmmo("鉄弾", plugin)
        val opAmmo = OpAmmo("OP弾")
        val pistol = Pistol("シンプルガン", plugin, listOf(ironAmmo, opAmmo))
        val revolver = Revolver("リボルガン", plugin, listOf(ironAmmo, opAmmo))
        val sniper = Sniper("スナイパー", plugin, listOf(ironAmmo, opAmmo))

        val gunItemList = listOf(
            pistol,
            revolver,
            sniper,
            ironAmmo,
            opAmmo
        )
        RecipeManager.registerRecipes(gunItemList)

        server.pluginManager.registerEvents(GunEvents(gunItemList), plugin)
        val command = getCommand("simpleguns")
        command!!.setExecutor(Command(gunItemList, messageManager))
    }
}
