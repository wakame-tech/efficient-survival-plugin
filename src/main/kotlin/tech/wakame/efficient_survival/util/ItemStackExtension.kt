package tech.wakame.efficient_survival.util

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
