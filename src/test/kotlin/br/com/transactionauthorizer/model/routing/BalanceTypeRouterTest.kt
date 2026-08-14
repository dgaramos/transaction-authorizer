package br.com.transactionauthorizer.model.routing

import br.com.transactionauthorizer.model.AccountBalanceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class BalanceTypeRouterTest {

    companion object {
        @JvmStatic
        fun merchantAndMccData() = buildList {
            val cashMCCExample = "9999"

            MerchantRegistry.FOOD.forEach { merchant ->
                MccRegistry.MEAL.plus(cashMCCExample).forEach { mcc ->
                    add(arrayOf(merchant, mcc, AccountBalanceType.FOOD))
                }
            }

            MerchantRegistry.MEAL.forEach { merchant ->
                MccRegistry.FOOD.plus(cashMCCExample).forEach { mcc ->
                    add(arrayOf(merchant, mcc, AccountBalanceType.MEAL))
                }
            }

            MccRegistry.MEAL.forEach { mcc ->
                add(arrayOf("Unknown Merchant", mcc, AccountBalanceType.MEAL))
            }

            MccRegistry.FOOD.forEach { mcc ->
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
        val result = BalanceTypeRouter.resolve(merchantName, mcc)
        assertEquals(expectedBalanceType, result)
    }
}
