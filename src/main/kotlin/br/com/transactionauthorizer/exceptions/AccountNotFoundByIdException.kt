package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.UUID

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountNotFoundByIdException(
    accountId: UUID
) : RuntimeException("Account with accountId $accountId not found.")
