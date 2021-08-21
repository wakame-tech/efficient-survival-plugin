package tech.wakame.efficient_survival.virtualchest

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.DoubleChestInventory
import org.bukkit.permissions.Permission
import tech.wakame.efficient_survival.EfficientSurvival
import tech.wakame.efficient_survival.util.inspect
import java.lang.Integer.max
import java.lang.Math.min

class VirtualChestEventHandler(private val useCase: VirtualChestUseCase) : Listener {
    companion object {
        var currentPageIndex = 0
    }

    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (event.inventory is DoubleChestInventory) {
            val loc = event.inventory.location ?: return
            val res = useCase.addChestLocation(loc.inspect(), loc)
            if (res) {
                event.player.sendMessage("[VirtualChest] register chest")
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) {
            return
        }
        val player = event.whoClicked as Player

        if (event.clickedInventory == null) {
            val panel = useCase.getVirtualChestPanel()
            player.openInventory(panel[0])
        }

        if (event.view.title != VirtualChestConfig.virtualChestName)
            return

        when (event.rawSlot) {
            in 0 until 45 -> {
                if (event.slot !in event.view.topInventory.toList().filterNotNull().indices)
                    return

                val chestKey = event.view.topInventory.getItem(event.slot)!!.itemMeta?.displayName ?: return
                player.sendMessage(chestKey)
                useCase.getInventory(chestKey)
                    ?.let { player.openInventory(it) }
            }
            VirtualChestConfig.prevIndex -> {
                val panel = useCase.getVirtualChestPanel()
                currentPageIndex = max(0, currentPageIndex - 1)
                player.openInventory(panel[currentPageIndex])
            }
            VirtualChestConfig.nextIndex -> {
                val panel = useCase.getVirtualChestPanel()
                currentPageIndex = min(panel.lastIndex, currentPageIndex + 1)
                player.openInventory(panel[currentPageIndex])
            }
        }


    }
}