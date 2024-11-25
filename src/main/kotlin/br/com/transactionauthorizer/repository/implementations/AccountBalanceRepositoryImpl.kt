package br.com.transactionauthorizer.repository.implementations

import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByAccountIdAndTypeException
import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByIdException
import br.com.transactionauthorizer.exceptions.AccountBalancesNotFoundByAccountIdException
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.table.AccountBalanceTable
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class AccountBalanceRepositoryImpl : AccountBalanceRepository {

    override fun getAccountBalanceById(id: Long): AccountBalance {
        return transaction {
            AccountBalanceTable.select { AccountBalanceTable.id eq id }
                .mapNotNull { mapToAccountBalance(it) }
                .singleOrNull()
        } ?: throw AccountBalanceNotFoundByIdException(id)
    }

    override fun getAccountBalanceByAccountIdAndType(
        accountId: Long,
        type: AccountBalanceType
    ): AccountBalance {
        return transaction {
            AccountBalanceTable.select {
                (AccountBalanceTable.accountId eq accountId) and
                        (AccountBalanceTable.accountBalanceType eq type)
            }
                .mapNotNull { mapToAccountBalance(it) }
                .singleOrNull()
        } ?: throw AccountBalanceNotFoundByAccountIdAndTypeException(accountId, type)
    }

    override fun getAccountBalancesByAccountId(
        accountId: Long,
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

    override fun createAccountBalance(
        accountId: Long,
        accountBalanceType: AccountBalanceType,
        amount: BigDecimal
    ): AccountBalance {
        var createdAccountBalance: AccountBalance? = null

        transaction {
            val id = AccountBalanceTable.insertAndGetId {
                it[AccountBalanceTable.accountId] = accountId
                it[AccountBalanceTable.accountBalanceType] = accountBalanceType
                it[AccountBalanceTable.amount] = amount
            }.value

            createdAccountBalance = getAccountBalanceById(id)
        }

        return createdAccountBalance!!
    }

    override fun updateAccountBalanceAmount(
        accountBalanceId: Long,
        newAmount: BigDecimal
    ): AccountBalance {
        return transaction {
            val updatedRows = AccountBalanceTable.update(
                { AccountBalanceTable.id eq accountBalanceId }
            ) { it[amount] = newAmount }

            if (updatedRows > 0) {
                getAccountBalanceById(accountBalanceId)
            } else {
                throw AccountBalanceNotFoundByIdException(accountBalanceId)
            }
        }
    }

    private fun mapToAccountBalance(row: ResultRow): AccountBalance {
        return AccountBalance(
            id = row[AccountBalanceTable.id].value,
            accountId = row[AccountBalanceTable.accountId],
            accountBalanceType = row[AccountBalanceTable.accountBalanceType],
            amount = row[AccountBalanceTable.amount]
        )
    }
}
