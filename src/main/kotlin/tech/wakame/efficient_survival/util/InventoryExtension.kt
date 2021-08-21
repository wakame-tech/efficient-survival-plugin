package tech.wakame.efficient_survival.util

import org.bukkit.Material
import org.bukkit.inventory.Inventory

fun Inventory.summary(): List<Pair<Material, Int>> {
    return this
        .asSequence()
        .filterNotNull()
        .groupBy { it.type }
        .map { (m, iss) -> m to iss.map { it.amount }.sum() }
        .sortedByDescending { it.second }
}