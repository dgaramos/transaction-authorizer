package br.com.transactionauthorizer.repository.implementations

import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.CardTransactionTable
import br.com.transactionauthorizer.repository.CardTransactionRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class CardTransactionRepositoryImpl : CardTransactionRepository {
    override fun getAllTransactions(): List<CardTransaction> {
        return transaction {
            CardTransactionTable.selectAll().map {
                mapToCardTransaction(it)
            }
        }
    }

    override fun getAllTransactionsByAccountId(account: String): List<CardTransaction> {
        return transaction {
            CardTransactionTable.selectAll().where { CardTransactionTable.account eq account }
                .map {
                    mapToCardTransaction(it)
                }
        }
    }

    override fun getTransactionById(id: Long): CardTransaction? {
        return transaction {
            CardTransactionTable.selectAll().where { CardTransactionTable.id eq id }
                .mapNotNull {
                    mapToCardTransaction(it)
                }.singleOrNull()
        }
    }

    override fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        transactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction {
        var createdTransaction: CardTransaction? = null

        transaction {
            val id = CardTransactionTable.insertAndGetId {
                it[CardTransactionTable.account] = account
                it[CardTransactionTable.totalAmount] = totalAmount
                it[CardTransactionTable.mcc] = mcc
                it[cardTransactionStatus] = transactionStatus
                it[CardTransactionTable.merchant] = merchant
            }.value

            createdTransaction = getTransactionById(id)
        }

        return createdTransaction!!
    }

    private fun mapToCardTransaction(row: ResultRow): CardTransaction {
        return CardTransaction(
            id = row[CardTransactionTable.id].value,
            account = row[CardTransactionTable.account],
            totalAmount = row[CardTransactionTable.totalAmount],
            mcc = row[CardTransactionTable.mcc],
            merchant = row[CardTransactionTable.merchant],
            cardTransactionStatus = row[CardTransactionTable.cardTransactionStatus],
            createdAt = row[CardTransactionTable.createdAt]
        )
    }
}