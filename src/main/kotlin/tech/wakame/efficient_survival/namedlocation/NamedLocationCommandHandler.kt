package tech.wakame.efficient_survival.namedlocation

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.wakame.efficient_survival.util.CommandHandler
import tech.wakame.efficient_survival.util.colored
import tech.wakame.efficient_survival.util.inspect
import tech.wakame.efficient_survival.util.toParamsAndOptions

class NamedLocationCommandHandler(
    private val namedLocationUseCase: INamedLocationUseCase
    ) : CommandHandler() {

    init {
        handlers["nl"] = ::nl
    }

    private fun nl(sender: CommandSender, params: Array<String>, options: Map<String, String?>): Boolean {
        if (sender !is Player)
            return false

        return when {
            params.size == 2 && params[0] == "tp" -> teleportToNamedLocation(sender, params[1])
            params.size == 2 && params[0] == "add" -> registerLocation(sender, params[1])
            params.size == 2 && params[0] == "rm" -> removeNamedLocation(sender, params[1])
            params.size == 1 && params[0] == "ls" -> listNamedLocations(sender)
            else -> false
        }
    }

    private fun registerLocation(player: Player, label: String): Boolean {
        namedLocationUseCase.registerLocation(label, player.location)
        player.sendMessage("${player.location.inspect()} has been set as $label")
        return true
    }

    private fun teleportToNamedLocation(player: Player, label: String): Boolean {
        val entry = namedLocationUseCase.getLocation(label)

        if (entry == null) {
            player.sendMessage("location $label not found")
            return true
        }

        player.teleport(entry.location)
        player.sendMessage("tp to $label")
        return true
    }

    private fun removeNamedLocation(player: Player, label: String): Boolean {
        namedLocationUseCase.deleteLocation(label)
        player.sendMessage("$label has been removed")
        return true
    }

    private fun listNamedLocations(player: Player): Boolean {
        val message = namedLocationUseCase.getLocations().map { namedLocation ->
            TextComponent().apply {
                addExtra(TextComponent("%-10s".format("${namedLocation.label}")))
                addExtra(TextComponent("%-20s".format("@${namedLocation.location.world?.name ?: "---"} ${namedLocation.location.inspect()}")))
                addExtra(TextComponent("%-15s".format("yellow{tp here}".colored())).apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("クリックしてテレポート")))

                    val world = when (namedLocation.location.world?.name) {
                        "world" -> "minecraft:overworld"
                        "world_nether" -> "minecraft:the_nether"
                        "world_end" -> "minecraft:the_end"
                        else -> return false
                    }
                    val coords = "${namedLocation.location.blockX} ${namedLocation.location.blockY} ${namedLocation.location.blockZ}"
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/execute in $world run tp $coords")
                })
                addExtra("\n")
            }
        }.toTypedArray()

        player.spigot().sendMessage(*message)

        return true
    }
}