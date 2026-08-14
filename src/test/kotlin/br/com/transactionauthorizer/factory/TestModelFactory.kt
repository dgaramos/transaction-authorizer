package br.com.transactionauthorizer.factory

import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

object TestModelFactory {

    fun buildAccount(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Account"
    ): Account {
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
        return Account(id = id, name = name, createdAt = now, updatedAt = now)
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
