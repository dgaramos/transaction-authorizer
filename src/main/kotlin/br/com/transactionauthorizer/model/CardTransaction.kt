package br.com.transactionauthorizer.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class CardTransaction(
    override val id: Long? = null, // Nullable for when the object is created but not yet persisted
    val account: String,
    val totalAmount: BigDecimal,
    val mcc: String,
    val merchant: String,
    val accountBalanceId: Long,
    val cardTransactionStatus: CardTransactionStatus,
    override val version: Long = 0,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, version, createdAt, updatedAt)

enum class CardTransactionStatus {
    APPROVED, DENIED
}
