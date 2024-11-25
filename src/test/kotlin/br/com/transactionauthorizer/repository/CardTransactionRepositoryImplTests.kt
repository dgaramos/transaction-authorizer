package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.CardTransactionTable
import br.com.transactionauthorizer.repository.implementations.CardTransactionRepositoryImpl
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardTransactionRepositoryImplTests {

    private lateinit var cardTransactionRepository: CardTransactionRepository

    @BeforeAll
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.create(CardTransactionTable)
        }

        cardTransactionRepository = CardTransactionRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        transaction {
            SchemaUtils.drop(CardTransactionTable)
        }

        transaction {
            SchemaUtils.create(CardTransactionTable)
        }
    }

    @Test
    fun `should insert a new transaction and retrieve it`() {
        val transaction = cardTransactionRepository.createTransaction(
            account = "1234567890",
            totalAmount = BigDecimal(150.00),
            mcc = "5811",
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = "PADARIA DO ZE - SAO PAULO BR"
        )

        val retrievedTransaction = cardTransactionRepository.getAllTransactions()

        assertNotNull(transaction)
        assertEquals(1, retrievedTransaction.size)
        assertEquals(transaction.account, retrievedTransaction.first().account)
        assertEquals(transaction.totalAmount, retrievedTransaction.first().totalAmount)
    }

    @Test
    fun `should return all transactions`() {
        cardTransactionRepository.createTransaction(
            account = "1234567890",
            totalAmount = BigDecimal(100.00),
            mcc = "5811",
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = "PADARIA DO ZE - SAO PAULO BR"
        )
        cardTransactionRepository.createTransaction(
            account = "9876543210",
            totalAmount = BigDecimal(200.00),
            mcc = "5411",
            transactionStatus = CardTransactionStatus.DENIED,
            merchant = "SUPERMERCADO ALVORADA - SÃO PAULO BR"
        )

        val transactions = cardTransactionRepository.getAllTransactions()

        assertEquals(2, transactions.size)
    }

    @Test
    fun `should return all transactions by account Id`() {
        cardTransactionRepository.createTransaction(
            account = "1234567890",
            totalAmount = BigDecimal(100.00),
            mcc = "5811",
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = "PADARIA DO ZE - SAO PAULO BR"
        )
        cardTransactionRepository.createTransaction(
            account = "9876543210",
            totalAmount = BigDecimal(200.00),
            mcc = "5411",
            transactionStatus = CardTransactionStatus.DENIED,
            merchant = "SUPERMERCADO ALVORADA - SÃO PAULO BR"
        )

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId("9876543210")

        assertEquals(1, transactions.size)
    }

    @Test
    fun `should return a transaction by its ID`() {
        val transaction = cardTransactionRepository.createTransaction(
            account = "1122334455",
            totalAmount = BigDecimal(50.00),
            mcc = "5812",
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = "RESTAURANTE SANTANA - RIO DE JANEIRO BR"
        )

        val retrievedTransaction = transaction {
            CardTransactionTable.selectAll().where { CardTransactionTable.id eq transaction.id }.singleOrNull()
        }

        assertNotNull(retrievedTransaction)
        assertEquals(transaction.account, retrievedTransaction?.get(CardTransactionTable.account))
    }

    @Test
    fun `should return null when trying to fetch non-existent transaction by ID`() {
        val transaction = cardTransactionRepository.getAllTransactions().find { it.id == 999L }

        assertNull(transaction)
    }
}
