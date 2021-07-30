package tech.wakame.efficient_survival.namedlocation

import org.bukkit.Location
import org.bukkit.configuration.Configuration
import tech.wakame.efficient_survival.util.getElements
import tech.wakame.efficient_survival.util.setElements

interface INamedLocationRepository {
    fun save(locations: List<NamedLocation>)

    fun load(): List<NamedLocation>
}

class NamedLocationRepository(private val config: Configuration) : INamedLocationRepository {
    override fun save(locations: List<NamedLocation>) {
        config.setElements("locations", locations.associateBy({ it.label }, { it.location }))
    }

    override fun load(): List<NamedLocation> {
        return config.getElements<Location>("locations")
                .map { NamedLocation(it.key, it.value) }
    }
}