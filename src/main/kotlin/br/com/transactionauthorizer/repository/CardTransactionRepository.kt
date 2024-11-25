package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal

interface CardTransactionRepository {

    fun getAllTransactions(): List<CardTransaction>

    fun getAllTransactionsByAccountId(account: String): List<CardTransaction>

    fun getTransactionById(id: Long): CardTransaction?

    fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        transactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction
}
