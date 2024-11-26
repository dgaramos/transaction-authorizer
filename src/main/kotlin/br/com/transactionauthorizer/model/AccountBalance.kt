package br.com.transactionauthorizer.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class AccountBalance(
    override val id: Long? = null, // Nullable for when the object is created but not yet persisted,
    val accountId: Long,
    val accountBalanceType: AccountBalanceType,
    val amount: BigDecimal,
    override val version: Long = 0,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, version, createdAt, updatedAt) {
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