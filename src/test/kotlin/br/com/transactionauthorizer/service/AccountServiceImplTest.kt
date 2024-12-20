package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.repository.AccountRepository
import br.com.transactionauthorizer.service.implementations.AccountServiceImpl
import org.junit.jupiter.api.*
import org.mockito.Mock
import org.mockito.kotlin.*
import org.mockito.MockitoAnnotations

class AccountServiceImplTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var accountService: AccountService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        accountService = AccountServiceImpl(accountRepository)
    }

    @Test
    fun `should return all accounts successfully`() {
        val account1 = TestModelFactory.buildAccount(name = "Account 1")
        val account2 = TestModelFactory.buildAccount(name = "Account 2")

        whenever(accountRepository.getAllAccounts(offset = 0, limit = 2)).thenReturn(listOf(account1, account2))

        val accounts = accountService.getAllAccounts(offset = 0, limit = 2)

        Assertions.assertNotNull(accounts)
        Assertions.assertEquals(2, accounts.size)
        Assertions.assertEquals("Account 1", accounts[0].name)
        Assertions.assertEquals("Account 2", accounts[1].name)

        verify(accountRepository).getAllAccounts(offset = 0, limit = 2)
    }

    @Test
    fun `should return account by id successfully`() {
        val account = TestModelFactory.buildAccount(name = "Account 1")

        whenever(accountRepository.getAccountById(account.id)).thenReturn(account)

        val result = accountService.getAccountById(account.id)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(account.id, result.id)
        Assertions.assertEquals("Account 1", result.name)

        verify(accountRepository).getAccountById(account.id)
    }

    @Test
    fun `should create account successfully`() {
        val name = "New Account"
        val account = TestModelFactory.buildAccount(name = name)

        whenever(accountRepository.createAccount(any())).thenReturn(account)

        val createdAccount = accountService.createAccount(name)

        Assertions.assertNotNull(createdAccount)
        Assertions.assertEquals(account.id, createdAccount.id)
        Assertions.assertEquals(name, createdAccount.name)

    }
}
