package tech.wakame.efficient_survival.virtualchest

import org.bukkit.Location
import org.bukkit.configuration.Configuration
import tech.wakame.efficient_survival.namedlocation.NamedLocation
import tech.wakame.efficient_survival.util.getElements
import tech.wakame.efficient_survival.util.setElements

interface IVirtualChestRepository {
    fun save(locations: List<NamedLocation>)

    fun load(): List<NamedLocation>
}

class VirtualChestRepository(private val config: Configuration) : IVirtualChestRepository {
    override fun save(locations: List<NamedLocation>) {
        config.setElements("chests", locations.associateBy({ it.label }, { it.location }))
    }

    override fun load(): List<NamedLocation> {
        return config.getElements<Location>("chests")
            .map { NamedLocation(it.key, it.value) }
    }
}
