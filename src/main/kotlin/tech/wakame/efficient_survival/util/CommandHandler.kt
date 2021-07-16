package tech.wakame.efficient_survival.util

import org.bukkit.command.CommandSender

interface CommandHandler {
    /**
     * available commands
     */
    val labels: Set<String>

    fun onCommand(sender: CommandSender, label: String, args: Array<out String>?): Boolean
}