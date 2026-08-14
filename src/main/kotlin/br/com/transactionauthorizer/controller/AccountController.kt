package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountListResponse
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import br.com.transactionauthorizer.service.ManageAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "Accounts", description = "Endpoints for managing accounts")
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(maxAge = 3600)
class AccountController(
    private val manageAccountService: ManageAccountService
) {

    @Operation(summary = "Get all accounts")
    @GetMapping("", produces = ["application/json"])
    fun getAllAccounts(
        @RequestParam offset: Int = 0,
        @RequestParam limit: Int = 10
    ): ResponseEntity<List<AccountListResponse>> {
        val summaries = manageAccountService.getAllAccounts(offset = offset, limit = limit)
        return ResponseEntity.ok(summaries.map { AccountListResponse.fromSummary(it) })
    }

    @Operation(summary = "Get a specific account by ID with all account balances")
    @GetMapping("/{id}", produces = ["application/json"])
    fun getAccountById(
        @Parameter(description = "ID of the account to retrieve") @PathVariable id: String
    ): ResponseEntity<AccountResponse> {
        val detail = manageAccountService.getAccountById(UUID.fromString(id))
        return ResponseEntity.ok(AccountResponse.fromDetail(detail))
    }

    @Operation(summary = "Create a new account")
    @PostMapping("", consumes = ["application/json"], produces = ["application/json"])
    fun createAccount(
        @Parameter(description = "Request payload for creating a new account") @RequestBody accountRequest: AccountRequest
    ): ResponseEntity<AccountResponse> {
        val detail = manageAccountService.createAccount(accountRequest.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponse.fromDetail(detail))
    }
}
