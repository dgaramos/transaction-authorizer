package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.UUID

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountBalanceNotFoundByIdException(accountBalanceId: UUID) :
    RuntimeException("Account balance with ID $accountBalanceId not found.")
