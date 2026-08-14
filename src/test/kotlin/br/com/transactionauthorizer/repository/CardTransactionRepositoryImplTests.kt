package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.factory.TestTableFactory
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.repository.implementations.CardTransactionRepositoryImpl
import br.com.transactionauthorizer.support.PostgresTestContainer
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.util.UUID

@Order(10)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardTransactionRepositoryImplTests {

    private lateinit var cardTransactionRepository: CardTransactionRepository

    @BeforeAll
    fun setup() {
        PostgresTestContainer.connect()
        cardTransactionRepository = CardTransactionRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        transaction {
            exec("TRUNCATE TABLE card_transaction, account_balance, account CASCADE")
        }
    }

    @Test
    fun `should insert a new transaction and retrieve it`() {
        val accountId = TestTableFactory.createAccount()
        val balanceId = TestTableFactory.createAccountBalance(accountId = accountId)
        val totalAmount = BigDecimal(150.00)
        val mcc = "5811"
        val cardTransactionStatus = CardTransactionStatus.APPROVED
        val merchant = "PADARIA DO ZE - SAO PAULO BR"

        TestTableFactory.createCardTransaction(
            account = accountId.toString(),
            accountId = accountId,
            accountBalanceId = balanceId,
            totalAmount = totalAmount,
            mcc = mcc,
            cardTransactionStatus = cardTransactionStatus,
            merchant = merchant
        )

        val retrievedTransactions = cardTransactionRepository.getAllTransactionsByAccountId(accountId, 0, 10)

        assertNotNull(retrievedTransactions)
        assertEquals(1, retrievedTransactions.size)
        assertEquals(accountId.toString(), retrievedTransactions.first().account)
    }

    @Test
    fun `should return all transactions by account Id with descending order`() {
        val accountId = TestTableFactory.createAccount()
        val balanceId = TestTableFactory.createAccountBalance(accountId = accountId)

        TestTableFactory.createCardTransaction(
            account = accountId.toString(),
            accountId = accountId,
            accountBalanceId = balanceId,
            totalAmount = BigDecimal(100.00),
            mcc = "5811",
            cardTransactionStatus = CardTransactionStatus.APPROVED,
            merchant = "Merchant A"
        )
        TestTableFactory.createCardTransaction(
            account = accountId.toString(),
            accountId = accountId,
            accountBalanceId = balanceId,
            totalAmount = BigDecimal(200.00),
            mcc = "5411",
            cardTransactionStatus = CardTransactionStatus.DENIED,
            merchant = "Merchant B"
        )

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(accountId, 0, 10)

        assertEquals(2, transactions.size)
        assertTrue(transactions[0].createdAt > transactions[1].createdAt)
    }

    @Test
    fun `should paginate transactions by account Id`() {
        val accountId = TestTableFactory.createAccount()
        val balanceId = TestTableFactory.createAccountBalance(accountId = accountId)

        repeat(15) {
            TestTableFactory.createCardTransaction(
                account = accountId.toString(),
                accountId = accountId,
                accountBalanceId = balanceId,
                totalAmount = BigDecimal(100 + it),
                mcc = "581${it % 10}",
                cardTransactionStatus = CardTransactionStatus.APPROVED,
                merchant = "Merchant $it"
            )
        }

        val transactionsPage1 = cardTransactionRepository.getAllTransactionsByAccountId(accountId, 0, 5)
        val transactionsPage2 = cardTransactionRepository.getAllTransactionsByAccountId(accountId, 5, 5)

        assertEquals(5, transactionsPage1.size)
        assertEquals(5, transactionsPage2.size)
        assertNotEquals(transactionsPage1.first().id, transactionsPage2.first().id)
    }

    @Test
    fun `should return all transactions by account balance Id with descending order`() {
        val accountId = TestTableFactory.createAccount()
        val accountBalanceId = TestTableFactory.createAccountBalance(accountId = accountId)

        TestTableFactory.createCardTransaction(
            account = accountId.toString(),
            accountId = accountId,
            accountBalanceId = accountBalanceId,
            totalAmount = BigDecimal(100.00),
            mcc = "5811",
            cardTransactionStatus = CardTransactionStatus.APPROVED,
            merchant = "Merchant A"
        )
        TestTableFactory.createCardTransaction(
            account = accountId.toString(),
            accountId = accountId,
            accountBalanceId = accountBalanceId,
            totalAmount = BigDecimal(200.00),
            mcc = "5411",
            cardTransactionStatus = CardTransactionStatus.DENIED,
            merchant = "Merchant B"
        )

        val transactions = cardTransactionRepository.getAllTransactionsByAccountBalanceId(accountBalanceId, 0, 10)

        assertEquals(2, transactions.size)
        assertTrue(transactions[0].createdAt > transactions[1].createdAt)
    }

    @Test
    fun `should paginate transactions by account balance Id`() {
        val accountId = TestTableFactory.createAccount()
        val accountBalanceId = TestTableFactory.createAccountBalance(accountId = accountId)

        repeat(15) {
            TestTableFactory.createCardTransaction(
                account = accountId.toString(),
                accountId = accountId,
                accountBalanceId = accountBalanceId,
                totalAmount = BigDecimal(100 + it),
                mcc = "581${it % 10}",
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
