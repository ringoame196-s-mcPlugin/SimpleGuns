package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.models.GunItem
import com.github.ringoame196_s_mcPlugin.message.MessageKey
import com.github.ringoame196_s_mcPlugin.message.MessageManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Command(gunList: List<GunItem>, private val messageManager: MessageManager) : CommandExecutor, TabCompleter {
    private val gunItemMap: Map<String, GunItem> = gunList.associateBy { it.id }

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
        val indexAmount = 2
        val indexTarget = 3

        if (args.size < indexGunID + 1) return false
        val gunId = args[indexGunID]
        val baseItem = gunItemMap[gunId]?.item

        if (baseItem == null) {
            val noFoundMsg = messageManager.get(MessageKey.GUN_NOT_FOUND, "%gun%" to gunId)
            sender.sendMessage(noFoundMsg)
            return true
        }
        val item = baseItem.clone()
        val amount: Int = if (args.size > indexAmount) {
            args[indexAmount].toInt()
        } else {
            1
        }
        item.amount = amount

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
            player.inventory.addItem(item)
        }

        val pairs = arrayOf(
            "'%gun%'" to gunId,
            "'%amount%'" to amount.toString(),
            "'%size%'" to targets.size.toString()
        )
        val giveMsg = messageManager.get(MessageKey.GIVE_MESSAGE, *pairs)
        sender.sendMessage(giveMsg)

        return true
    }

    override fun onTabComplete(commandSender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf(CommandConst.GIVE_COMMAND)
            2 -> when (args[0]) {
                CommandConst.GIVE_COMMAND -> gunItemMap.keys.toMutableList()
                else -> mutableListOf()
            }
            3 -> when (args[0]) {
                CommandConst.GIVE_COMMAND -> mutableListOf("[<count>]")
                else -> mutableListOf()
            }
            4 -> when (args[0]) {
                CommandConst.GIVE_COMMAND -> (Bukkit.getOnlinePlayers().map { it.name } + "@a" + "@p" + "@r" + "@s").toMutableList()
                else -> mutableListOf()
            }
            else -> mutableListOf()
        }
    }
}
