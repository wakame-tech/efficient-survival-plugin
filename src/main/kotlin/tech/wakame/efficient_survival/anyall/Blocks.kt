package tech.wakame.efficient_survival.anyall

import org.bukkit.Material

object Blocks {
    val Minings = arrayOf(
            // ores
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.GOLD_ORE,
            Material.LAPIS_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.REDSTONE_ORE,
            Material.NETHER_GOLD_ORE,
            // other
            Material.GLOWSTONE,
            Material.OBSIDIAN
    )

    val Cuttings = arrayOf(
            // logs
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG
    )

    val Diggings = arrayOf(
            Material.GRAVEL,
            Material.SOUL_SAND,
            Material.CLAY
    )
}