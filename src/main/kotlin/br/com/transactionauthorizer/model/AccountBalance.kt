package br.com.transactionauthorizer.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class AccountBalance(
    override val id: UUID = UUID.randomUUID(),
    val accountId: UUID,
    val accountBalanceType: AccountBalanceType,
    val amount: BigDecimal,
    override val version: Long = 0,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, version, createdAt, updatedAt)

enum class AccountBalanceType {
    CASH, MEAL, FOOD;

    fun isCash(): Boolean {
        return this == CASH
    }
}