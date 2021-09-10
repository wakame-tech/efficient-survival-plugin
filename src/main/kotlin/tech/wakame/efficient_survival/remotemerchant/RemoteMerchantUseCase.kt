package tech.wakame.efficient_survival.remotemerchant

class RemoteMerchantUseCase(
    private val repository: RemoteMerchantRepository
) {
    fun getDeals(): List<Deal> {
        return repository.getDeals()
    }

    fun addDeal(deal: Deal) {
        repository.addDeal(deal)
    }
}