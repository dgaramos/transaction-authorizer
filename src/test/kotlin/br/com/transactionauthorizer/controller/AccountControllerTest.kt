package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.AccountRequest
import br.com.transactionauthorizer.model.AccountDetail
import br.com.transactionauthorizer.model.AccountSummary
import br.com.transactionauthorizer.service.ManageAccountService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.UUID

class AccountControllerTest {

    @InjectMockKs
    private lateinit var accountController: AccountController

    @MockK
    private lateinit var manageAccountService: ManageAccountService

    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build()
    }

    @Test
    fun `test get all accounts`() {
        every { manageAccountService.getAllAccounts(0, 10) } returns listOf(
            AccountSummary(id = UUID.randomUUID(), name = "Account1"),
            AccountSummary(id = UUID.randomUUID(), name = "Account2")
        )

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts?offset=0&limit=10"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Account1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Account2"))
    }

    @Test
    fun `test get account by ID`() {
        val accountId = UUID.randomUUID()
        every { manageAccountService.getAccountById(accountId) } returns AccountDetail(
            id = accountId,
            name = "Account1",
            balances = listOf()
        )

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/$accountId"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(accountId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Account1"))
    }

    @Test
    fun `test create account`() {
        val accountId = UUID.randomUUID()
        val accountRequest = AccountRequest(name = "NewAccount")
        every { manageAccountService.createAccount("NewAccount") } returns AccountDetail(
            id = accountId,
            name = "NewAccount",
            balances = listOf()
        )

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
