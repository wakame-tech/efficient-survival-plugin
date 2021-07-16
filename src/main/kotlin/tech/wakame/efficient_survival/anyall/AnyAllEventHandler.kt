package tech.wakame.efficient_survival.anyall

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.lang.Integer.max
import java.util.*

object AnyAllEventHandler: Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val tool = event.player.inventory.itemInMainHand
        val block = event.block

        if (
                !(tool.type in Tools.Pickaxes && block.type in Blocks.Minings) &&
                !(tool.type in Tools.Axes && block.type in Blocks.Cuttings) &&
                !(tool.type in Tools.Shovels && block.type in Blocks.Diggings)
        ) return

        var limit = 100
        val type = block.type
        val dest = event.player.location

        val directions = arrayOf(
                BlockFace.DOWN,
                BlockFace.UP,
                BlockFace.SOUTH,
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.WEST
        )

        fun blockBreak(target: Block) {
            if (limit < 0 || target.type == Material.AIR)
                return

            when {
                // looting
                Enchantment.LOOT_BONUS_BLOCKS in tool.enchantments.keys -> {
                    val level = tool.enchantments[Enchantment.LOOT_BONUS_BLOCKS]!!
                    val amount = max(1, Random().nextInt(level + 2) - 1)
                    ItemStack(target.drops.first().type, amount)
                }
                // silk touch
                Enchantment.SILK_TOUCH in tool.enchantments.keys -> {
                    ItemStack(target.type, 1)
                }
                else -> {
                    target.drops.first()
                }
            }.let {
                dest.world?.dropItemNaturally(dest, it)
            }

            target.type = Material.AIR

            directions.forEach {
                val nextTarget = target.getRelative(it, 1)
                if (nextTarget.type == type) {
                    limit--
                    blockBreak(nextTarget)
                }
            }
        }

        blockBreak(block)
    }
}