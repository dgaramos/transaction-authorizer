package br.com.transactionauthorizer.factory

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.model.*
import java.math.BigDecimal
import java.time.LocalDateTime

object TestModelFactory {

    fun createAccount(
        id: Long? = null,
        name: String = "Test Account"
    ): Account {
        return Account(id = id, name = name)
    }

    fun createAccountBalance(
        id: Long? = null,
        accountId: Long = 1L,
        accountBalanceType: AccountBalanceType = AccountBalanceType.CASH,
        amount: BigDecimal = BigDecimal.valueOf(100.0)
    ): AccountBalance {
        return AccountBalance(
            id = id,
            accountId = accountId,
            accountBalanceType = accountBalanceType,
            amount = amount
        )
    }

    fun createCardTransaction(
        id: Long? = null,
        account: String = "Test Account",
        totalAmount: BigDecimal = BigDecimal.valueOf(50.0),
        mcc: String = "1234",
        merchant: String = "Test Merchant",
        cardTransactionStatus: CardTransactionStatus = CardTransactionStatus.APPROVED,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): CardTransaction {
        return CardTransaction(
            id = id,
            account = account,
            totalAmount = totalAmount,
            mcc = mcc,
            merchant = merchant,
            cardTransactionStatus = cardTransactionStatus,
            createdAt = createdAt
        )
    }

    fun createReceivedTransactionRequest(
        account: String = "1",
        totalAmount: BigDecimal = BigDecimal(100),
        mcc: String = "5811",
        merchant: String = "MerchantName"
    ): ReceivedTransactionRequest {
        return ReceivedTransactionRequest(
            account = account,
            totalAmount = totalAmount,
            mcc = mcc,
            merchant = merchant
        )
    }

}
