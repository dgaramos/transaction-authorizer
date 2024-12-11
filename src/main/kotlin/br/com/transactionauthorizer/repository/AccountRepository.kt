package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.Account

interface AccountRepository {

    fun getAllAccounts(offset: Int = 0, limit: Int = 10): List<Account>
    fun getAccountById(id: Long): Account
    fun createAccount(account: Account): Account
}
