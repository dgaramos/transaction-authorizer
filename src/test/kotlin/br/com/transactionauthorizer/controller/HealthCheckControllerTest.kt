package br.com.transactionauthorizer.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(HealthCheckController::class)
class HealthCheckControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return health status as UP`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/health"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UP"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").isNotEmpty)
    }
}
