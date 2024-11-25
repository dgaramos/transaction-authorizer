package br.com.transactionauthorizer.controller.model.request

import br.com.transactionauthorizer.model.AccountBalanceType

data class CreateAccountBalanceRequest(
    val accountId: Long,
    val type: AccountBalanceType
)