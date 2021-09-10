package tech.wakame.efficient_survival.virtualchest

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.wakame.efficient_survival.util.CommandHandler
import tech.wakame.efficient_survival.util.toParamsAndOptions
import tech.wakame.efficient_survival.virtualchest.presentation.VirtualChestView

class VirtualChestCommandHandler(private val useCase: VirtualChestUseCase) : CommandHandler() {
    init {
        handlers["vc"] = ::vc
    }

    override fun onCommand(sender: CommandSender, label: String, args: Array<out String>?): Boolean {
        return if (label in labels && args != null) {
            val (params, options) = args.toParamsAndOptions()
            handlers[label]!!.invoke(sender, params, options)
        } else {
            false
        }
    }

    private fun open(player: Player): Boolean {
        VirtualChestView.INVENTORY.open(player)
        return true
    }

    private fun vc(sender: CommandSender, params: Array<String>, options: Map<String, String?>): Boolean {
        if (sender !is Player)
            return false

        return when {
            params.size == 1 && params[0] == "open" -> open(sender)
            else -> false
        }
    }
}

