package br.com.transactionauthorizer.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CardTransaction(
    override val id: UUID = UUID.randomUUID(),
    val accountBalanceId: UUID,
    val accountId: UUID,
    val account: String,
    val totalAmount: BigDecimal,
    val mcc: String,
    val merchant: String,
    val cardTransactionStatus: CardTransactionStatus,
    override val version: Long = 0,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, version, createdAt, updatedAt)

enum class CardTransactionStatus {
    APPROVED, DENIED
}
