package tech.wakame.efficient_survival.virtualchest.presentation

import fr.minuskube.inv.SmartInventory
import tech.wakame.efficient_survival.virtualchest.VirtualChestConfig

object VirtualChestView {
    // TODO
    val INVENTORY = SmartInventory.builder()
        .id(VirtualChestConfig.virtualChestName)
        .provider(VirtualChestContentProvider())
        .size(6, 9)
        .title(VirtualChestConfig.virtualChestName)
        .build()
}