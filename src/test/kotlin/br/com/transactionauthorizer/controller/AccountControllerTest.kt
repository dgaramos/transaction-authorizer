package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountListResponse
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import br.com.transactionauthorizer.service.implementations.ManageAccountServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class AccountControllerTest {

    private lateinit var manageAccountService: ManageAccountServiceImpl
    private lateinit var accountController: AccountController

    @BeforeEach
    fun setUp() {
        manageAccountService = mock(ManageAccountServiceImpl::class.java)
        accountController = AccountController(manageAccountService)
    }

    @Test
    fun `should retrieve all accounts`() {
        val accounts = listOf(
            AccountListResponse(id = 1L, name = "Account 1"),
            AccountListResponse(id = 2L, name = "Account 2")
        )
        `when`(manageAccountService.getAllAccounts()).thenReturn(ResponseEntity(accounts, HttpStatus.OK))

        val response = accountController.getAllAccounts()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
        verify(manageAccountService, times(1)).getAllAccounts()
    }

    @Test
    fun `should retrieve account by ID`() {
        val accountId = 1L
        val accountResponse = AccountResponse(
            id = accountId,
            name = "Account 1",
            balances = emptyList()
        )
        `when`(manageAccountService.getAccountById(accountId)).thenReturn(ResponseEntity(accountResponse, HttpStatus.OK))

        val response = accountController.getAccountById(accountId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Account 1", response.body?.name)
        verify(manageAccountService, times(1)).getAccountById(accountId)
    }

    @Test
    fun `should create a new account`() {
        val accountRequest = AccountRequest(name = "New Account")
        val accountResponse = AccountResponse(
            id = 1L,
            name = "New Account",
            balances = emptyList()
        )
        `when`(manageAccountService.createAccount(accountRequest)).thenReturn(ResponseEntity(accountResponse, HttpStatus.CREATED))

        val response = accountController.createAccount(accountRequest)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("New Account", response.body?.name)
        verify(manageAccountService, times(1)).createAccount(accountRequest)
    }
}
