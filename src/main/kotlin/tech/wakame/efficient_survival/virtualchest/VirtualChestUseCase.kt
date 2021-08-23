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

enum class VirtualChestPanelType {
    /**
     * チェスト毎
     */
    ByChest,

    /**
     * アイテム数毎
     */
    ByAmount,
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

    /**
     * インベントリからアイコンを作成
     */
    private fun createInventoryIcon(label: String, inventory: Inventory): ItemStack {
        val items = inventory
            .toList()
            .summary()
            .take(10)
        return if (items.isEmpty()) {
            ItemStack(Material.CHEST)
                .renamed(label)
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
    fun createItemIconsByChest(): List<ItemStack> {
        val chests = chestLocations
            .filter { it.value.block.state is InventoryHolder }
            .mapValues { (it.value.block.state as InventoryHolder).inventory }
        return (chests.entries + virtualChests.entries)
            .map { (label, inventory) ->
                createInventoryIcon(label, inventory)
            }
            .sortedBy { it.type }
    }

    /**
     * アイテム数毎のアイテムサムネイルを作成
     */
    fun createItemIconsByAmount(): List<ItemStack> {
        val items = chestLocations
            .filter { it.value.block.state is InventoryHolder }
            .mapValues { (it.value.block.state as InventoryHolder).inventory }
            .flatMap { it.value.toList() }

        return items.summary()
            .map {
                ItemStack(it.first)
                    .renamed("${it.first} x${it.second}")
            }
    }

    /**
     * パネルを作成
     */
    private fun createPanelInventory(icons: List<ItemStack>): Inventory {
        return Bukkit.createInventory(null, 54, VirtualChestConfig.virtualChestName)
            .apply {
                icons.forEachIndexed { index, icon ->
                    setItem(index, icon)
                }
                setItem(VirtualChestConfig.prev.index, VirtualChestConfig.prev.icon)
                setItem(VirtualChestConfig.chest.index, VirtualChestConfig.chest.icon)
                setItem(VirtualChestConfig.amount.index, VirtualChestConfig.amount.icon)
                setItem(VirtualChestConfig.next.index, VirtualChestConfig.next.icon)
            }
    }

    fun getVirtualChestPanel(panelType: VirtualChestPanelType): List<Inventory> {
        return when (panelType) {
            VirtualChestPanelType.ByChest -> createItemIconsByChest()
            VirtualChestPanelType.ByAmount -> createItemIconsByAmount()
        }
            .chunked(54 - 9)
            .ifEmpty { listOf(listOf()) }
            .map { createPanelInventory(it) }
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

    fun clickIndex(player: Player, type: VirtualChestPanelType, page: Int, index: Int) {
        val panels = getVirtualChestPanel(type)
        val panel = panels[page]

        when (index) {
            in 0 until 45 -> {
                if (index !in panel.toList().filterNotNull().indices)
                    return

                when (type) {
                    VirtualChestPanelType.ByChest -> {
                        val chestKey = panel.getItem(index)!!.itemMeta?.displayName ?: return
                        player.sendMessage("key: $chestKey")
                        getInventory(chestKey)
                            ?.let { player.openInventory(it) }
                    }
                    VirtualChestPanelType.ByAmount -> {
                        player.sendMessage("click: ${panel.getItem(index)!!.type}")
                        // TODO: withdraw item from chests
                    }
                }
            }
            VirtualChestConfig.prev.index -> {
                VirtualChestEventHandler.currentPageIndex =
                    Integer.max(0, index - 1)
                player.openInventory(panel)
            }
            VirtualChestConfig.chest.index -> {
                VirtualChestEventHandler.currentPageIndex = 0
                VirtualChestEventHandler.currentPanelType = VirtualChestPanelType.ByChest
                player.openInventory(panel)
            }
            VirtualChestConfig.amount.index -> {
                VirtualChestEventHandler.currentPageIndex = 0
                VirtualChestEventHandler.currentPanelType = VirtualChestPanelType.ByAmount
                player.openInventory(panel)
            }
            VirtualChestConfig.next.index -> {
                VirtualChestEventHandler.currentPageIndex =
                    Math.min(panels.lastIndex, index + 1)
                player.openInventory(panel)
            }
        }
    }
}