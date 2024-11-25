package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import java.math.BigDecimal

interface AccountBalanceRepository {

    fun getAccountBalanceById(id: Long): AccountBalance

    fun getAccountBalanceByAccountIdAndType(accountId: Long, type: AccountBalanceType): AccountBalance

    fun getAccountBalancesByAccountId(accountId: Long): List<AccountBalance>

    fun upsertAccountBalance(accountId: Long, accountBalanceType: AccountBalanceType): AccountBalance

    fun updateAccountBalanceAmount(accountBalanceId: Long, newAmount: BigDecimal): AccountBalance
}