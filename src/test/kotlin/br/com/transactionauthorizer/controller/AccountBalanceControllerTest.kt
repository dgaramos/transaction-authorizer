package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.CreateAccountBalanceRequest
import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.CardTransactionService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
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
    @Mock
    private lateinit var cardTransactionService: CardTransactionService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(accountBalanceController, cardTransactionService).build()
    }

    @Test
    fun `test create account balance`() {
        val request = CreateAccountBalanceRequest(123L, AccountBalanceType.CASH)
        val createdBalance = TestModelFactory.buildAccountBalance(1L, 123L, AccountBalanceType.CASH, BigDecimal(0))
        val amount = BigDecimal(0)

        whenever(accountBalanceService.upsertAccountBalance(123L, AccountBalanceType.CASH))
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

        verify(accountBalanceService, times(1))
            .upsertAccountBalance(123L, AccountBalanceType.CASH)
    }

    @Test
    fun `test get account balance`() {
        val balanceId = 1L
        val accountBalance = TestModelFactory.buildAccountBalance(balanceId, 123L, AccountBalanceType.CASH, BigDecimal(100))
        val transactions =
            TestModelFactory.buildCardTransaction(
                id = 1L,
                accountBalanceId = balanceId,
                totalAmount = BigDecimal(50),
                cardTransactionStatus = CardTransactionStatus.APPROVED
            )

        whenever(accountBalanceService.getAccountBalanceById(balanceId))
            .thenReturn(accountBalance)
        whenever(cardTransactionService.getAllTransactionsByAccountBalanceId(balanceId))
            .thenReturn(listOf(transactions))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/account-balances/$balanceId"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(123L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("CASH"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(100))
            .andExpect(MockMvcResultMatchers.jsonPath("$.transactions.length()").value(1))


        verify(accountBalanceService, times(1))
            .getAccountBalanceById(balanceId)
        verify(cardTransactionService, times(1))
            .getAllTransactionsByAccountBalanceId(balanceId)
    }
}
