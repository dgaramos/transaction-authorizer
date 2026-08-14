package br.com.transactionauthorizer.support

import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

abstract class AbstractSpringIntegrationTest {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun overrideDataSourceProperties(registry: DynamicPropertyRegistry) {
            val container = PostgresTestContainer.container
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }

        @JvmStatic
        @BeforeAll
        fun connectExposed() {
            PostgresTestContainer.connect()
        }
    }
}
