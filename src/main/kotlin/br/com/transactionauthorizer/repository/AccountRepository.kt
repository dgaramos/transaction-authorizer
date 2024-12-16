package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.Account
import java.util.*

interface AccountRepository {

    fun getAllAccounts(offset: Int = 0, limit: Int = 10): List<Account>
    fun getAccountById(id: UUID): Account
    fun createAccount(account: Account): Account
}
