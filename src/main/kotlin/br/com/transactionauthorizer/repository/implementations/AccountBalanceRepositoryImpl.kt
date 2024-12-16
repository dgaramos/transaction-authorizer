package br.com.transactionauthorizer.repository.implementations

import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByAccountIdAndTypeException
import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByIdException
import br.com.transactionauthorizer.exceptions.AccountBalancesNotFoundByAccountIdException
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.table.AccountBalanceTable
import br.com.transactionauthorizer.model.table.AccountBalanceTable.accountBalanceType
import br.com.transactionauthorizer.model.table.AccountBalanceTable.accountId
import br.com.transactionauthorizer.model.table.AccountBalanceTable.amount
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import br.com.transactionauthorizer.repository.BaseRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class AccountBalanceRepositoryImpl : AccountBalanceRepository, BaseRepository<AccountBalance, AccountBalanceTable>(AccountBalanceTable, { row ->
    AccountBalance(
        id = row[AccountBalanceTable.id].value,
        accountId = row[accountId],
        accountBalanceType = row[accountBalanceType],
        amount = row[amount],
        version = row[AccountBalanceTable.version],
        createdAt = row[AccountBalanceTable.createdAt],
        updatedAt = row[AccountBalanceTable.updatedAt]
    )
}) {

    override fun getAccountBalanceById(id: UUID): AccountBalance {
        return super.findById(id) ?: throw AccountBalanceNotFoundByIdException(id)
    }

    override fun getAccountBalanceByAccountIdAndType(
        accountId: UUID,
        type: AccountBalanceType
    ): AccountBalance {
        return transaction {
            AccountBalanceTable.select {
                (AccountBalanceTable.accountId eq accountId) and
                        (accountBalanceType eq type)
            }
                .mapNotNull { mapToAccountBalance(it) }
                .singleOrNull()
        } ?: throw AccountBalanceNotFoundByAccountIdAndTypeException(accountId, type)
    }

    override fun getAccountBalancesByAccountId(
        accountId: UUID,
    ): List<AccountBalance> {
        val balances = transaction {
            AccountBalanceTable
                .select { AccountBalanceTable.accountId eq accountId }
                .map { mapToAccountBalance(it) }
        }

        if (balances.isEmpty()) {
            throw AccountBalancesNotFoundByAccountIdException(accountId)
        }

        return balances
    }

    override fun upsertAccountBalance(
        accountId: UUID,
        accountBalanceType: AccountBalanceType
    ) = try {
            getAccountBalanceByAccountIdAndType(accountId, accountBalanceType)
        } catch (ex: AccountBalanceNotFoundByAccountIdAndTypeException) {
            val accountBalance = AccountBalance(
                accountId = accountId,
                accountBalanceType = accountBalanceType,
                amount = BigDecimal(0.00)
            )
            super.create(accountBalance, ::buildAccountBalanceTable)
        }

    override fun updateAccountBalanceAmount(
        accountBalanceId: UUID,
        newAmount: BigDecimal
    ): AccountBalance {
        return getAccountBalanceById(accountBalanceId).let { balance ->
            super.update(balance.copy(amount = newAmount)){ updateStatement, accountBalance ->
                updateStatement[accountId] = accountBalance.accountId
                updateStatement[accountBalanceType] = accountBalance.accountBalanceType
                updateStatement[amount] = newAmount
                updateStatement[AccountBalanceTable.version] = accountBalance.version
                updateStatement[AccountBalanceTable.createdAt] = accountBalance.createdAt
                updateStatement[AccountBalanceTable.updatedAt] = accountBalance.updatedAt
            }
        }
    }

    private fun mapToAccountBalance(row: ResultRow): AccountBalance {
        return AccountBalance(
            id = row[AccountBalanceTable.id].value,
            accountId = row[accountId],
            accountBalanceType = row[accountBalanceType],
            amount = row[amount]
        )
    }
    private fun buildAccountBalanceTable(accountBalance: AccountBalance): UUID {
        return AccountBalanceTable.insertAndGetId {
            it[id] = accountBalance.id
            it[accountId] = accountBalance.accountId
            it[accountBalanceType] = accountBalance.accountBalanceType
            it[amount] = accountBalance.amount
            it[AccountBalanceTable.version] = accountBalance.version
            it[AccountBalanceTable.createdAt] = accountBalance.createdAt
            it[AccountBalanceTable.updatedAt] = accountBalance.updatedAt
        }.value
    }

}
