package br.com.transactionauthorizer.model

import java.util.UUID

data class AccountSummary(
    val id: UUID,
    val name: String
)

data class AccountDetail(
    val id: UUID,
    val name: String,
    val balances: List<AccountBalance>
)
