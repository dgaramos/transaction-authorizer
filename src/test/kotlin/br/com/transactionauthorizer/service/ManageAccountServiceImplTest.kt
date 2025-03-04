package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.service.implementations.ManageAccountServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.random.Random

class ManageAccountServiceImplTest {

    private lateinit var accountService: AccountService
    private lateinit var accountBalanceService: AccountBalanceService
    private lateinit var manageAccountService: ManageAccountService

    @BeforeEach
    fun setUp() {
        accountService = mock()
        accountBalanceService = mock()
        manageAccountService = ManageAccountServiceImpl(accountService, accountBalanceService)
    }

    @Test
    fun `should retrieve all accounts`() {
        val accounts = listOf(
            TestModelFactory.buildAccount(name = "Account 1"),
            TestModelFactory.buildAccount(name = "Account 2")
        )
        whenever(accountService.getAllAccounts()).thenReturn(accounts)

        val response = manageAccountService.getAllAccounts()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
        verify(accountService, times(1)).getAllAccounts()
    }

    @Test
    fun `should retrieve account by ID`() {
        val accountId = UUID.randomUUID()
        val account = TestModelFactory.buildAccount(id = accountId, name = "Account 1")
        val balances = listOf(
            AccountBalance(accountId = accountId, accountBalanceType = AccountBalanceType.CASH, amount = 100.toBigDecimal())
        )
        whenever(accountService.getAccountById(accountId)).thenReturn(account)
        whenever(accountBalanceService.getAccountBalancesByAccountId(accountId)).thenReturn(balances)

        val response = manageAccountService.getAccountById(accountId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Account 1", response.body?.name)
        assertEquals(1, response.body?.balances?.size)
        verify(accountService, times(1)).getAccountById(accountId)
        verify(accountBalanceService, times(1)).getAccountBalancesByAccountId(accountId)
    }

    @Test
    fun `should create a new account`() {
        val accountRequest = AccountRequest(name = "New Account")
        val account = TestModelFactory.buildAccount( name = accountRequest.name)
        val balances = AccountBalanceType.entries.map { balanceType ->
            TestModelFactory.buildAccountBalance(id = UUID.randomUUID(), accountId = account.id, accountBalanceType = balanceType, amount = 0.toBigDecimal())
        }
        whenever(accountService.createAccount(accountRequest.name)).thenReturn(account)
        whenever(accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.CASH)).thenReturn(balances[0])
        whenever(accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.FOOD)).thenReturn(balances[1])
        whenever(accountBalanceService.upsertAccountBalance(account.id, AccountBalanceType.MEAL)).thenReturn(balances[2])

        val response = manageAccountService.createAccount(accountRequest)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("New Account", response.body?.name)
        assertEquals(3, response.body?.balances?.size)
        verify(accountService, times(1)).createAccount(accountRequest.name)
        verify(accountBalanceService, times(1)).upsertAccountBalance(account.id, AccountBalanceType.CASH)
        verify(accountBalanceService, times(1)).upsertAccountBalance(account.id, AccountBalanceType.MEAL)
        verify(accountBalanceService, times(1)).upsertAccountBalance(account.id, AccountBalanceType.FOOD)
    }
}
