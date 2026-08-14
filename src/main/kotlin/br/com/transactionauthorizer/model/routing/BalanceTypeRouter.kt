package br.com.transactionauthorizer.model.routing

import br.com.transactionauthorizer.model.AccountBalanceType

object BalanceTypeRouter {

    fun resolve(merchantName: String, mcc: String): AccountBalanceType =
        resolveByMerchant(merchantName) ?: resolveByMcc(mcc)

    private fun resolveByMerchant(merchantName: String): AccountBalanceType? =
        when (merchantName) {
            in MerchantRegistry.FOOD -> AccountBalanceType.FOOD
            in MerchantRegistry.MEAL -> AccountBalanceType.MEAL
            else -> null
        }

    private fun resolveByMcc(mcc: String): AccountBalanceType =
        when (mcc) {
            in MccRegistry.FOOD -> AccountBalanceType.FOOD
            in MccRegistry.MEAL -> AccountBalanceType.MEAL
            else -> AccountBalanceType.CASH
        }
}
