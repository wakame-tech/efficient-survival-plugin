package tech.wakame.efficient_survival.virtualchest

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import tech.wakame.efficient_survival.namedlocation.NamedLocation
import tech.wakame.efficient_survival.util.*

class VirtualChestUseCase(private val repo: IVirtualChestRepository) {
    private val chestLocations: MutableMap<String, Location> = mutableMapOf()
    private val virtualChests: MutableMap<String, Inventory> = mutableMapOf()

    fun saveAll() {
        chestLocations.entries.toTypedArray()
            .map { NamedLocation(it.key, it.value) }
            .let { repo.save(it) }
    }

    fun loadAll() {
        repo.load()
            .forEach {
                chestLocations[it.label] = it.location
            }
    }

    fun getInventories(): Map<String, Inventory> {
        return chestLocations
            .mapValuesNotNull {
                (it.value.block.state as? InventoryHolder)?.inventory
            }
    }

    fun addVirtualInventory(label: String, inventory: Inventory): Boolean {
        if (label in virtualChests.keys) {
            return false
        }
        virtualChests[label] = inventory
        return true
    }

    fun addChestLocation(label: String, location: Location): Boolean {
        if (label in chestLocations.keys) {
            return false
        }
        chestLocations[label] = location
        return true
    }

    /**
     * インベントリからアイコンを作成
     */
    private fun createInventoryIcon(label: String, inventory: Inventory): ItemStack {
        val items = inventory
            .toList()
            .summary()
            .take(10)
        return if (items.isEmpty()) {
            val desc = listOf("@${inventory.location?.world?.name ?: "---"}")
            ItemStack(Material.CHEST)
                .renamed(label)
                .lored(desc)
        } else {
            val desc = listOf("@${inventory.location?.world?.name ?: "---"}") +
                    items
                        .map { "${it.first} x${it.second}" }
            ItemStack(items.first().first)
                .renamed(label)
                .lored(desc)
        }
    }

    /**
     * チェスト毎のアイテムサムネイルを作成
     */
    fun createItemIconsByChest(): List<Pair<ItemStack, String>> {
        val chests = chestLocations
            .filter { it.value.block.state is InventoryHolder }
            .mapValues { (it.value.block.state as InventoryHolder).inventory }
        return (chests.entries + virtualChests.entries)
            .map { (label, inventory) ->
                createInventoryIcon(label, inventory) to label
            }
            .sortedBy { it.first.type }
    }

    /**
     * アイテム数毎のアイテムサムネイルを作成
     */
    fun createItemIconsByAmount(): List<Pair<ItemStack, Material>> {
        val items = chestLocations
            .filter { it.value.block.state is InventoryHolder }
            .mapValues { (it.value.block.state as InventoryHolder).inventory }
            .flatMap { it.value.toList() }

        return items.summary()
            .map {
                ItemStack(it.first)
                    .renamed("${it.first} x${it.second}") to it.first
            }
    }
}