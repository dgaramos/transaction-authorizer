package br.com.transactionauthorizer.service.implementations

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountListResponse
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.AccountService
import br.com.transactionauthorizer.service.ManageAccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ManageAccountServiceImpl(
    private val accountService: AccountService,
    private val accountBalanceService: AccountBalanceService
): ManageAccountService {

    override fun getAllAccounts(): ResponseEntity<List<AccountListResponse>> {
        val response = accountService.getAllAccounts().map { account ->
            AccountListResponse.fromAccount(account)
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    override fun getAccountById(id: Long): ResponseEntity<AccountResponse> {
        val account = accountService.getAccountById(id)
        val balances = accountBalanceService.getAccountBalancesByAccountId(id)
        val response = AccountResponse.fromAccount(account, balances)
        return ResponseEntity(response, HttpStatus.OK)
    }

    override fun createAccount(accountRequest: AccountRequest): ResponseEntity<AccountResponse> {
        val account = accountService.createAccount(accountRequest.name)
        val balances = AccountBalanceType.entries.map { balanceType ->
            accountBalanceService.upsertAccountBalance(account.id!!, balanceType)
        }
        val response = AccountResponse.fromAccount(account, balances)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
