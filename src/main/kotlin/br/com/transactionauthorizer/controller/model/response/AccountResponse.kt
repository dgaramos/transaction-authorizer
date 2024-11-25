package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.model.AccountBalance

data class AccountResponse(
    val id: Long,
    val name: String,
    val balances: List<AccountBalanceResponse>
) {
    companion object {
        fun fromAccount(account: Account, balances: List<AccountBalance>): AccountResponse {
            return AccountResponse(
                id = account.id!!,
                name = account.name,
                balances = balances.map { AccountBalanceResponse.fromAccountBalance(it) }
            )
        }
    }
}
