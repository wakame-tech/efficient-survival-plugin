package tech.wakame.efficient_survival.virtualchest

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import org.bukkit.inventory.DoubleChestInventory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import tech.wakame.efficient_survival.namedlocation.NamedLocation
import tech.wakame.efficient_survival.util.*

interface IVirtualChestRepository {
    fun save(locations: List<NamedLocation>)

    fun load(): List<NamedLocation>
}

class VirtualChestRepository(private val config: Configuration) : IVirtualChestRepository {
    override fun save(locations: List<NamedLocation>) {
        config.setElements("chests", locations.associateBy({ it.label }, { it.location }))
    }

    override fun load(): List<NamedLocation> {
        return config.getElements<Location>("chests")
            .map { NamedLocation(it.key, it.value) }
    }
}


fun createInventoryIcon(label: String, inventory: Inventory): ItemStack {
    val items = inventory.summary()
        .take(10)
    return if (items.isEmpty()) {
        ItemStack(Material.CHEST)
            .renamed(label)
    } else {
        val desc = items
            .map { "${it.first} x${it.second}" }
        ItemStack(items.first().first)
            .renamed(label)
            .lored(desc)
    }
}

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

    fun getVirtualChestPanel(): List<Inventory> {
        val chests = chestLocations
            .filter { it.value.block.state is InventoryHolder }
            .mapValues { (it.value.block.state as InventoryHolder).inventory }
        return (chests.entries + virtualChests.entries)
            .map { (label, inventory) ->
                createInventoryIcon(label, inventory)
            }
            .sortedBy { it.type }
            .chunked(54 - 9)
            .map {
                Bukkit.createInventory(null, 54, VirtualChestConfig.virtualChestName)
                    .apply {
                        it.forEachIndexed { index, icon ->
                            setItem(index, icon)
                        }
                        setItem(VirtualChestConfig.prevIndex, ItemStack(Material.PAPER).renamed("戻る"))
                        setItem(VirtualChestConfig.nextIndex, ItemStack(Material.PAPER).renamed("進む"))
                    }
            }
            .ifEmpty {
                return Bukkit.createInventory(null, 54, VirtualChestConfig.virtualChestName)
                    .apply {
                        setItem(VirtualChestConfig.prevIndex, ItemStack(Material.PAPER).renamed("戻る"))
                        setItem(VirtualChestConfig.nextIndex, ItemStack(Material.PAPER).renamed("進む"))
                    }
                    .let { inv -> listOf(inv) }
            }

    }

    fun getInventories(): List<Pair<String, Inventory>> {
        return chestLocations
            .mapValues { (it.value.block.state as? InventoryHolder)?.inventory }
            .filter { it.value != null }
            .map { it.key to it.value!! }
    }

    fun getInventory(label: String): Inventory? {
        if (label in chestLocations.keys) {
            return (chestLocations[label]!!.block.state as? InventoryHolder)?.inventory
        }
        if (label in virtualChests.keys) {
            return virtualChests[label]!!
        }
        return null
    }
}