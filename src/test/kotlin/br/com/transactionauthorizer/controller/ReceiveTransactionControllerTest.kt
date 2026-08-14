package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.model.TransactionCommand
import br.com.transactionauthorizer.service.ReceiveTransactionService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.util.UUID

class ReceiveTransactionControllerTest {

    @InjectMockKs
    private lateinit var receiveTransactionController: ReceiveTransactionController
    @MockK
    private lateinit var receiveTransactionService: ReceiveTransactionService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        mockMvc = MockMvcBuilders.standaloneSetup(receiveTransactionController).build()
    }

    @Test
    fun `test create receive transaction`() {
        val accountId = UUID.randomUUID()
        val request = ReceivedTransactionRequest(accountId.toString(), BigDecimal("100.00"), "MCC1", "Merchant1")
        val command = TransactionCommand(accountId.toString(), BigDecimal("100.00"), "MCC1", "Merchant1")
        val transactionCode = "XX"

        every { receiveTransactionService.receiveTransaction(command) } returns transactionCode

        val objectMapper = ObjectMapper()
        val jsonRequest = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/receive-transactions")
                .contentType("application/json")
                .content(jsonRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("XX"))

        verify(exactly = 1) { receiveTransactionService.receiveTransaction(command) }
    }
}
