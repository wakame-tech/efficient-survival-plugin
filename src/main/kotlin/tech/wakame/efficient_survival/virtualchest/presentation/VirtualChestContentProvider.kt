package tech.wakame.efficient_survival.virtualchest.presentation

import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import fr.minuskube.inv.content.SlotIterator
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class VirtualChestContentProvider : InventoryProvider {
    override fun init(player: Player?, contents: InventoryContents?) {
        if (player == null || contents == null) return

        val pagination = contents.pagination()
        val data = (0..100).toList()
        val items = (0..100)
            .map {
                ClickableItem.of(ItemStack(Material.DIAMOND)) {
                    player.sendMessage("clicked ${pagination.page} ${it.rawSlot}")
                }
            }
            .toTypedArray()
        pagination.setItems(*items)
        pagination.setItemsPerPage(54 - 9)
        pagination.addToIterator(
            contents.newIterator(
                SlotIterator.Type.HORIZONTAL, 0, 0
            )
        )
        contents.set(5, 0, ClickableItem.of(ItemStack(Material.ARROW)) {
            VirtualChestView.INVENTORY.open(player, pagination.previous().page)
        })
        contents.set(5, 8, ClickableItem.of(ItemStack(Material.ARROW)) {
            VirtualChestView.INVENTORY.open(player, pagination.next().page)
        })
    }

    override fun update(player: Player?, contents: InventoryContents?) {}
}