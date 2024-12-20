package br.com.transactionauthorizer.repository.implementations

import br.com.transactionauthorizer.exceptions.AccountNotFoundByIdException
import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.repository.AccountRepository
import br.com.transactionauthorizer.repository.BaseRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AccountRepositoryImpl : AccountRepository, BaseRepository<Account, AccountTable>(AccountTable, { row ->
    Account(
        id = row[AccountTable.id].value,
        name = row[AccountTable.name],
        version = row[AccountTable.version],
        createdAt = row[AccountTable.createdAt],
        updatedAt = row[AccountTable.updatedAt]
    )
}) {

    override fun getAllAccounts(offset: Int, limit: Int): List<Account> {
        return super.findAll(offset, limit)
    }

    override fun getAccountById(id: UUID): Account {
        return super.findById(id) ?: throw AccountNotFoundByIdException(id)
    }

     override fun createAccount(account: Account): Account {
         return super.create(account, ::buildAccountTable)
    }

    private fun buildAccountTable(account: Account): UUID {
        return AccountTable.insertAndGetId {
            it[AccountTable.id] = account.id
            it[name] = account.name
            it[AccountTable.version] = account.version
            it[AccountTable.createdAt] = account.createdAt
            it[AccountTable.updatedAt] = account.updatedAt
        }.value
    }
}
