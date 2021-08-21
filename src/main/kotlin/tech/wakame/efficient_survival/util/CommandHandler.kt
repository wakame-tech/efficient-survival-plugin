package tech.wakame.efficient_survival.util

import org.bukkit.command.CommandSender

open class CommandHandler {
    open val handlers: MutableMap<String, (CommandSender, Array<String>, Map<String, String?>) -> Boolean> = mutableMapOf()

    /**
     * available commands
     */
    open val labels: Set<String> = handlers.keys

    open fun onCommand(sender: CommandSender, label: String, args: Array<out String>?): Boolean {
        return if (label in labels && args != null) {
            val (params, options) = args.toParamsAndOptions()
            handlers[label]!!.invoke(sender, params, options)
        } else {
            false
        }
    }
}