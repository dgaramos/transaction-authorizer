package br.com.transactionauthorizer.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class CardTransaction(
    val id: Long? = null, // Nullable for when the object is created but not yet persisted
    val account: String,
    val totalAmount: BigDecimal,
    val mcc: String,
    val merchant: String,
    val cardTransactionStatus: CardTransactionStatus,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class CardTransactionStatus {
    APPROVED, DENIED
}
