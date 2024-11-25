package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import java.math.BigDecimal

interface AccountBalanceService {

    fun createAccountBalance(accountId: Long, type: AccountBalanceType, amount: BigDecimal): AccountBalance
    fun getAccountBalanceByAccountIdAndType(accountId: Long, type: AccountBalanceType): AccountBalance
    fun getAccountBalancesByAccountId(accountId: Long): List<AccountBalance>
    fun updateAccountBalanceAmount(id: Long, newAmount: BigDecimal): AccountBalance
}