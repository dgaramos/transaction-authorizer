package br.com.transactionauthorizer.repository.implementations

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.model.table.CardTransactionTable
import br.com.transactionauthorizer.repository.BaseRepository
import br.com.transactionauthorizer.repository.CardTransactionRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class CardTransactionRepositoryImpl : CardTransactionRepository, BaseRepository<CardTransaction, CardTransactionTable>(
    CardTransactionTable, { row ->
    CardTransaction(
        id = row[CardTransactionTable.id].value,
        account = row[CardTransactionTable.account],
        totalAmount = row[CardTransactionTable.totalAmount],
        mcc = row[CardTransactionTable.mcc],
        merchant = row[CardTransactionTable.merchant],
        accountBalanceId = row[CardTransactionTable.accountBalanceId],
        cardTransactionStatus = row[CardTransactionTable.cardTransactionStatus],
        version = row[CardTransactionTable.version],
        createdAt = row[CardTransactionTable.createdAt],
        updatedAt = row[CardTransactionTable.updatedAt]
    )
}) {

    override fun getAllTransactionsByAccountId(account: String): List<CardTransaction> {
        return transaction {
            CardTransactionTable.selectAll().where { CardTransactionTable.account eq account }
                .map {
                    mapToCardTransaction(it)
                }
        }
    }

    override fun getAllTransactionsByAccountBalanceId(accountBalanceId: Long): List<CardTransaction> {
        return transaction {
            CardTransactionTable.selectAll().where { CardTransactionTable.accountBalanceId eq accountBalanceId }
                .map {
                    mapToCardTransaction(it)
                }
        }
    }

    override fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: Long,
        cardTransactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction {
        val cardTransaction = CardTransaction(
            account = account,
            totalAmount = totalAmount,
            mcc = mcc,
            accountBalanceId = accountBalanceId,
            cardTransactionStatus = cardTransactionStatus,
            merchant = merchant
        )
        return super.create(cardTransaction, ::buildCardTransactionTable)
    }

    private fun buildCardTransactionTable(cardTransaction: CardTransaction): Long {
        return CardTransactionTable.insertAndGetId {
            it[account] = cardTransaction.account
            it[totalAmount] = cardTransaction.totalAmount
            it[mcc] = cardTransaction.mcc
            it[merchant] = cardTransaction.merchant
            it[accountBalanceId] = cardTransaction.accountBalanceId
            it[cardTransactionStatus] = cardTransaction.cardTransactionStatus
            it[CardTransactionTable.version] = cardTransaction.version
            it[CardTransactionTable.createdAt] = cardTransaction.createdAt
            it[CardTransactionTable.updatedAt] = cardTransaction.updatedAt
        }.value
    }

    private fun mapToCardTransaction(row: ResultRow): CardTransaction {
        return CardTransaction(
            id = row[CardTransactionTable.id].value,
            account = row[CardTransactionTable.account],
            totalAmount = row[CardTransactionTable.totalAmount],
            mcc = row[CardTransactionTable.mcc],
            merchant = row[CardTransactionTable.merchant],
            accountBalanceId = row[CardTransactionTable.accountBalanceId],
            cardTransactionStatus = row[CardTransactionTable.cardTransactionStatus],
            version = row[CardTransactionTable.version],
            createdAt = row[CardTransactionTable.createdAt],
            updatedAt = row[CardTransactionTable.updatedAt]
        )
    }
}