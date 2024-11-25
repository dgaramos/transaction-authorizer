package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal

interface CardTransactionService {

    fun getAllTransactions(): List<CardTransaction>
    fun getTransactionById(id: Long): CardTransaction?
    fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        transactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction
}

