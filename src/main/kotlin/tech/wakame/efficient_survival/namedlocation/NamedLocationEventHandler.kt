package tech.wakame.efficient_survival.namedlocation

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.wakame.efficient_survival.util.CommandHandler
import tech.wakame.efficient_survival.util.colored
import tech.wakame.efficient_survival.util.inspect

class NamedLocationEventHandler(private val namedLocationUseCase: INamedLocationUseCase): CommandHandler {
    /**
     * A map of command to handler
     */
    private val handlers: MutableMap<String, (CommandSender, Array<String>, Map<String, String?>) -> Boolean> = mutableMapOf()

    override val labels: Set<String> = handlers.keys

    init {
        handlers["addloc"] = ::registerLocation
        handlers["rmloc"] = ::removeNamedLocation
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

        if (params.isEmpty()) {
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

        if (params.isEmpty()) {
            return false
        }

        val entry = namedLocationUseCase.getLocation(params.first())

        if (entry == null) {
            sender.sendMessage("location ${params.first()} not found")
            return true
        }

        sender.teleport(entry.location)
        sender.sendMessage("tereported to ${params.first()}")
        return true
    }

    /**
     * unregister a location
     * @return result of command
     */
    private fun removeNamedLocation(sender: CommandSender, params: Array<String>, options: Map<String, String?>): Boolean {
        if (sender !is Player)
            return false

        if (params.isEmpty()) {
            return false
        }

        namedLocationUseCase.deleteLocation(params.first())
        sender.sendMessage("${params.first()} has been removed")
        return true
    }

    /**
     * list named locations
     * @return result of command
     */
    private fun listNamedLocations(sender: CommandSender, params: Array<String>, options: Map<String, String?>):Boolean {
        if (sender !is Player) return false
        val message = namedLocationUseCase.getLocations().map { namedLocation ->
            TextComponent().apply {
                addExtra(TextComponent("%-10s".format("${namedLocation.label}")))
                addExtra(TextComponent("%-15s".format("yellow{tp here}".colored())).apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("クリックしてテレポート")))
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p ${namedLocation.location.blockX} ${namedLocation.location.blockY} ${namedLocation.location.blockZ}")
                })
                addExtra("\n")
            }
        }.toTypedArray()

        sender.spigot().sendMessage(*message)

        return true
    }
}