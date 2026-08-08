package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.ammos.IronAmmo
import com.github.ringoame196_s_mcPlugin.ammos.OpAmmo
import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.events.GunEvents
import com.github.ringoame196_s_mcPlugin.guns.Pistol
import com.github.ringoame196_s_mcPlugin.guns.Revolver
import com.github.ringoame196_s_mcPlugin.guns.Sniper
import com.github.ringoame196_s_mcPlugin.managers.ConfigManager
import com.github.ringoame196_s_mcPlugin.managers.GunItemManager
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

        // config.yml の初期生成
        saveDefaultConfig()
        val configManager = ConfigManager(plugin.config)
        GunItemManager.configManager = configManager

        val gunItemList = createGunItemList()
        registerRecipes(configManager, gunItemList)

        registerEvents(gunItemList)
        registerCommands(gunItemList)
    }

    private fun createGunItemList(): List<GunItem> {
        val ironAmmo = IronAmmo(plugin)
        val opAmmo = OpAmmo()
        val pistol = Pistol(plugin, listOf(ironAmmo, opAmmo))
        val revolver = Revolver(plugin, listOf(ironAmmo, opAmmo))
        val sniper = Sniper(plugin, listOf(ironAmmo, opAmmo))

        return listOf(
            pistol,
            revolver,
            sniper,
            ironAmmo,
            opAmmo
        )
    }

    private fun registerRecipes(configManager: ConfigManager, gunItemList: List<GunItem>) {
        if (configManager.isCraftingEnabled) {
            RecipeManager.registerRecipes(gunItemList)
        }
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
