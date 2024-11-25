package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.AccountBalanceRequest
import br.com.transactionauthorizer.controller.model.response.AccountBalanceCreatedResponse
import br.com.transactionauthorizer.controller.model.response.AccountBalanceResponse
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.model.AccountBalanceType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@Tag(name = "Account Balances", description = "Endpoints for managing account balances")
@RestController
@RequestMapping("/api/account-balances")
@CrossOrigin(maxAge = 3600)
class AccountBalanceController(
    private val accountBalanceService: AccountBalanceService
) {

    @Operation(summary = "Get account balances by account ID")
    @GetMapping("/{accountId}", produces = ["application/json"])
    fun getAccountBalancesByAccountId(@PathVariable accountId: Long): ResponseEntity<List<AccountBalanceResponse>> {
        val balances = accountBalanceService.getAccountBalancesByAccountId(accountId)
        val response = balances.map { AccountBalanceResponse.fromAccountBalance(it) }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @Operation(summary = "Get account balance by account ID and type")
    @GetMapping("/{accountId}/{type}", produces = ["application/json"])
    fun getAccountBalanceByAccountIdAndType(
        @PathVariable accountId: Long,
        @PathVariable type: AccountBalanceType
    ): ResponseEntity<AccountBalanceResponse> {
        val balance = accountBalanceService.getAccountBalanceByAccountIdAndType(accountId, type)
        val response = AccountBalanceResponse.fromAccountBalance(balance)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @Operation(summary = "Create a new account balance")
    @PostMapping("", consumes = ["application/json"], produces = ["application/json"])
    fun createAccountBalance(
        @RequestBody accountBalanceRequest: AccountBalanceRequest
    ): ResponseEntity<AccountBalanceCreatedResponse> {
        val createdBalance = accountBalanceService.createAccountBalance(
            accountBalanceRequest.accountId,
            accountBalanceRequest.type,
            BigDecimal(accountBalanceRequest.amount)
        )
        val response = AccountBalanceCreatedResponse.fromAccountBalance(createdBalance)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @Operation(summary = "Update the amount of an account balance")
    @PutMapping("/{id}", consumes = ["application/json"], produces = ["application/json"])
    fun updateAccountBalanceAmount(
        @PathVariable id: Long,
        @RequestBody accountBalanceRequest: AccountBalanceRequest
    ): ResponseEntity<AccountBalanceResponse> {
        val updatedBalance = accountBalanceService.updateAccountBalanceAmount(
            id,
            BigDecimal(accountBalanceRequest.amount)
        )
        val response = AccountBalanceResponse.fromAccountBalance(updatedBalance)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
