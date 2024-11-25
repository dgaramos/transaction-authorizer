package br.com.transactionauthorizer.repository.implementations

import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.repository.AccountRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository

@Repository
class AccountRepositoryImpl : AccountRepository {

    override fun getAllAccounts(): List<Account> {
        return transaction {
            AccountTable.selectAll().map {
                buildAccount(
                    id = it[AccountTable.id].value,
                    name = it[AccountTable.name]
                )
            }
        }
    }

    override fun getAccountById(id: Long): Account? {
        return transaction {
            AccountTable
                .select { AccountTable.id eq id }
                .mapNotNull {
                    buildAccount(
                        id = it[AccountTable.id].value,
                        name = it[AccountTable.name]
                    )
                }
                .singleOrNull()
        }
    }
    override fun createAccount(name: String): Account {
        return transaction {
            val id = AccountTable
                .insertAndGetId {
                    it[AccountTable.name] = name
                }.value

            buildAccount(id, name)
        }
    }

    private fun buildAccount(id: Long, name: String) = Account(
        id = id,
        name = name
    )
}
