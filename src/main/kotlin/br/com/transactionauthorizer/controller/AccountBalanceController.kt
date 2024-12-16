package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.CreateAccountBalanceRequest
import br.com.transactionauthorizer.controller.model.response.AccountBalanceCreatedResponse
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.CardTransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Account Balances", description = "Endpoints for managing account balances")
@RestController
@RequestMapping("/api/account-balances")
@CrossOrigin(maxAge = 3600)
class AccountBalanceController(
    private val accountBalanceService: AccountBalanceService,
    private val cardTransactionService: CardTransactionService
) {

    @Operation(summary = "Create a new account balance")
    @PostMapping("", consumes = ["application/json"], produces = ["application/json"])
    fun createAccountBalance(
        @RequestBody createAccountBalanceRequest: CreateAccountBalanceRequest
    ): ResponseEntity<AccountBalanceCreatedResponse> {
        val createdBalance = accountBalanceService.upsertAccountBalance(
            UUID.fromString(createAccountBalanceRequest.accountId),
            createAccountBalanceRequest.type
        )
        val response = AccountBalanceCreatedResponse.fromAccountBalance(createdBalance, emptyList())
        return ResponseEntity(response, HttpStatus.CREATED)
    }


    @Operation(summary = "Get account balance info and its transactions")
    @GetMapping("/{id}", produces = ["application/json"])
    fun getAccountBalance(
        @Parameter(description = "ID of the account balance to retrieve") @PathVariable id: String,
        @Parameter(description = "Offset of the transaction pagination") @RequestParam transactionOffset: Int = 0,
        @Parameter(description = "Limit of the transaction pagination") @RequestParam transactionLimit: Int = 10
    ): ResponseEntity<AccountBalanceCreatedResponse> {
        val response = accountBalanceService.getAccountBalanceById(UUID.fromString(id)).let { balance ->
            cardTransactionService.getAllTransactionsByAccountBalanceId(
                accountBalanceId = balance.id,
                offset = transactionOffset,
                limit = transactionLimit
            ).let { transactions ->
                AccountBalanceCreatedResponse.fromAccountBalance(balance, transactions)
            }
        }
        return ResponseEntity(response, HttpStatus.OK)
    }
}
