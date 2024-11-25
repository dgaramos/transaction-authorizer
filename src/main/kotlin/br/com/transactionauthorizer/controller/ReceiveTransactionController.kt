package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.controller.model.response.ReceiveTransactionResponse
import br.com.transactionauthorizer.service.ReceiveTransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Transactions receiver", description = "Endpoint for receiving transactions")
@RestController
@RequestMapping("/api/receive-transactions")
@CrossOrigin(maxAge = 3600)
class ReceiveTransactionController(
    private val receiveTransactionService: ReceiveTransactionService
) {

    @Operation(summary = "Receives a new transaction")
    @PostMapping("", consumes = ["application/json"], produces = ["application/json"])
    fun receiveTransaction(
        @Parameter(description = "Request payload for creating a transaction") @RequestBody receivedTransactionRequest: ReceivedTransactionRequest
    ): ResponseEntity<ReceiveTransactionResponse> {
        val createdTransactionCode = receiveTransactionService.receiveTransaction(receivedTransactionRequest)
        val response = ReceiveTransactionResponse.fromCode(createdTransactionCode)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
