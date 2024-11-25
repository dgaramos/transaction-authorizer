package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import br.com.transactionauthorizer.service.AccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Accounts", description = "Endpoints for managing accounts")
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(maxAge = 3600)
class AccountController(
    private val accountService: AccountService
) {

    @Operation(summary = "Get all accounts")
    @GetMapping("", produces = ["application/json"])
    fun getAllAccounts(): ResponseEntity<List<AccountResponse>> {
        val accounts = accountService.getAllAccounts()
        val response = accounts.map { AccountResponse.fromAccount(it) }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @Operation(summary = "Get a specific account by ID")
    @GetMapping("/{id}", produces = ["application/json"])
    fun getAccountById(
        @Parameter(description = "ID of the account to retrieve") @PathVariable id: Long
    ): ResponseEntity<AccountResponse> {
        val account = accountService.getAccountById(id)
        val response = account?.let { AccountResponse.fromAccount(it) }
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @Operation(summary = "Create a new account")
    @PostMapping("", consumes = ["application/json"], produces = ["application/json"])
    fun createAccount(
        @Parameter(description = "Request payload for creating a new account") @RequestBody accountRequest: AccountRequest
    ): ResponseEntity<AccountResponse> {
        val createdAccount = accountService.createAccount(accountRequest.name)
        val response = AccountResponse.fromAccount(createdAccount)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
