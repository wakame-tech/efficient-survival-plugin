package tech.wakame.efficient_survival.anyall

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.lang.Integer.max
import tech.wakame.efficient_survival.util.inspect
import java.util.*

object AnyAllEventHandler : Listener {
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

        val diffs = arrayOf(
                Triple(-1, -1, -1),
                Triple(-1, -1, 0),
                Triple(-1, -1, 1),
                Triple(-1, 0, -1),
                Triple(-1, 0, 0),
                Triple(-1, 0, 1),
                Triple(-1, 1, -1),
                Triple(-1, 1, 0),
                Triple(-1, 1, 1),

                Triple(0, -1, -1),
                Triple(0, -1, 0),
                Triple(0, -1, 1),
                Triple(0, 0, -1),
                Triple(0, 0, 1),
                Triple(0, 1, -1),
                Triple(0, 1, 0),
                Triple(0, 1, 1),

                Triple(1, -1, -1),
                Triple(1, -1, 0),
                Triple(1, -1, 1),
                Triple(1, 0, -1),
                Triple(1, 0, 0),
                Triple(1, 0, 1),
                Triple(1, 1, -1),
                Triple(1, 1, 0),
                Triple(1, 1, 1)
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
                // get damage
                if (tool.hasItemMeta() && tool.itemMeta is Damageable) {
                    val unbreakableLevel = if (Enchantment.LOOT_BONUS_BLOCKS in tool.enchantments.keys) tool.enchantments[Enchantment.DURABILITY]!! else 0
                    val damagePercent = 100 / (unbreakableLevel + 1)

                    if (Random().nextInt(101) < damagePercent) {
                        tool.itemMeta = tool.itemMeta.apply {
                            (this as Damageable).damage -= 1
                        }
                    }
                }
                dest.world?.dropItemNaturally(dest, it)
            }

            target.type = Material.AIR

            diffs.forEach {
                val nextTarget = target.getRelative(it.first, it.second, it.third)
                if (nextTarget.type == type) {
                    event.player.sendMessage(nextTarget.location.inspect())
                    limit--
                    blockBreak(nextTarget)
                }
            }
        }

        blockBreak(block)
    }
}