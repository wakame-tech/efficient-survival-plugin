package tech.wakame.efficient_survival.namedlocation

import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import tech.wakame.efficient_survival.util.CommandHandler

/**
 *  [Location] to easy string, such as "(X, Y, Z)"
 */
fun Location.inspect() = "(${x.toInt()}, ${y.toInt()}, ${z.toInt()})"

class NamedLocationEventHandler(private val namedLocationUseCase: INamedLocationUseCase): CommandHandler {
    /**
     * A map of command to handler
     */
    private val handlers: MutableMap<String, (CommandSender, Array<String>, Map<String, String?>) -> Boolean> = mutableMapOf()

    override val labels: Set<String> = handlers.keys

    init {
        handlers["addloc"] = ::registerLocation
        handlers["tpn"] = ::teleportToNamedLocation
        handlers["locs"] = ::listNamedLocations
    }

    override fun onCommand(sender: CommandSender, label: String, args: Array<out String>?): Boolean {
        return if (label in labels && args != null) {
            val (params, options) = args.toParamsAndOptions()
            handlers[label]!!.invoke(sender, params, options)
        } else {
            false
        }
    }

    /**
     * register a location where the player is.
     * @return result of command
     */
    private fun registerLocation(sender: CommandSender, params: Array<String>, options: Map<String, String?>): Boolean {
        if (sender !is Player)
            return false

        if (params.first().isEmpty()) {
            return false
        }

        namedLocationUseCase.registerLocation(params.first(), sender.location)
        sender.sendMessage("${sender.location.inspect()} has been set as ${params.first()}")
        return true
    }

    /**
     * tereport the player to named location.
     * @return result of command
     */
    private fun teleportToNamedLocation(sender: CommandSender, params: Array<String>, options: Map<String, String?>):Boolean {
        if (sender !is Player)
            return false

        val entry = namedLocationUseCase.getLocation(params.first()) ?: return false

        sender.teleport(entry.location)

        return true
    }

    /**
     * list named locations
     * @return result of command
     */
    private fun listNamedLocations(sender: CommandSender, params: Array<String>, options: Map<String, String?>):Boolean {
        namedLocationUseCase.getLocations()
                .forEach { (k, v) -> sender.sendMessage("$k: ${v.inspect()}") }

        return true
    }
}