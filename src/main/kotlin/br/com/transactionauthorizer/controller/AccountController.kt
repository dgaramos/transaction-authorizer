package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountListResponse
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import br.com.transactionauthorizer.service.implementations.ManageAccountServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Accounts", description = "Endpoints for managing accounts")
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(maxAge = 3600)
class AccountController(
    private val manageAccountService: ManageAccountServiceImpl
) {

    @Operation(summary = "Get all accounts")
    @GetMapping("", produces = ["application/json"])
    fun getAllAccounts(): ResponseEntity<List<AccountListResponse>> {
        return manageAccountService.getAllAccounts()
    }

    @Operation(summary = "Get a specific account by ID with all account balances")
    @GetMapping("/{id}", produces = ["application/json"])
    fun getAccountById(
        @Parameter(description = "ID of the account to retrieve") @PathVariable id: Long
    ): ResponseEntity<AccountResponse> {
        return manageAccountService.getAccountById(id)
    }

    @Operation(summary = "Create a new account")
    @PostMapping("", consumes = ["application/json"], produces = ["application/json"])
    fun createAccount(
        @Parameter(description = "Request payload for creating a new account") @RequestBody accountRequest: AccountRequest
    ): ResponseEntity<AccountResponse> {
        return manageAccountService.createAccount(accountRequest)
    }
}