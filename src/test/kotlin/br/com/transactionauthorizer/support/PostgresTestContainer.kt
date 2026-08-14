package br.com.transactionauthorizer.support

import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.PostgreSQLContainer

object PostgresTestContainer {

    val container: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")

    init {
        container.start()
    }

    fun connect() {
        Database.connect(
            url = container.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = container.username,
            password = container.password
        )
    }
}
