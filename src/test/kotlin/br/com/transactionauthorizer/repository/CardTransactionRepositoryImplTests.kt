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

        val retrievedTransactions = cardTransactionRepository.getAllTransactionsByAccountId(account, 0, 10)

        assertNotNull(retrievedTransactions)
        assertEquals(1, retrievedTransactions.size)
        assertEquals(account, retrievedTransactions.first().account)
    }

    @Test
    fun `should return all transactions by account Id with descending order`() {
        val account = "1234567890"
        TestTableFactory.createCardTransaction(
            account = account,
            totalAmount = BigDecimal(100.00),
            mcc = "5811",
            cardTransactionStatus = CardTransactionStatus.APPROVED,
            merchant = "Merchant A"
        )
        TestTableFactory.createCardTransaction(
            account = account,
            totalAmount = BigDecimal(200.00),
            mcc = "5411",
            cardTransactionStatus = CardTransactionStatus.DENIED,
            merchant = "Merchant B"
        )

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(account, 0, 10)

        assertEquals(2, transactions.size)
        assertTrue(transactions[0].createdAt > transactions[1].createdAt)
    }

    @Test
    fun `should paginate transactions by account Id`() {
        val account = "1234567890"
        repeat(15) {
            TestTableFactory.createCardTransaction(
                account = account,
                totalAmount = BigDecimal(100 + it),
                mcc = "581${it % 10}",
                cardTransactionStatus = CardTransactionStatus.APPROVED,
                merchant = "Merchant $it"
            )
        }

        val transactionsPage1 = cardTransactionRepository.getAllTransactionsByAccountId(account, 0, 5)
        val transactionsPage2 = cardTransactionRepository.getAllTransactionsByAccountId(account, 5, 5)

        assertEquals(5, transactionsPage1.size)
        assertEquals(5, transactionsPage2.size)
        assertNotEquals(transactionsPage1.first().id, transactionsPage2.first().id)
    }

    @Test
    fun `should return all transactions by account balance Id with descending order`() {
        val accountBalanceId = 12L
        TestTableFactory.createCardTransaction(
            account = "1234567890",
            totalAmount = BigDecimal(100.00),
            mcc = "5811",
            accountBalanceId = accountBalanceId,
            cardTransactionStatus = CardTransactionStatus.APPROVED,
            merchant = "Merchant A"
        )
        TestTableFactory.createCardTransaction(
            account = "1234567890",
            totalAmount = BigDecimal(200.00),
            mcc = "5411",
            accountBalanceId = accountBalanceId,
            cardTransactionStatus = CardTransactionStatus.DENIED,
            merchant = "Merchant B"
        )

        val transactions = cardTransactionRepository.getAllTransactionsByAccountBalanceId(accountBalanceId, 0, 10)

        assertEquals(2, transactions.size)
        assertTrue(transactions[0].createdAt > transactions[1].createdAt)
    }

    @Test
    fun `should paginate transactions by account balance Id`() {
        val accountBalanceId = 12L
        repeat(15) {
            TestTableFactory.createCardTransaction(
                account = "1234567890",
                totalAmount = BigDecimal(100 + it),
                mcc = "581${it % 10}",
                accountBalanceId = accountBalanceId,
                cardTransactionStatus = CardTransactionStatus.APPROVED,
                merchant = "Merchant $it"
            )
        }

        val transactionsPage1 = cardTransactionRepository.getAllTransactionsByAccountBalanceId(accountBalanceId, 0, 5)
        val transactionsPage2 = cardTransactionRepository.getAllTransactionsByAccountBalanceId(accountBalanceId, 5, 5)

        assertEquals(5, transactionsPage1.size)
        assertEquals(5, transactionsPage2.size)
        assertNotEquals(transactionsPage1.first().id, transactionsPage2.first().id)
    }
}