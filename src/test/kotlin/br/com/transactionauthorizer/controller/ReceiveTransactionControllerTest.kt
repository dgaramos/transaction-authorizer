package br.com.transactionauthorizer.controller

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.service.ReceiveTransactionService
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
class ReceiveTransactionControllerTest {

    @InjectMocks
    private lateinit var receiveTransactionController: ReceiveTransactionController
    @Mock
    private lateinit var receiveTransactionService: ReceiveTransactionService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(receiveTransactionController).build()
    }

    @Test
    fun `test create receive transaction`() {
        val request = ReceivedTransactionRequest("123", BigDecimal("100.00"), "MCC1", "Merchant1")
        val transactionCode = "XX"

        whenever(receiveTransactionService.receiveTransaction(request))
            .thenReturn(transactionCode)

        val objectMapper = ObjectMapper()
        val jsonRequest = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/receive-transactions")
                .contentType("application/json")
                .content(jsonRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("XX"))

        verify(receiveTransactionService, times(1))
            .receiveTransaction(request)
    }
}

