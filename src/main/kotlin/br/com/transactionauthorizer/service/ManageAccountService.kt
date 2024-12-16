package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountListResponse
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import org.springframework.http.ResponseEntity
import java.util.UUID

interface ManageAccountService {
    fun getAllAccounts(offset: Int = 0, limit: Int = 10): ResponseEntity<List<AccountListResponse>>
    fun getAccountById(id: UUID): ResponseEntity<AccountResponse>
    fun createAccount(accountRequest: AccountRequest): ResponseEntity<AccountResponse>
}

