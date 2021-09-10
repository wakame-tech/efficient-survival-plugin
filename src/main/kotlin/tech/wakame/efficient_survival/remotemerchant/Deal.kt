package tech.wakame.efficient_survival.remotemerchant

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

data class Deal(
    val from: ItemStack,
    val to: ItemStack
) {
    companion object {
        fun fromMerchantRecipe(recipe: MerchantRecipe): Deal {
            return Deal(recipe.ingredients[0], recipe.result)
        }
    }
}