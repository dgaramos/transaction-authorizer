package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.controller.model.response.AccountListResponse
import br.com.transactionauthorizer.controller.model.response.AccountResponse
import br.com.transactionauthorizer.service.ManageAccountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

class AccountControllerTest {

    @InjectMocks
    private lateinit var accountController: AccountController

    @Mock
    private lateinit var manageAccountService: ManageAccountService

    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build()
    }

    @Test
    fun `test get all accounts`() {
        val accountListResponse = listOf(
            AccountListResponse(id = UUID.randomUUID().toString(), name = "Account1"),
            AccountListResponse(id = UUID.randomUUID().toString(), name = "Account2")
        )

        whenever(manageAccountService.getAllAccounts(0, 10))
            .thenReturn(ResponseEntity.ok(accountListResponse))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts?offset=0&limit=10"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Account1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Account2"))
    }

    @Test
    fun `test get account by ID`() {
        val accountId = UUID.randomUUID()
        val accountResponse = AccountResponse(
            id = accountId.toString(),
            name = "Account1",
            balances = listOf()
        )

        whenever(manageAccountService.getAccountById(accountId))
            .thenReturn(ResponseEntity.ok(accountResponse))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/$accountId"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(accountId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Account1"))
    }

    @Test
    fun `test create account`() {
        val accountId = UUID.randomUUID()
        val accountRequest = AccountRequest(name = "NewAccount")
        val accountResponse = AccountResponse(
            id = accountId.toString(),
            name = "NewAccount",
            balances = listOf()
        )

        whenever(manageAccountService.createAccount(accountRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(accountResponse))

        val jsonRequest = objectMapper.writeValueAsString(accountRequest)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/accounts")
                .contentType("application/json")
                .content(jsonRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(accountId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NewAccount"))
    }
}