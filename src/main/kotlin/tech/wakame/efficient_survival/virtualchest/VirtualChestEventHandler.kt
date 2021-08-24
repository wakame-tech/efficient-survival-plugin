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
        /**
         * 現在のページ
         */
        var currentPageIndex = 0

        /**
         * 現在のパネルタイプ
         */
        var currentPanelType = VirtualChestPanelType.ByChest
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
            val panel = useCase.getVirtualChestPanel(currentPanelType)
            player.openInventory(panel[0])
            return
        }

        if (event.view.title != VirtualChestConfig.virtualChestName)
            return

        event.isCancelled = true

        if (!event.isLeftClick) {
            return
        }

        player.sendMessage(currentPageIndex.toString())
        useCase.clickIndex(currentPanelType, currentPageIndex, event.rawSlot)
            ?.let {
                player.closeInventory()
                player.openInventory(it)
            }
    }
}