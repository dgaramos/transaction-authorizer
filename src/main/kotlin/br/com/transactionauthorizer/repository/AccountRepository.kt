package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.Account

interface AccountRepository {

    fun getAllAccounts(): List<Account>
    fun getAccountById(id: Long): Account
    fun createAccount(name: String): Account
}
