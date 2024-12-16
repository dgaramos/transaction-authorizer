package br.com.transactionauthorizer.factory

import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.AccountBalanceTable
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.model.table.CardTransactionTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

object TestTableFactory {

    fun createAccount(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Account"
    ): UUID {
        return transaction {
            AccountTable.insertAndGetId {
                it[AccountTable.id] = id
                it[AccountTable.name] = name
            }.value
        }
    }

    fun createAccountBalance(
        id: UUID = UUID.randomUUID(),
        accountId: UUID = UUID.randomUUID(),
        accountBalanceType: AccountBalanceType = AccountBalanceType.CASH,
        amount: BigDecimal = BigDecimal.valueOf(100.00)
    ): UUID {
        return transaction {
            AccountBalanceTable.insertAndGetId {
                it[AccountBalanceTable.id] = id
                it[AccountBalanceTable.accountId] = accountId
                it[AccountBalanceTable.accountBalanceType] = accountBalanceType
                it[AccountBalanceTable.amount] = amount
            }.value
        }
    }

    fun createCardTransaction(
        id: UUID = UUID.randomUUID(),
        account: String = UUID.randomUUID().toString(),
        accountBalanceId: UUID = UUID.randomUUID(),
        accountId: UUID = UUID.fromString(account),
        totalAmount: BigDecimal = BigDecimal.valueOf(50.00),
        mcc: String = "1234",
        merchant: String = "Test Merchant",
        cardTransactionStatus: CardTransactionStatus = CardTransactionStatus.APPROVED,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): UUID {
        return transaction {
            CardTransactionTable.insertAndGetId {
                it[CardTransactionTable.id] = id
                it[CardTransactionTable.accountId] = accountId
                it[CardTransactionTable.accountBalanceId] = accountBalanceId
                it[CardTransactionTable.account] = account
                it[CardTransactionTable.totalAmount] = totalAmount
                it[CardTransactionTable.mcc] = mcc
                it[CardTransactionTable.merchant] = merchant
                it[CardTransactionTable.cardTransactionStatus] = cardTransactionStatus
                it[CardTransactionTable.createdAt] = createdAt
            }.value
        }
    }
}
