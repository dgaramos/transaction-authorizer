package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.constants.MccLists.MEAL_MCCS
import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.factory.TestTableFactory
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.AccountBalanceTable
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.model.table.CardTransactionTable
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import br.com.transactionauthorizer.repository.CardTransactionRepository
import br.com.transactionauthorizer.service.ReceiveTransactionService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReceiveTransactionServiceIntegrationTest(
    @Autowired private val receiveTransactionService: ReceiveTransactionService,
    @Autowired private val cardTransactionRepository: CardTransactionRepository,
    @Autowired private val accountBalanceRepository: AccountBalanceRepository
) {

    @Test
    fun `should commit transaction when all operations succeed`() {
        val accountId = TestTableFactory.createAccount(name = "Jane Doe")
        val accountBalanceId = TestTableFactory.createAccountBalance(
            accountBalanceType = AccountBalanceType.MEAL,
            accountId = accountId,
            amount = BigDecimal(200)
        )
        val request = ReceivedTransactionRequest(
            account = accountId.toString(),
            totalAmount = BigDecimal(50),
            mcc = "5811",
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)
        val remainingBalance = accountBalanceRepository.getAccountBalanceById(accountBalanceId).amount
        assertEquals(BigDecimal(150).setScale(2, RoundingMode.HALF_UP), remainingBalance)

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(request.account)

        assertEquals(1, transactions.size)
        val transaction = transactions.first()
        assertEquals(CardTransactionStatus.APPROVED, transaction.cardTransactionStatus)
        assertEquals(accountId.toString(), transaction.account)
        assertEquals(BigDecimal(50).setScale(2, RoundingMode.HALF_UP), transaction.totalAmount)
    }

    @Test
    fun `should commit denied transaction when insufficient funds`() {
        val accountId = TestTableFactory.createAccount(name = "Jane Doe")
        val mealAccountBalanceId = TestTableFactory.createAccountBalance(
            accountBalanceType = AccountBalanceType.MEAL,
            accountId = accountId,
            amount = BigDecimal(30)
        )
        val cashAccountBalanceId = TestTableFactory.createAccountBalance(
            accountBalanceType = AccountBalanceType.CASH,
            accountId = accountId,
            amount = BigDecimal(30)
        )

        val request = ReceivedTransactionRequest(
            account = accountId.toString(),
            totalAmount = BigDecimal(50),
            mcc = MEAL_MCCS.first(),
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("51", result)

        val remainingMealBalance = accountBalanceRepository.getAccountBalanceById(mealAccountBalanceId).amount
        assertEquals(BigDecimal(30).setScale(2, RoundingMode.HALF_UP), remainingMealBalance)


        val remainingCashBalance = accountBalanceRepository.getAccountBalanceById(cashAccountBalanceId).amount
        assertEquals(BigDecimal(30).setScale(2, RoundingMode.HALF_UP), remainingCashBalance)

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(request.account)
        assertEquals(1, transactions.size)
        val transaction = transactions.first()
        assertEquals(CardTransactionStatus.DENIED, transaction.cardTransactionStatus)
        assertEquals(accountId.toString(), transaction.account)
        assertEquals(BigDecimal(50).setScale(2, RoundingMode.HALF_UP), transaction.totalAmount)
    }

    @Test
    fun `should commit denied transaction when insufficient funds and cash balance does not exist`() {
        val accountId = TestTableFactory.createAccount(name = "Jane Doe")
        val accountBalanceId = TestTableFactory.createAccountBalance(
            accountBalanceType = AccountBalanceType.MEAL,
            accountId = accountId,
            amount = BigDecimal(30)
        )

        val request = ReceivedTransactionRequest(
            account = accountId.toString(),
            totalAmount = BigDecimal(50),
            mcc = MEAL_MCCS.first(),
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("51", result)

        val remainingBalance = accountBalanceRepository.getAccountBalanceById(accountBalanceId).amount
        assertEquals(BigDecimal(30).setScale(2, RoundingMode.HALF_UP), remainingBalance)

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(request.account)
        assertEquals(1, transactions.size)
        val transaction = transactions.first()
        assertEquals(CardTransactionStatus.DENIED, transaction.cardTransactionStatus)
        assertEquals(accountId.toString(), transaction.account)
        assertEquals(BigDecimal(50).setScale(2, RoundingMode.HALF_UP), transaction.totalAmount)
    }

    @Test
    fun `should not commit transaction when account is not found`() {
        val accountId = 10L

        val request = ReceivedTransactionRequest(
            account = accountId.toString(),
            totalAmount = BigDecimal(50),
            mcc = "5711",
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("07", result)

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(request.account)
        assertEquals(0, transactions.size)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

            transaction {
                SchemaUtils.create(
                    AccountTable,
                    AccountBalanceTable,
                    CardTransactionTable
                )
            }
        }
    }
}
