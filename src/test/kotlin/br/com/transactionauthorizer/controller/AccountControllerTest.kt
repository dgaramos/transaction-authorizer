package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.service.AccountService
import br.com.transactionauthorizer.controller.model.request.AccountRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AccountControllerTest {

    @InjectMocks
    private lateinit var accountController: AccountController

    @Mock
    private lateinit var accountService: AccountService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build()
    }

    @Test
    fun `test get all accounts`() {
        val accounts = listOf(
            Account(1L, "Account 1"),
            Account(2L, "Account 2")
        )

        Mockito.`when`(accountService.getAllAccounts()).thenReturn(accounts)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Account 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Account 2"))

        Mockito.verify(accountService, Mockito.times(1)).getAllAccounts()
    }

    @Test
    fun `test get account by id`() {
        val account = Account(1L, "Account 1")

        Mockito.`when`(accountService.getAccountById(1L)).thenReturn(account)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Account 1"))

        Mockito.verify(accountService, Mockito.times(1)).getAccountById(1L)
    }

    @Test
    fun `test create account`() {
        val request = AccountRequest("New Account")
        val createdAccount = Account(1L, "New Account")

        Mockito.`when`(accountService.createAccount("New Account")).thenReturn(createdAccount)

        val objectMapper = ObjectMapper()
        val jsonRequest = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/accounts")
                .contentType("application/json")
                .content(jsonRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Account"))

        Mockito.verify(accountService, Mockito.times(1)).createAccount("New Account")
    }
}
