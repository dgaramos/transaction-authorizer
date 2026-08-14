package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.AccountSummary

data class AccountListResponse(
    val id: String,
    val name: String
) {
    companion object {
        fun fromSummary(summary: AccountSummary): AccountListResponse {
            return AccountListResponse(
                id = summary.id.toString(),
                name = summary.name
            )
        }
    }
}
