package br.com.transactionauthorizer.factory

import br.com.transactionauthorizer.model.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

object TestModelFactory {

    fun buildAccount(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Account"
    ): Account {
        return Account(id = id, name = name)
    }

    fun buildAccountBalance(
        id: UUID = UUID.randomUUID(),
        accountId: UUID = UUID.randomUUID(),
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

    fun buildCardTransaction(
        id: UUID = UUID.randomUUID(),
        account: String = UUID.randomUUID().toString(),
        accountBalanceId: UUID = UUID.randomUUID(),
        accountId: UUID = UUID.fromString(account),
        totalAmount: BigDecimal = BigDecimal.valueOf(50.0),
        mcc: String = "1234",
        merchant: String = "Test Merchant",
        cardTransactionStatus: CardTransactionStatus = CardTransactionStatus.APPROVED,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): CardTransaction {
        return CardTransaction(
            id = id,
            account = account,
            accountBalanceId = accountBalanceId,
            accountId = accountId,
            totalAmount = totalAmount,
            mcc = mcc,
            merchant = merchant,
            cardTransactionStatus = cardTransactionStatus,
            createdAt = createdAt
        )
    }
}
