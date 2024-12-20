package br.com.transactionauthorizer.controller.model.request

import br.com.transactionauthorizer.model.AccountBalanceType

data class CreateAccountBalanceRequest(
    val accountId: String,
    val type: AccountBalanceType
)