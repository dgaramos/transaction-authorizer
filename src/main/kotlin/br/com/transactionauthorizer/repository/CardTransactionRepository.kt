package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal

interface CardTransactionRepository {

    fun getAllTransactionsByAccountId(account: String): List<CardTransaction>

    fun getAllTransactionsByAccountBalanceId(accountBalanceId: Long): List<CardTransaction>

    fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: Long,
        cardTransactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction
}
