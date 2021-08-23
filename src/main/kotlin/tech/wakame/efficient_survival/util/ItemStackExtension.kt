package tech.wakame.efficient_survival.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun ItemStack.renamed(name: String): ItemStack {
    itemMeta = itemMeta!!.run {
        setDisplayName(name)
        this
    }
    return this
}

fun ItemStack.lored(description: List<String>): ItemStack {
    itemMeta = itemMeta!!.run {
        lore = description
        this
    }
    return this
}

fun List<ItemStack?>.summary(): List<Pair<Material, Int>> {
    return this
        .asSequence()
        .filterNotNull()
        .groupBy { it.type }
        .map { (m, iss) -> m to iss.map { it.amount }.sum() }
        .sortedByDescending { it.second }
}
