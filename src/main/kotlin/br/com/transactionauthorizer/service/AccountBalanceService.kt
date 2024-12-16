package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import java.math.BigDecimal
import java.util.*

interface AccountBalanceService {

    fun getAccountBalanceById(id: UUID): AccountBalance
    fun upsertAccountBalance(accountId: UUID, type: AccountBalanceType): AccountBalance
    fun getAccountBalanceByAccountIdAndType(accountId: UUID, type: AccountBalanceType): AccountBalance
    fun getAccountBalancesByAccountId(accountId: UUID): List<AccountBalance>
    fun updateAccountBalanceAmount(id: UUID, newAmount: BigDecimal): AccountBalance
}