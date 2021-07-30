package tech.wakame.efficient_survival.util

import org.bukkit.configuration.Configuration

/**
 * getElements
 */
inline fun <reified V : Any> Configuration.getElements(path: String): MutableMap<String, V> {
    getConfigurationSection(path)?.let { section ->
        return section.getKeys(false)
                .combineNotNull { this["$path.$it"] as? V }
                .toMutableMap()
    }
    return mutableMapOf()
}

/**
 * setElements
 */
fun <V> Configuration.setElements(path: String, data: Map<String, V>) {
    createSection(path)
    data.forEach { (k, v) -> set("$path.$k", v) }
}
