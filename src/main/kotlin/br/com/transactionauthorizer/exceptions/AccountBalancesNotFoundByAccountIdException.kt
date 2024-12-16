package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.UUID

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountBalancesNotFoundByAccountIdException(accountId: UUID) :
    RuntimeException("Account balances with accountId $accountId were not found.")
