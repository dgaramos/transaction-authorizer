package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal

interface CardTransactionService {

    fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: Long,
        transactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction
}

