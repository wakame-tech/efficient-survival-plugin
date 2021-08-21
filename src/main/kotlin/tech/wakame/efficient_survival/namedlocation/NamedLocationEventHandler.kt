package tech.wakame.efficient_survival.namedlocation

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import tech.wakame.efficient_survival.virtualchest.VirtualChestUseCase

class NamedLocationEventHandler(
    private val namedLocationUseCase: INamedLocationUseCase,
    private val virtualLocationUseCase: VirtualChestUseCase
): Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        namedLocationUseCase.registerLocation(event.entity.displayName, event.entity.location)
        virtualLocationUseCase.addVirtualInventory(event.entity.displayName, event.entity.inventory)
    }
}