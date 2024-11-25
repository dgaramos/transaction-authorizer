package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.controller.model.response.CardTransactionResponse
import br.com.transactionauthorizer.service.CardTransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Card Transactions", description = "Endpoints for managing card transactions")
@RestController
@RequestMapping("/api/card-transactions")
@CrossOrigin(maxAge = 3600)
class CardTransactionController(
    private val cardTransactionService: CardTransactionService
) {

    @Operation(summary = "Get all card transactions")
    @GetMapping("", produces = ["application/json"])
    fun getAllCardTransactions(): ResponseEntity<List<CardTransactionResponse>> {
        val transactions = cardTransactionService.getAllTransactions()
        val response = transactions.map { CardTransactionResponse.fromCardTransaction(it) }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @Operation(summary = "Get a specific card transaction by ID")
    @GetMapping("/{id}", produces = ["application/json"])
    fun getCardTransactionById(
        @Parameter(description = "ID of the transaction to retrieve") @PathVariable id: Long
    ): ResponseEntity<CardTransactionResponse> {
        val transaction = cardTransactionService.getTransactionById(id)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val response = CardTransactionResponse.fromCardTransaction(transaction)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
