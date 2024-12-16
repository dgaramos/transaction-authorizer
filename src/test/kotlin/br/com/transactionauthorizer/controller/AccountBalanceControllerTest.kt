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
import java.util.UUID

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
        val accountId = UUID.randomUUID()
        val request = CreateAccountBalanceRequest(accountId.toString(), AccountBalanceType.CASH)
        val createdBalance = TestModelFactory.buildAccountBalance(accountId = accountId, accountBalanceType = AccountBalanceType.CASH, amount = BigDecimal(0))
        val amount = BigDecimal(0)

        whenever(accountBalanceService.upsertAccountBalance(accountId, AccountBalanceType.CASH))
            .thenReturn(createdBalance)

        val objectMapper = ObjectMapper()
        val jsonRequest = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/account-balances")
                .contentType("application/json")
                .content(jsonRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("CASH"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(amount))

        verify(accountBalanceService, times(1))
            .upsertAccountBalance(accountId, AccountBalanceType.CASH)
    }

    @Test
    fun `test get account balance`() {
        val balanceId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val offset = 0
        val limit = 10
        val accountBalance = TestModelFactory.buildAccountBalance(balanceId, accountId, AccountBalanceType.CASH, BigDecimal(100))
        val transactions =
            TestModelFactory.buildCardTransaction(
                accountBalanceId = balanceId,
                totalAmount = BigDecimal(50),
                cardTransactionStatus = CardTransactionStatus.APPROVED
            )

        whenever(accountBalanceService.getAccountBalanceById(balanceId))
            .thenReturn(accountBalance)
        whenever(cardTransactionService.getAllTransactionsByAccountBalanceId(balanceId, offset, limit))
            .thenReturn(listOf(transactions))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/account-balances/$balanceId?transactionOffset=$offset&transactionLimit=$limit"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("CASH"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(100))
            .andExpect(MockMvcResultMatchers.jsonPath("$.transactions.length()").value(1))


        verify(accountBalanceService, times(1))
            .getAccountBalanceById(balanceId)
        verify(cardTransactionService, times(1))
            .getAllTransactionsByAccountBalanceId(balanceId, offset, limit)
    }
}
