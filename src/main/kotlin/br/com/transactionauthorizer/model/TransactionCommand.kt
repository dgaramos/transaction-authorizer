package br.com.transactionauthorizer.model

import br.com.transactionauthorizer.model.routing.BalanceTypeRouter
import java.math.BigDecimal

data class TransactionCommand(
    val account: String,
    val totalAmount: BigDecimal,
    val mcc: String,
    val merchant: String
) {
    fun resolveBalanceType(): AccountBalanceType =
        BalanceTypeRouter.resolve(merchant, mcc)
}
