package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.factory.TestTableFactory
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
        val account = "1234567890"
        val totalAmount = BigDecimal(150.00)
        val mcc = "5811"
        val cardTransactionStatus = CardTransactionStatus.APPROVED
        val merchant = "PADARIA DO ZE - SAO PAULO BR"

        TestTableFactory.createCardTransaction(
            account = account,
            totalAmount = totalAmount,
            mcc = mcc,
            cardTransactionStatus = cardTransactionStatus,
            merchant = merchant
        )

        val retrievedTransactions = cardTransactionRepository.getAllTransactionsByAccountId(account)

        assertNotNull(retrievedTransactions)
        assertEquals(1, retrievedTransactions.size)
        assertEquals(account, retrievedTransactions.first().account)
    }

    @Test
    fun `should return all transactions by account Id`() {
        TestTableFactory.createCardTransaction(
            account = "1234567890",
            totalAmount = BigDecimal(100.00),
            mcc = "5811",
            cardTransactionStatus = CardTransactionStatus.APPROVED,
            merchant = "PADARIA DO ZE - SAO PAULO BR"
        )
        TestTableFactory.createCardTransaction(
            account = "9876543210",
            totalAmount = BigDecimal(200.00),
            mcc = "5411",
            cardTransactionStatus = CardTransactionStatus.DENIED,
            merchant = "SUPERMERCADO ALVORADA - SÃO PAULO BR"
        )

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId("9876543210")

        assertEquals(1, transactions.size)
    }
}
