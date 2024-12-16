package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal
import java.util.UUID

interface CardTransactionRepository {

    fun getAllTransactionsByAccountId(accountId: UUID, offset: Int = 0, limit: Int = 10): List<CardTransaction>

    fun getAllTransactionsByAccountBalanceId(accountBalanceId: UUID, offset: Int = 0, limit: Int = 10): List<CardTransaction>

    fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: UUID,
        cardTransactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction
}
