package br.com.transactionauthorizer.exceptions

import br.com.transactionauthorizer.model.AccountBalanceType
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class AccountBalanceNotFoundByAccountIdAndTypeException(
    accountId: Long,
    type: AccountBalanceType
) : RuntimeException("Account balance with accountId $accountId and type ${type.name} not found.")
