package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.Account

data class AccountListResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun fromAccount(account: Account): AccountListResponse {
            return AccountListResponse(
                id = account.id!!,
                name = account.name
            )
        }
    }
}
