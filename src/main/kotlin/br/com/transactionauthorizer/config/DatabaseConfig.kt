package br.com.transactionauthorizer.config

import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun initializeDatabase() {

        val dbUrl = System.getenv("SPRING_DATASOURCE_URL") ?: "jdbc:postgresql://localhost:5432/demo_db"
        val dbUser = System.getenv("SPRING_DATASOURCE_USERNAME") ?: "demo_dev_rw"
        val dbPass = System.getenv("SPRING_DATASOURCE_PASSWORD") ?: "dev_database_passwd"

        Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPass
        )
    }
}
