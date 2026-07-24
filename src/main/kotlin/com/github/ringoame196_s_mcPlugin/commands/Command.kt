package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.Gun
import com.github.ringoame196_s_mcPlugin.message.MessageKey
import com.github.ringoame196_s_mcPlugin.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Command(gunList: List<Gun>, private val messageManager: MessageManager) : CommandExecutor, TabCompleter {
    private val gunMap: Map<String, Gun> = gunList.associateBy { it.id }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false
        val subCommand = args[0]
        return when (subCommand) {
            CommandConst.GIVE_COMMAND -> give(sender, args)
            else -> false
        }
    }

    private fun give(sender: CommandSender, args: Array<out String>): Boolean {
        val indexGunID = 1
        val indexTarget = 2

        if (args.size < indexGunID + 1) return false
        val gunId = args[indexGunID]
        val gun = gunMap[gunId]?.gun

        if (gun == null) {
            val msg = messageManager.get(MessageKey.GUN_NOT_FOUND, "%gun%" to gunId)
            sender.sendMessage(msg)
            return true
        }

        val targets = mutableListOf<Player>()
        if (args.size > indexTarget) {
            val selector = args[indexTarget]
            val selectedPlayers = Bukkit.selectEntities(sender, selector)
                .filterIsInstance<Player>()
            targets.addAll(selectedPlayers)
        } else {
            if (sender is Player) {
                targets.add(sender)
            }
        }

        for (player in targets) {
            player.inventory.addItem(gun)
        }

        return true
    }

    override fun onTabComplete(commandSender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf(CommandConst.GIVE_COMMAND)
            2 -> when (args[0]) {
                CommandConst.GIVE_COMMAND -> gunMap.keys.toMutableList()
                else -> mutableListOf()
            }
            else -> mutableListOf()
        }
    }
}
