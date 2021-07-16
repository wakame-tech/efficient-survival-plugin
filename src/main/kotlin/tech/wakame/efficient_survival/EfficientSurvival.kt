package tech.wakame.efficient_survival

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.efficient_survival.anyall.AnyAllEventHandler
import tech.wakame.efficient_survival.namedlocation.INamedLocationUseCase
import tech.wakame.efficient_survival.namedlocation.NamedLocationEventHandler
import tech.wakame.efficient_survival.namedlocation.NamedLocationRepository
import tech.wakame.efficient_survival.namedlocation.NamedLocationUseCase

class EfficientSurvival : JavaPlugin() {
    private val namedLocationUseCase: INamedLocationUseCase = NamedLocationUseCase(NamedLocationRepository(config))
    private val namedLocationEventHandler = NamedLocationEventHandler(namedLocationUseCase)

    override fun onEnable() {
        logger.info("[EfficientSurvival]")
        server.pluginManager.registerEvents(AnyAllEventHandler, this)

        namedLocationUseCase.loadAll()
    }

    override fun onDisable() {
        namedLocationUseCase.saveAll()
        saveConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return when (label) {
            in namedLocationEventHandler.labels -> namedLocationEventHandler.onCommand(sender, label, args)
            else -> super.onCommand(sender, command, label, args)
        }
    }
}