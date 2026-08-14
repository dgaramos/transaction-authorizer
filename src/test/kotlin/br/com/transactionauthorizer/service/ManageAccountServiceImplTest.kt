package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.service.implementations.ManageAccountServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class ManageAccountServiceImplTest {

    private lateinit var accountService: AccountService
    private lateinit var accountBalanceService: AccountBalanceService
    private lateinit var manageAccountService: ManageAccountService

    @BeforeEach
    fun setUp() {
        accountService = mockk()
        accountBalanceService = mockk()
        manageAccountService = ManageAccountServiceImpl(accountService, accountBalanceService)
    }

    @Test
    fun `should retrieve all accounts`() {
        val accounts = listOf(
            TestModelFactory.buildAccount(name = "Account 1"),
            TestModelFactory.buildAccount(name = "Account 2")
        )
        every { accountService.getAllAccounts() } returns accounts

        val summaries = manageAccountService.getAllAccounts()

        assertEquals(2, summaries.size)
        assertEquals("Account 1", summaries[0].name)
        verify(exactly = 1) { accountService.getAllAccounts() }
    }

    @Test
    fun `should retrieve account by ID`() {
        val accountId = UUID.randomUUID()
        val account = TestModelFactory.buildAccount(id = accountId, name = "Account 1")
        val balances = listOf(
            AccountBalance(accountId = accountId, accountBalanceType = AccountBalanceType.CASH, amount = 100.toBigDecimal())
        )
        every { accountService.getAccountById(accountId) } returns account
        every { accountBalanceService.getAccountBalancesByAccountId(accountId) } returns balances

        val detail = manageAccountService.getAccountById(accountId)

        assertEquals("Account 1", detail.name)
        assertEquals(1, detail.balances.size)
        verify(exactly = 1) { accountService.getAccountById(accountId) }
        verify(exactly = 1) { accountBalanceService.getAccountBalancesByAccountId(accountId) }
    }

    @Test
    fun `should create a new account`() {
        val account = TestModelFactory.buildAccount(name = "New Account")
        val balances = AccountBalanceType.entries.map { balanceType ->
            TestModelFactory.buildAccountBalance(id = UUID.randomUUID(), accountId = account.id, accountBalanceType = balanceType, amount = 0.toBigDecimal())
        }
        every { accountService.createAccount("New Account") } returns account
        every { accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.CASH) } returns balances[0]
        every { accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.FOOD) } returns balances[1]
        every { accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.MEAL) } returns balances[2]

        val detail = manageAccountService.createAccount("New Account")

        assertEquals("New Account", detail.name)
        assertEquals(3, detail.balances.size)
        verify(exactly = 1) { accountService.createAccount("New Account") }
        verify(exactly = 1) { accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.CASH) }
        verify(exactly = 1) { accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.MEAL) }
        verify(exactly = 1) { accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.FOOD) }
    }
}
