package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal
import java.util.UUID

interface CardTransactionService {

    fun getAllTransactionsByAccountBalanceId(accountBalanceId: UUID, offset: Int = 0, limit: Int = 10): List<CardTransaction>
    fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: UUID,
        transactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction
}

