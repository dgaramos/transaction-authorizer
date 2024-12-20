package br.com.transactionauthorizer.controller.model.response

import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.CardTransaction
import java.util.UUID

data class AccountBalanceResponse(
    val id: UUID,
    val accountId: UUID,
    val type: String,
    val amount: String
) {
    companion object {
        fun fromAccountBalance(accountBalance: AccountBalance): AccountBalanceResponse {
            return AccountBalanceResponse(
                id = accountBalance.id,
                accountId = accountBalance.accountId,
                type = accountBalance.accountBalanceType.name,
                amount = accountBalance.amount.toString()
            )
        }
    }
}

data class AccountBalanceCreatedResponse(
    val id: UUID,
    val accountId: UUID,
    val type: String,
    val amount: String,
    val transactions: List<CardTransactionResponse>
) {
    companion object {
        fun fromAccountBalance(accountBalance: AccountBalance, transactions: List<CardTransaction>): AccountBalanceCreatedResponse {
            return AccountBalanceCreatedResponse(
                id = accountBalance.id,
                accountId = accountBalance.accountId,
                type = accountBalance.accountBalanceType.name,
                amount = accountBalance.amount.toString(),
                transactions = transactions.map { CardTransactionResponse.fromCardTransaction(it) }
            )
        }
    }
}