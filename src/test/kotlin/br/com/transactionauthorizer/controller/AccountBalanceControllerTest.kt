package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.controller.model.request.AccountBalanceRequest
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
    fun `test get account balances by accountId`() {
        val balances = listOf(
            AccountBalance(1L, 123L, AccountBalanceType.CASH, BigDecimal("100.00")),
            AccountBalance(2L, 123L, AccountBalanceType.MEAL, BigDecimal("50.00"))
        )

        Mockito.`when`(accountBalanceService.getAccountBalancesByAccountId(123L)).thenReturn(balances)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/account-balances/123"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountId").value(123L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].type").value("CASH"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value("100.00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].accountId").value(123L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].type").value("MEAL"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount").value("50.00"))

        Mockito.verify(accountBalanceService, Mockito.times(1)).getAccountBalancesByAccountId(123L)
    }

    @Test
    fun `test get account balance by accountId and type`() {
        val balance = AccountBalance(1L, 123L, AccountBalanceType.CASH, BigDecimal("100.00"))

        Mockito.`when`(accountBalanceService.getAccountBalanceByAccountIdAndType(123L, AccountBalanceType.CASH))
            .thenReturn(balance)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/account-balances/123/CASH"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(123L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("CASH"))

        Mockito.verify(accountBalanceService, Mockito.times(1))
            .getAccountBalanceByAccountIdAndType(123L, AccountBalanceType.CASH)
    }

    @Test
    fun `test create account balance`() {
        val request = AccountBalanceRequest(123L, AccountBalanceType.CASH, "100.00")
        val createdBalance = AccountBalance(1L, 123L, AccountBalanceType.CASH, BigDecimal("100.00"))

        Mockito.`when`(accountBalanceService.createAccountBalance(123L, AccountBalanceType.CASH, BigDecimal("100.00")))
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value("100.00"))

        Mockito.verify(accountBalanceService, Mockito.times(1))
            .createAccountBalance(123L, AccountBalanceType.CASH, BigDecimal("100.00"))
    }

    @Test
    fun `test update account balance amount`() {
        val request = AccountBalanceRequest(123L, AccountBalanceType.CASH, "200.00")
        val updatedBalance = AccountBalance(1L, 123L, AccountBalanceType.CASH, BigDecimal("200.00"))

        Mockito.`when`(accountBalanceService.updateAccountBalanceAmount(1L, BigDecimal("200.00")))
            .thenReturn(updatedBalance)

        val objectMapper = ObjectMapper()
        val jsonRequest = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/account-balances/1")
                .contentType("application/json")
                .content(jsonRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(123L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("CASH"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value("200.00"))

        Mockito.verify(accountBalanceService, Mockito.times(1))
            .updateAccountBalanceAmount(1L, BigDecimal("200.00"))
    }
}
