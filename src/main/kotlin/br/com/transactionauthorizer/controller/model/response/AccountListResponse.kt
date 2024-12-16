package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.Account

data class AccountListResponse(
    val id: String,
    val name: String
) {
    companion object {
        fun fromAccount(account: Account): AccountListResponse {
            return AccountListResponse(
                id = account.id.toString(),
                name = account.name
            )
        }
    }
}
