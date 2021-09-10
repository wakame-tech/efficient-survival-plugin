package tech.wakame.efficient_survival.remotemerchant

import org.bukkit.configuration.Configuration

class RemoteMerchantRepository(configuration: Configuration) {
    private val deals: MutableList<Deal> = mutableListOf()

    fun getDeals(): List<Deal> {
        return deals.toList()
    }

    fun addDeal(deal: Deal) {
        deals.add(deal)
    }

    fun saveDeals() {
        // TODO:
    }

    fun loadDeals() {
        // TODO
    }
}