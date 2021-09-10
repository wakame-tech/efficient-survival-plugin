package tech.wakame.efficient_survival.remotemerchant

import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import tech.wakame.efficient_survival.util.inspect

class RemoteMerchantEventHandler(
    private val useCase: RemoteMerchantUseCase
) : Listener {
    @EventHandler
    fun onVillagerInteract(event: PlayerInteractAtEntityEvent) {
        if (event.rightClicked !is Villager) {
            return
        }

        val villager = event.rightClicked as Villager
        villager.recipes.forEach {
            event.player.sendMessage("${it.ingredients[0].inspect()} -> ${it.result.inspect()}")
            useCase.addDeal(Deal.fromMerchantRecipe(it))
        }
    }
}