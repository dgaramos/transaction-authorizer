package br.com.transactionauthorizer.utils

import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.AccountBalanceTable
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.model.table.CardTransactionTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import kotlin.random.Random

object DatabaseInitializer {
    fun setupSchemaAndData() {
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.drop(
                AccountTable,
                AccountBalanceTable,
                CardTransactionTable
            )
            SchemaUtils.create(
                AccountTable,
                AccountBalanceTable,
                CardTransactionTable
            )

            populateInitialData()
        }
    }

    private fun populateInitialData() {
        val account1Id = AccountTable.insertAndGetId {
            it[name] = "John Doe"
        }.value
        val account2Id = AccountTable.insertAndGetId {
            it[name] = "Jane Smith"
        }.value
        val account3Id = AccountTable.insertAndGetId {
            it[name] = "Carlos Silva"
        }.value

        insertAccountBalances(account1Id)
        insertAccountBalances(account2Id)
        insertAccountBalances(account3Id)

        repeat(20) {
            insertCardTransaction(account1Id)
            insertCardTransaction(account2Id)
            insertCardTransaction(account3Id)
        }
    }

    private fun insertAccountBalances(accountId: Long) {
        AccountBalanceTable.insert {
            it[AccountBalanceTable.accountId] = accountId
            it[accountBalanceType] = AccountBalanceType.FOOD
            it[amount] = BigDecimal("150.00")
        }
        AccountBalanceTable.insert {
            it[AccountBalanceTable.accountId] = accountId
            it[accountBalanceType] = AccountBalanceType.MEAL
            it[amount] = BigDecimal("200.00")
        }
        AccountBalanceTable.insert {
            it[AccountBalanceTable.accountId] = accountId
            it[accountBalanceType] = AccountBalanceType.CASH
            it[amount] = BigDecimal("500.00")
        }
    }

    private fun insertCardTransaction(accountId: Long) {
        val mcc = generateRandomMcc()
        val transactionAmount = BigDecimal(Random.nextDouble(50.00, 300.00))
        val status = if (Random.nextBoolean()) CardTransactionStatus.APPROVED else CardTransactionStatus.DENIED
        val merchant = "Merchant ${Random.nextInt(1, 10)}"

        CardTransactionTable.insert {
            it[account] = accountId.toString()
            it[CardTransactionTable.mcc] = mcc
            it[totalAmount] = transactionAmount
            it[cardTransactionStatus] = status
            it[CardTransactionTable.merchant] = merchant
        }
    }

    private fun generateRandomMcc(): String {
        // Generate random MCC codes to simulate different types of merchants
        val mccs = listOf(
            "5411",
            "5412",
            "5811",
            "5812",
            "5999",
            "1234",
            "5678"
        )
        return mccs[Random.nextInt(mccs.size)]
    }
}
