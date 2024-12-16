package br.com.transactionauthorizer.repository.implementations

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.CardTransactionTable
import br.com.transactionauthorizer.repository.BaseRepository
import br.com.transactionauthorizer.repository.CardTransactionRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
class CardTransactionRepositoryImpl : CardTransactionRepository, BaseRepository<CardTransaction, CardTransactionTable>(
    CardTransactionTable, { row ->
    CardTransaction(
        id = row[CardTransactionTable.id].value,
        account = row[CardTransactionTable.account],
        accountId = row[CardTransactionTable.accountId],
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

    override fun getAllTransactionsByAccountId(accountId: UUID, offset: Int, limit: Int): List<CardTransaction> {
        return transaction {
            CardTransactionTable
                .selectAll().where { CardTransactionTable.accountId eq accountId }
                .limit(n = limit, offset = offset.toLong())
                .orderBy(CardTransactionTable.createdAt, SortOrder.DESC)
                .map {
                    mapToCardTransaction(it)
                }
        }
    }

    override fun getAllTransactionsByAccountBalanceId(accountBalanceId: UUID, offset: Int, limit: Int): List<CardTransaction> {
        return transaction {
            CardTransactionTable
                .selectAll().where { CardTransactionTable.accountBalanceId eq accountBalanceId }
                .limit(n = limit, offset = offset.toLong())
                .orderBy(CardTransactionTable.createdAt, SortOrder.DESC)
                .map {
                    mapToCardTransaction(it)
                }
        }
    }

    override fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: UUID,
        cardTransactionStatus: CardTransactionStatus,
        merchant: String
    ): CardTransaction {
        val cardTransaction = CardTransaction(
            account = account,
            accountId = UUID.fromString(account),
            totalAmount = totalAmount,
            mcc = mcc,
            accountBalanceId = accountBalanceId,
            cardTransactionStatus = cardTransactionStatus,
            merchant = merchant
        )
        return super.create(cardTransaction, ::buildCardTransactionTable)
    }

    private fun buildCardTransactionTable(cardTransaction: CardTransaction): UUID {
        return CardTransactionTable.insertAndGetId {
            it[id] = cardTransaction.id
            it[accountId] = cardTransaction.accountId
            it[accountBalanceId] = cardTransaction.accountBalanceId
            it[account] = cardTransaction.account
            it[totalAmount] = cardTransaction.totalAmount
            it[mcc] = cardTransaction.mcc
            it[merchant] = cardTransaction.merchant
            it[cardTransactionStatus] = cardTransaction.cardTransactionStatus
            it[CardTransactionTable.version] = cardTransaction.version
            it[CardTransactionTable.createdAt] = cardTransaction.createdAt
            it[CardTransactionTable.updatedAt] = cardTransaction.updatedAt
        }.value
    }

    private fun mapToCardTransaction(row: ResultRow): CardTransaction {
        return CardTransaction(
            id = row[CardTransactionTable.id].value,
            accountId = row[CardTransactionTable.accountId],
            accountBalanceId = row[CardTransactionTable.accountBalanceId],
            account = row[CardTransactionTable.account],
            totalAmount = row[CardTransactionTable.totalAmount],
            mcc = row[CardTransactionTable.mcc],
            merchant = row[CardTransactionTable.merchant],
            cardTransactionStatus = row[CardTransactionTable.cardTransactionStatus],
            version = row[CardTransactionTable.version],
            createdAt = row[CardTransactionTable.createdAt],
            updatedAt = row[CardTransactionTable.updatedAt]
        )
    }
}