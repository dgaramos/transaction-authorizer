package br.com.transactionauthorizer.exceptions

import br.com.transactionauthorizer.model.AccountBalanceType
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.UUID

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountBalanceNotFoundByAccountIdAndTypeException(
    accountId: UUID,
    type: AccountBalanceType
) : RuntimeException("Account balance with accountId $accountId and type ${type.name} not found.")
