package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.CreateAccountBalanceRequest
import br.com.transactionauthorizer.controller.model.response.AccountBalanceCreatedResponse
import br.com.transactionauthorizer.service.AccountBalanceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Account Balances", description = "Endpoints for managing account balances")
@RestController
@RequestMapping("/api/account-balances")
@CrossOrigin(maxAge = 3600)
class AccountBalanceController(
    private val accountBalanceService: AccountBalanceService
) {

    @Operation(summary = "Create a new account balance")
    @PostMapping("", consumes = ["application/json"], produces = ["application/json"])
    fun createAccountBalance(
        @RequestBody createAccountBalanceRequest: CreateAccountBalanceRequest
    ): ResponseEntity<AccountBalanceCreatedResponse> {
        val createdBalance = accountBalanceService.upsertAccountBalance(
            createAccountBalanceRequest.accountId,
            createAccountBalanceRequest.type
        )
        val response = AccountBalanceCreatedResponse.fromAccountBalance(createdBalance)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}
