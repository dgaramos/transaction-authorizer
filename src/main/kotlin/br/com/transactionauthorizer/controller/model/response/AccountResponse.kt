package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.AccountDetail

data class AccountResponse(
    val id: String,
    val name: String,
    val balances: List<AccountBalanceResponse>
) {
    companion object {
        fun fromDetail(detail: AccountDetail): AccountResponse {
            return AccountResponse(
                id = detail.id.toString(),
                name = detail.name,
                balances = detail.balances.map { AccountBalanceResponse.fromAccountBalance(it) }
            )
        }
    }
}
