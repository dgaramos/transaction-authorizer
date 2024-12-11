package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal

interface CardTransactionService {

    fun getAllTransactionsByAccountBalanceId(accountBalanceId: Long, offset: Int = 0, limit: Int = 10): List<CardTransaction>
    fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: Long,
        transactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction
}

