package br.com.transactionauthorizer.model

import java.math.BigDecimal

data class AccountBalance(
    val id: Long? = null, // Nullable for when the object is created but not yet persisted,
    val accountId: Long,
    val accountBalanceType: AccountBalanceType,
    val amount: BigDecimal
) {
    fun isCash(): Boolean {
        return this.accountBalanceType == AccountBalanceType.CASH
    }
}

enum class AccountBalanceType {
    CASH, MEAL, FOOD;

    fun isCash(): Boolean {
        return this == CASH
    }
}