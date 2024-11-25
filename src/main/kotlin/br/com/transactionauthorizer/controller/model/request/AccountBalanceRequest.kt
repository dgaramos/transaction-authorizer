package br.com.transactionauthorizer.controller.model.request

import br.com.transactionauthorizer.model.AccountBalanceType

data class AccountBalanceRequest(
    val accountId: Long,
    val type: AccountBalanceType,
    val amount: String
)