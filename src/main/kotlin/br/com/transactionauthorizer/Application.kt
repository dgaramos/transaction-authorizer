package br.com.transactionauthorizer

import br.com.transactionauthorizer.config.DatabaseConfig
import br.com.transactionauthorizer.utils.DatabaseInitializer
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@OpenAPIDefinition(
	info = Info(
		title = "Transaction Authorizer API",
		version = "1.0.0",
		description = """
			This API is part of a take-home test for the Senior Software Engineer position at Caju Beneficios. 
			It is responsible for authorizing transactions based on a set of business rules and integrates with a PostgreSQL database to store and process transaction data. 
			The system provides a REST API for transaction authorization, enabling seamless interaction with external services.
		""",
		contact = Contact(
			name = "Danilo Ramos",
			email = "dgaramos@gmail.com",
			url = "https://github.com/dgaramos"
		),
		license = License(
			name = "MIT License",
			url = "https://opensource.org/licenses/MIT"
		)
	),
	servers = [
		Server(url = "http://localhost:8080", description = "Local server")
	]
)
@SpringBootApplication
@EnableTransactionManagement
class TransactionAuthorizerApplication

fun main(args: Array<String>) {
	DatabaseConfig.initializeDatabase()
	//DatabaseInitializer.setupSchemaAndData()

	runApplication<TransactionAuthorizerApplication>(*args)
}