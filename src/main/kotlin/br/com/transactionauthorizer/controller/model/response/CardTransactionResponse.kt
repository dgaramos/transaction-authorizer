package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.CardTransaction
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

data class CardTransactionResponse(
    val id: Long,
    val account: String,
    val totalAmount: BigDecimal,
    val mcc: String,
    val merchant: String,
    val cardTransactionStatus: String,
    val createdAt: String
) {
    companion object {
        fun fromCardTransaction(cardTransaction: CardTransaction): CardTransactionResponse {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDate = cardTransaction.createdAt.format(formatter)
            return CardTransactionResponse(
                id = cardTransaction.id!!,
                account = cardTransaction.account,
                totalAmount = cardTransaction.totalAmount,
                mcc = cardTransaction.mcc,
                merchant = cardTransaction.merchant,
                cardTransactionStatus = cardTransaction.cardTransactionStatus.name,
                createdAt = formattedDate
            )
        }
    }
}