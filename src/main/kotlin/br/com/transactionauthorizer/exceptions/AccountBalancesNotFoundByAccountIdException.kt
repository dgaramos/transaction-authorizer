package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class AccountBalancesNotFoundByAccountIdException(accountId: Long) :
    RuntimeException("Account balances with accountId $accountId were not found.")
