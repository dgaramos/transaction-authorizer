package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import java.math.BigDecimal
import java.util.*

interface AccountBalanceRepository {

    fun getAccountBalanceById(id: UUID): AccountBalance

    fun getAccountBalanceByAccountIdAndType(accountId: UUID, type: AccountBalanceType): AccountBalance

    fun getAccountBalancesByAccountId(accountId: UUID): List<AccountBalance>

    fun upsertAccountBalance(accountId: UUID, accountBalanceType: AccountBalanceType): AccountBalance

    fun updateAccountBalanceAmount(accountBalanceId: UUID, newAmount: BigDecimal): AccountBalance
}