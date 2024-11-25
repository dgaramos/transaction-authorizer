package br.com.transactionauthorizer.utils

import br.com.transactionauthorizer.constants.MccLists
import br.com.transactionauthorizer.constants.MerchantNames
import br.com.transactionauthorizer.model.AccountBalanceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class AccountBalanceTypeUtilsTest {

    companion object {
        @JvmStatic
        fun merchantAndMccData() = buildList {
            val cashMCCExample = "9999"

            MerchantNames.FOOD_MERCHANTS.forEach { merchant ->
                MccLists.MEAL_MCCS.plus(cashMCCExample).forEach { mcc ->
                    add(arrayOf(merchant, mcc, AccountBalanceType.FOOD))
                }
            }

            MerchantNames.MEAL_MERCHANTS.forEach { merchant ->
                MccLists.FOOD_MCCS.plus(cashMCCExample).forEach { mcc ->
                    add(arrayOf(merchant, mcc, AccountBalanceType.MEAL))
                }
            }

            MccLists.MEAL_MCCS.forEach { mcc ->
                add(arrayOf("Unknown Merchant", mcc, AccountBalanceType.MEAL))
            }

            MccLists.FOOD_MCCS.forEach { mcc ->
                add(arrayOf("Unknown Merchant", mcc, AccountBalanceType.FOOD))
            }

            add(arrayOf("Unknown Merchant", cashMCCExample, AccountBalanceType.CASH))
        }
    }

    @ParameterizedTest
    @MethodSource("merchantAndMccData")
    fun `should determine balance type based on merchant name and MCC`(
        merchantName: String,
        mcc: String,
        expectedBalanceType: AccountBalanceType
    ) {
        val result = AccountBalanceTypeUtils.determineBalanceType(merchantName, mcc)
        assertEquals(expectedBalanceType, result)
    }
}
