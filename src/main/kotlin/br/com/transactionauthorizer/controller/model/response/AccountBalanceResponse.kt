package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.AccountBalance

data class AccountBalanceResponse(
    val id: Long,
    val accountId: Long,
    val type: String,
    val amount: String
) {
    companion object {
        fun fromAccountBalance(accountBalance: AccountBalance): AccountBalanceResponse {
            return AccountBalanceResponse(
                id = accountBalance.id!!,
                accountId = accountBalance.accountId,
                type = accountBalance.accountBalanceType.name,
                amount = accountBalance.amount.toString()
            )
        }
    }
}

data class AccountBalanceCreatedResponse(
    val id: Long,
    val accountId: Long,
    val type: String,
    val amount: String
) {
    companion object {
        fun fromAccountBalance(accountBalance: AccountBalance): AccountBalanceCreatedResponse {
            return AccountBalanceCreatedResponse(
                id = accountBalance.id!!,
                accountId = accountBalance.accountId,
                type = accountBalance.accountBalanceType.name,
                amount = accountBalance.amount.toString()
            )
        }
    }
}