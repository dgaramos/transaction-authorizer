package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.service.CardTransactionService
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
import java.time.LocalDateTime
class CardTransactionControllerTest {

    @InjectMocks
    private lateinit var cardTransactionController: CardTransactionController
    @Mock
    private lateinit var cardTransactionService: CardTransactionService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(cardTransactionController).build()
    }

    @Test
    fun `test get all card transactions`() {
        val transactions = listOf(
            CardTransaction(1L, "123", BigDecimal("100.00"), "MCC1", "Merchant1", CardTransactionStatus.APPROVED, LocalDateTime.now()),
            CardTransaction(2L, "456", BigDecimal("150.00"), "MCC2", "Merchant2", CardTransactionStatus.DENIED, LocalDateTime.now().plusHours(1))
        )

        Mockito.`when`(cardTransactionService.getAllTransactions()).thenReturn(transactions)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/card-transactions"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].account").value("123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].merchant").value("Merchant2"))

        Mockito.verify(cardTransactionService, Mockito.times(1)).getAllTransactions()
    }

    @Test
    fun `test get card transaction by id`() {
        val transaction = CardTransaction(1L, "123", BigDecimal("100.00"), "MCC1", "Merchant1", CardTransactionStatus.APPROVED, LocalDateTime.now())

        Mockito.`when`(cardTransactionService.getTransactionById(1L)).thenReturn(transaction)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/card-transactions/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.account").value("123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.merchant").value("Merchant1"))

        Mockito.verify(cardTransactionService, Mockito.times(1)).getTransactionById(1L)
    }

    @Test
    fun `test get card transaction by non-existing id`() {
        Mockito.`when`(cardTransactionService.getTransactionById(999L)).thenReturn(null)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/card-transactions/999"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        Mockito.verify(cardTransactionService, Mockito.times(1)).getTransactionById(999L)
    }
}

