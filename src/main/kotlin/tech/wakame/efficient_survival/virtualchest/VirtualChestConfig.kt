package tech.wakame.efficient_survival.virtualchest

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import tech.wakame.efficient_survival.util.renamed

data class IUIIcon(val index: Int, val icon: ItemStack)

object VirtualChestConfig {
    val virtualChestName: String = "virtual_chest"
    val prev = IUIIcon(
        45,
        ItemStack(Material.PAPER).renamed( "戻る")
    )
    val chest = IUIIcon(
        46,
        ItemStack(Material.CHEST).renamed("ソート:チェスト")
    )
    val amount = IUIIcon(
        47,
        ItemStack(Material.CLOCK).renamed("ソート:数")
    )
    val next = IUIIcon(
        53,
        ItemStack(Material.PAPER).renamed("進む")
    )
}