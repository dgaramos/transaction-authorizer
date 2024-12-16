package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.Account
import java.util.UUID

interface AccountService {
    fun createAccount(name: String): Account
    fun getAllAccounts(offset: Int = 0, limit: Int = 10): List<Account>
    fun getAccountById(id: UUID): Account
}
