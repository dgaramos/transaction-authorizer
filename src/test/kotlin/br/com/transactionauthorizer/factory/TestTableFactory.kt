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

object TestTableFactory {

    fun createAccount(
        id: Long? = null,
        name: String = "Test Account"
    ): Long {
        return transaction {
            AccountTable.insertAndGetId {
                if (id != null) it[AccountTable.id] = id
                it[AccountTable.name] = name
            }.value
        }
    }

    fun createAccountBalance(
        id: Long? = null,
        accountId: Long,
        accountBalanceType: AccountBalanceType = AccountBalanceType.CASH,
        amount: BigDecimal = BigDecimal.valueOf(100.00)
    ): Long {
        return transaction {
            AccountBalanceTable.insertAndGetId {
                if (id != null) it[AccountBalanceTable.id] = id
                it[AccountBalanceTable.accountId] = accountId
                it[AccountBalanceTable.accountBalanceType] = accountBalanceType
                it[AccountBalanceTable.amount] = amount
            }.value
        }
    }

    fun createCardTransaction(
        id: Long? = null,
        account: String = "Test Account",
        totalAmount: BigDecimal = BigDecimal.valueOf(50.00),
        mcc: String = "1234",
        merchant: String = "Test Merchant",
        accountBalanceId: Long = 1L,
        cardTransactionStatus: CardTransactionStatus = CardTransactionStatus.APPROVED,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Long {
        return transaction {
            CardTransactionTable.insertAndGetId {
                if (id != null) it[CardTransactionTable.id] = id
                it[CardTransactionTable.account] = account
                it[CardTransactionTable.totalAmount] = totalAmount
                it[CardTransactionTable.mcc] = mcc
                it[CardTransactionTable.merchant] = merchant
                it[CardTransactionTable.accountBalanceId] = accountBalanceId
                it[CardTransactionTable.cardTransactionStatus] = cardTransactionStatus
                it[CardTransactionTable.createdAt] = createdAt
            }.value
        }
    }
}
