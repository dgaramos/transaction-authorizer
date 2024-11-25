package br.com.transactionauthorizer.utils

import br.com.transactionauthorizer.constants.MccLists.FOOD_MCCS
import br.com.transactionauthorizer.constants.MccLists.MEAL_MCCS
import br.com.transactionauthorizer.constants.MerchantNames
import br.com.transactionauthorizer.model.AccountBalanceType

object AccountBalanceTypeUtils {

    fun determineBalanceType(merchantName: String, mcc: String) =
        balanceTypeBasedOnMerchantName(merchantName) ?: balanceTypeBasedOnMCC(mcc)

    private fun balanceTypeBasedOnMCC(mcc: String) =
        when (mcc) {
            in FOOD_MCCS -> AccountBalanceType.FOOD
            in MEAL_MCCS -> AccountBalanceType.MEAL
            else -> AccountBalanceType.CASH
        }

    private fun balanceTypeBasedOnMerchantName(merchantName: String) =
        when (merchantName) {
            in MerchantNames.FOOD_MERCHANTS -> AccountBalanceType.FOOD
            in MerchantNames.MEAL_MERCHANTS -> AccountBalanceType.MEAL
            else -> null
        }
}