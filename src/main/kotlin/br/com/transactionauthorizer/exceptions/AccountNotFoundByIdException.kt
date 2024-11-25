package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountNotFoundByIdException(
    accountId: Long
) : RuntimeException("Account with accountId $accountId not found.")
