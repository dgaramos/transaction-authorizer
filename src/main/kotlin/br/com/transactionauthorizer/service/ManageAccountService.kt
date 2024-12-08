package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountListResponse
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import org.springframework.http.ResponseEntity

interface ManageAccountService {
    fun getAllAccounts(): ResponseEntity<List<AccountListResponse>>
    fun getAccountById(id: Long): ResponseEntity<AccountResponse>
    fun createAccount(accountRequest: AccountRequest): ResponseEntity<AccountResponse>
}

