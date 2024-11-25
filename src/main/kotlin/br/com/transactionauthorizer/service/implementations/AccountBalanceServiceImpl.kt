package br.com.transactionauthorizer.service.implementations

import org.springframework.stereotype.Service
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import br.com.transactionauthorizer.service.AccountBalanceService
import java.math.BigDecimal

@Service
class AccountBalanceServiceImpl(
    private val repository: AccountBalanceRepository
) : AccountBalanceService {

    override fun createAccountBalance(accountId: Long, type: AccountBalanceType): AccountBalance {
        return repository.upsertAccountBalance(accountId, type)
    }

    override fun getAccountBalanceByAccountIdAndType(accountId: Long, type: AccountBalanceType): AccountBalance {
        return repository.getAccountBalanceByAccountIdAndType(accountId, type)
    }

    override fun getAccountBalancesByAccountId(accountId: Long): List<AccountBalance> {
        return repository.getAccountBalancesByAccountId(accountId)
    }

    override fun updateAccountBalanceAmount(id: Long, newAmount: BigDecimal): AccountBalance {
        return repository.updateAccountBalanceAmount(id, newAmount)
    }
}