package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class AccountBalanceNotFoundByIdException(accountBalanceId: Long) :
    RuntimeException("Account balance with ID $accountBalanceId not found.")
