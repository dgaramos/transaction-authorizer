package br.com.transactionauthorizer.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Health Check", description = "Endpoint to check application health")
@RestController
@RequestMapping("/api/health")
class HealthCheckController {

    @Operation(summary = "Check the application's health")
    @GetMapping
    fun healthCheck(): ResponseEntity<Map<String, String>> {
        val healthStatus = mapOf(
            "status" to "UP",
            "timestamp" to System.currentTimeMillis().toString()
        )
        return ResponseEntity.ok(healthStatus)
    }
}
