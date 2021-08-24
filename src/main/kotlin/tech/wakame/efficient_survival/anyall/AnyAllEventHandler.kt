package tech.wakame.efficient_survival.anyall

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.ExpBottleEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import tech.wakame.efficient_survival.util.inspect
import java.lang.Integer.max
import java.util.*

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

@Deprecated(message = "use Block.getDrops(tool)")
fun blockBreakWithTool(target: Block, tool: ItemStack, dest: Location) {
    when {
        // looting
        Enchantment.LOOT_BONUS_BLOCKS in tool.enchantments.keys -> {
            val level = tool.enchantments.getOrDefault(Enchantment.LOOT_BONUS_BLOCKS, 0)
            val amount = max(1, Random().nextInt(level + 2) - 1)
            target.drops.map { ItemStack(it.type, amount) }
        }
        // silk touch
        Enchantment.SILK_TOUCH in tool.enchantments.keys -> {
            listOf(ItemStack(target.type, 1))
        }
        else -> {
            target.drops
        }
    }.let { items ->
        // get damage
        if (tool.hasItemMeta() && tool.itemMeta is Damageable) {
            val unbreakableLevel = tool.enchantments.getOrDefault(Enchantment.DURABILITY, 0)
            val damagePercent = 100 / (unbreakableLevel + 1)
            if (Random().nextInt(101) < damagePercent) {
                tool.itemMeta = tool.itemMeta.apply {
                    (this as Damageable).damage -= 1
                }
            }
        }

        items.forEach {
            dest.world?.dropItemNaturally(dest, it)
        }
    }

    target.type = Material.AIR
}

class AnyAllEventHandler : Listener {
    @EventHandler
    fun warnToolDurabilityLow(event: BlockBreakEvent) {
        val tool = event.player.inventory.itemInMainHand
        if (tool.itemMeta !is Damageable || !Tools.Tools.contains(tool.type)) {
            return
        }
        val durability = tool.type.maxDurability - (tool.itemMeta as Damageable).damage
        if (durability < 10) {
            event.player.sendMessage("[WARN] this tool will break soon. durability: $durability")
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val tool = event.player.inventory.itemInMainHand
        val block = event.block
        var cnt = 0

        if (
            !(tool.type in Tools.Pickaxes && block.type in Blocks.Minings) &&
            !(tool.type in Tools.Axes && block.type in Blocks.Cuttings) &&
            !(tool.type in Tools.Shovels && block.type in Blocks.Diggings) &&
            !(block.type in Blocks.Harvests.keys)
        ) return

        var limit = 500
        val type = block.type
        val dest = event.player.location

        fun blockBreak(target: Block) {
            if (limit < cnt || target.type.isAir)
                return

            target.getDrops(tool).forEach {
                target.world.dropItemNaturally(dest, it)
            }
            target.type = Material.AIR
//            blockBreakWithTool(target, tool, dest)

            diffs.forEach {
                val nextTarget = target.getRelative(it.first, it.second, it.third)
                if (nextTarget.type == type) {
                    cnt++
                    blockBreak(nextTarget)
                }
            }
        }

        blockBreak(block)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.block.type !in Blocks.Harvests.keys) {
            return
        }

        val blockType = event.block.type
        val limit = 500
        var cnt = 0
        val queue = LinkedList<Block>()
        queue.add(event.block)

        while (queue.isNotEmpty()) {
            if (limit < cnt)
                return

            val target = queue.removeFirst()
            target.type = blockType
            cnt++

            arrayOf(BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST).forEach {
                val next = target.getRelative(it)
                val isOnFarmland = next.getRelative(BlockFace.DOWN).type == Material.FARMLAND
                if (next.type.isAir && isOnFarmland) {
                    queue.push(next)
                }
            }
        }
    }
}