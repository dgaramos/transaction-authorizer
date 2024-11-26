package br.com.transactionauthorizer.service.implementations

import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.repository.AccountRepository
import br.com.transactionauthorizer.service.AccountService
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(private val accountRepository: AccountRepository) : AccountService {

    override fun createAccount(name: String): Account {
        val account = Account(name = name)
        return accountRepository.createAccount(account)
    }

    override fun getAllAccounts(): List<Account> {
        return accountRepository.getAllAccounts()
    }

    override fun getAccountById(id: Long): Account {
        return accountRepository.getAccountById(id)
    }
}
