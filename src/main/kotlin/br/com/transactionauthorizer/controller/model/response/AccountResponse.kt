package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.Account

data class AccountResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun fromAccount(account: Account): AccountResponse {
            return AccountResponse(
                id = account.id!!,
                name = account.name
            )
        }
    }
}
