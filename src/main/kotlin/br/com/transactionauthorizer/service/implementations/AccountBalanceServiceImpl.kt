package br.com.transactionauthorizer.service.implementations

import org.springframework.stereotype.Service
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import br.com.transactionauthorizer.service.AccountBalanceService
import java.math.BigDecimal
import java.util.UUID

@Service
class AccountBalanceServiceImpl(
    private val repository: AccountBalanceRepository
) : AccountBalanceService {

    override fun getAccountBalanceById(id: UUID): AccountBalance {
        return repository.getAccountBalanceById(id)
    }

    override fun upsertAccountBalance(accountId: UUID, type: AccountBalanceType): AccountBalance {
        return repository.upsertAccountBalance(accountId, type)
    }

    override fun getAccountBalanceByAccountIdAndType(accountId: UUID, type: AccountBalanceType): AccountBalance {
        return repository.getAccountBalanceByAccountIdAndType(accountId, type)
    }

    override fun getAccountBalancesByAccountId(accountId: UUID): List<AccountBalance> {
        return repository.getAccountBalancesByAccountId(accountId)
    }

    override fun updateAccountBalanceAmount(id: UUID, newAmount: BigDecimal): AccountBalance {
        return repository.updateAccountBalanceAmount(id, newAmount)
    }
}