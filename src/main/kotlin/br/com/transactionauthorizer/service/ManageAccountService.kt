package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.AccountDetail
import br.com.transactionauthorizer.model.AccountSummary
import java.util.UUID

interface ManageAccountService {
    fun getAllAccounts(offset: Int = 0, limit: Int = 10): List<AccountSummary>
    fun getAccountById(id: UUID): AccountDetail
    fun createAccount(name: String): AccountDetail
}
