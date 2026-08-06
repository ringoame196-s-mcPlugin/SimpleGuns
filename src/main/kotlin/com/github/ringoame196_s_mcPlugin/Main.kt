package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.ammos.IronAmmo
import com.github.ringoame196_s_mcPlugin.ammos.OpAmmo
import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.events.GunEvents
import com.github.ringoame196_s_mcPlugin.guns.Pistol
import com.github.ringoame196_s_mcPlugin.guns.Revolver
import com.github.ringoame196_s_mcPlugin.guns.Sniper
import com.github.ringoame196_s_mcPlugin.managers.RecipeManager
import com.github.ringoame196_s_mcPlugin.message.MessageManager
import com.github.ringoame196_s_mcPlugin.models.GunItem
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

        val gunItemList = createGunItemList()
        RecipeManager.registerRecipes(gunItemList)

        registerEvents(gunItemList)
        registerCommands(gunItemList)
    }

    private fun createGunItemList(): List<GunItem> {
        val ironAmmo = IronAmmo("鉄弾", plugin)
        val opAmmo = OpAmmo("OP弾")
        val pistol = Pistol("シンプルガン", plugin, listOf(ironAmmo, opAmmo))
        val revolver = Revolver("リボルガン", plugin, listOf(ironAmmo, opAmmo))
        val sniper = Sniper("スナイパー", plugin, listOf(ironAmmo, opAmmo))

        return listOf(
            pistol,
            revolver,
            sniper,
            ironAmmo,
            opAmmo
        )
    }

    private fun registerEvents(gunItemList: List<GunItem>) {
        server.pluginManager.registerEvents(GunEvents(gunItemList), plugin)
    }

    private fun registerCommands(gunItemList: List<GunItem>) {
        val messageManager = MessageManager(plugin)
        val command = getCommand("simpleguns")
        command?.setExecutor(Command(gunItemList, messageManager))
    }
}
