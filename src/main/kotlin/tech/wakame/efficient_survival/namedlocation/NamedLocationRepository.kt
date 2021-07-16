package tech.wakame.efficient_survival.namedlocation

import org.bukkit.Location
import org.bukkit.configuration.Configuration

interface INamedLocationRepository {
    fun save(locations: List<NamedLocation>)

    fun load(): List<NamedLocation>
}

/**
 *  make pair from key: K to not-null value: V that'll be let to transform
 */
inline fun <K, V : Any> Collection<K>.combineNotNull(crossinline transform: (K) -> V?): Map<K, V> {
    return this.mapNotNull {
        val v = transform(it)
        return@mapNotNull if (v is V) {
            Pair(it, v)
        } else {
            null
        }
    }.toMap()
}

class NamedLocationRepository(private val config: Configuration): INamedLocationRepository {
    /**
     * getElements
     */
    private inline fun <reified V : Any> getElements (path: String): MutableMap<String, V> {
        config.getConfigurationSection(path)?.let { section ->
            return section.getKeys(false)
                    .combineNotNull { config["$path.$it"] as? V }
                    .toMutableMap()
        }
        return mutableMapOf()
    }

    /**
     * setElements
     */
    private fun <V> setElements (path: String, data: Map<String, V>) {
        config.createSection(path)
        data.forEach { (k, v) -> config.set("$path.$k", v) }
    }

    override fun save(locations: List<NamedLocation>) {
        setElements("locations", locations.associateBy({ it.label }, { it.location }))
    }

    override fun load(): List<NamedLocation> {
        return getElements<Location>("locations")
            .map { NamedLocation(it.key, it.value) }
    }
}