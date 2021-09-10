package tech.wakame.efficient_survival

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.efficient_survival.anyall.AnyAllEventHandler
import tech.wakame.efficient_survival.caveanalyzer.CaveAnalyzerCommandHandler
import tech.wakame.efficient_survival.namedlocation.*
import tech.wakame.efficient_survival.portableexp.PortableExpCommandHandler
import tech.wakame.efficient_survival.virtualchest.VirtualChestCommandHandler
import tech.wakame.efficient_survival.virtualchest.VirtualChestEventHandler
import tech.wakame.efficient_survival.virtualchest.VirtualChestRepository
import tech.wakame.efficient_survival.virtualchest.VirtualChestUseCase

class EfficientSurvival : JavaPlugin() {
    private val anyAllEventHandler = AnyAllEventHandler()

    private val namedLocationUseCase: INamedLocationUseCase = NamedLocationUseCase(NamedLocationRepository(config))
    private val namedLocationCommandHandler = NamedLocationCommandHandler(namedLocationUseCase)

    private val caveAnalyzerCommandHandler = CaveAnalyzerCommandHandler()

    private val virtualChestUseCase = VirtualChestUseCase(VirtualChestRepository(config))
    private val virtualChestCommandHandler = VirtualChestCommandHandler(virtualChestUseCase)
    private val virtualChestEventHandler = VirtualChestEventHandler(virtualChestUseCase)

    private val namedLocationEventHandler = NamedLocationEventHandler(namedLocationUseCase, virtualChestUseCase)

    private val portableExpCommandHandler = PortableExpCommandHandler()

    companion object {
        lateinit var plugin: JavaPlugin
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(anyAllEventHandler, this)
        server.pluginManager.registerEvents(virtualChestEventHandler, this)
        server.pluginManager.registerEvents(namedLocationEventHandler, this)
    }

    private fun loadConfig() {
        namedLocationUseCase.loadAll()
        virtualChestUseCase.loadAll()
    }

    override fun saveConfig() {
        namedLocationUseCase.saveAll()
        virtualChestUseCase.saveAll()
        super.saveConfig()
    }

    override fun onEnable() {
        server.broadcastMessage("$name ${server.version}")
//        plugin = this
//        registerEvents()
//        loadConfig()
    }

    override fun onDisable() {
        saveConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return when (label) {
            in namedLocationCommandHandler.labels -> namedLocationCommandHandler.onCommand(sender, label, args)
            in caveAnalyzerCommandHandler.labels -> caveAnalyzerCommandHandler.onCommand(sender, label, args)
            in virtualChestCommandHandler.labels -> virtualChestCommandHandler.onCommand(sender, label, args)
            in portableExpCommandHandler.labels -> portableExpCommandHandler.onCommand(sender, label, args)
            else -> super.onCommand(sender, command, label, args)
        }
    }
}