package br.com.transactionauthorizer.service.implementations

import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.AccountDetail
import br.com.transactionauthorizer.model.AccountSummary
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.AccountService
import br.com.transactionauthorizer.service.ManageAccountService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ManageAccountServiceImpl(
    private val accountService: AccountService,
    private val accountBalanceService: AccountBalanceService
): ManageAccountService {

    override fun getAllAccounts(offset: Int, limit: Int): List<AccountSummary> =
        accountService.getAllAccounts(offset, limit).map { account ->
            AccountSummary(id = account.id, name = account.name)
        }

    override fun getAccountById(id: UUID): AccountDetail {
        val account = accountService.getAccountById(id)
        val balances = accountBalanceService.getAccountBalancesByAccountId(id)
        return AccountDetail(id = account.id, name = account.name, balances = balances)
    }

    override fun createAccount(name: String): AccountDetail {
        val account = accountService.createAccount(name)
        val balances = AccountBalanceType.entries.map { balanceType ->
            accountBalanceService.upsertAccountBalance(account.id, balanceType)
        }
        return AccountDetail(id = account.id, name = account.name, balances = balances)
    }
}
