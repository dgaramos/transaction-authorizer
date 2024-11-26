package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.CreateAccountBalanceRequest
import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.service.AccountBalanceService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

class AccountBalanceControllerTest {

    @InjectMocks
    private lateinit var accountBalanceController: AccountBalanceController

    @Mock
    private lateinit var accountBalanceService: AccountBalanceService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(accountBalanceController).build()
    }

    @Test
    fun `test create account balance`() {
        val request = CreateAccountBalanceRequest(123L, AccountBalanceType.CASH)
        val createdBalance = TestModelFactory.buildAccountBalance(1L, 123L, AccountBalanceType.CASH, BigDecimal(0))
        val amount = BigDecimal(0)

        Mockito.`when`(accountBalanceService.upsertAccountBalance(123L, AccountBalanceType.CASH))
            .thenReturn(createdBalance)

        val objectMapper = ObjectMapper()
        val jsonRequest = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/account-balances")
                .contentType("application/json")
                .content(jsonRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(123L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("CASH"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(amount))

        Mockito.verify(accountBalanceService, Mockito.times(1))
            .upsertAccountBalance(123L, AccountBalanceType.CASH)
    }
}
