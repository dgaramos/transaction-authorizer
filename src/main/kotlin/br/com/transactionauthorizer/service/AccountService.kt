package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.Account

interface AccountService {
    fun createAccount(name: String): Account
    fun getAllAccounts(): List<Account>
    fun getAccountById(id: Long): Account
}
