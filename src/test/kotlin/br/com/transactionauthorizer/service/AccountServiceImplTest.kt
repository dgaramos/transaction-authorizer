package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.repository.AccountRepository
import br.com.transactionauthorizer.service.implementations.AccountServiceImpl
import org.junit.jupiter.api.*
import org.mockito.Mock
import org.mockito.Mockito.*
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
        val account1 = Account(id = 1L, name = "Account 1")
        val account2 = Account(id = 2L, name = "Account 2")

        `when`(accountRepository.getAllAccounts()).thenReturn(listOf(account1, account2))

        val accounts = accountService.getAllAccounts()

        Assertions.assertNotNull(accounts)
        Assertions.assertEquals(2, accounts.size)
        Assertions.assertEquals("Account 1", accounts[0].name)
        Assertions.assertEquals("Account 2", accounts[1].name)

        verify(accountRepository).getAllAccounts()
    }

    @Test
    fun `should return account by id successfully`() {
        val account = Account(id = 1L, name = "Account 1")

        `when`(accountRepository.getAccountById(1L)).thenReturn(account)

        val result = accountService.getAccountById(1L)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(1L, result?.id)
        Assertions.assertEquals("Account 1", result?.name)

        verify(accountRepository).getAccountById(1L)
    }

    @Test
    fun `should create account successfully`() {
        val name = "New Account"
        val account = Account(id = 1L, name = name)

        `when`(accountRepository.createAccount(name)).thenReturn(account)

        val createdAccount = accountService.createAccount(name)

        Assertions.assertNotNull(createdAccount)
        Assertions.assertEquals(1L, createdAccount.id)
        Assertions.assertEquals(name, createdAccount.name)

        verify(accountRepository).createAccount(name)
    }
}
