package tech.wakame.efficient_survival.virtualchest

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.DoubleChestInventory
import tech.wakame.efficient_survival.util.colored
import tech.wakame.efficient_survival.util.inspect


class VirtualChestEventHandler(private val useCase: VirtualChestUseCase) : Listener {
    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (event.inventory is DoubleChestInventory) {
            val loc = event.inventory.location ?: return
            val res = useCase.addChestLocation(loc.inspect(), loc)
            if (res) {
                event.player.sendMessage("[VirtualChest] registered chest at yellow{${loc.inspect()}}".colored())
            }
        }
    }
}