package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.model.table.AccountBalanceTable
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.model.table.CardTransactionTable
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import br.com.transactionauthorizer.repository.AccountRepository
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
    @Autowired private val accountBalanceRepository: AccountBalanceRepository,
    @Autowired private val accountRepository: AccountRepository
) {

    @Test
    fun `should commit transaction when all operations succeed`() {
        val account = accountRepository.createAccount("Jane Doe")
        val accountBalance = accountBalanceRepository.createAccountBalance(
            amount = BigDecimal(200),
            accountBalanceType = AccountBalanceType.MEAL,
            accountId = account.id!!
        )

        val request = ReceivedTransactionRequest(
            account = account.id.toString(),
            totalAmount = BigDecimal(50),
            mcc = "5811",
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)
        val remainingBalance = accountBalanceRepository.getAccountBalanceById(accountBalance.id!!).amount
        assertEquals(BigDecimal(150).setScale(2, RoundingMode.HALF_UP), remainingBalance)

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(request.account)

        assertEquals(1, transactions.size)
        val transaction = transactions.first()
        assertEquals(CardTransactionStatus.APPROVED, transaction.cardTransactionStatus)
        assertEquals(account.id.toString(), transaction.account)
        assertEquals(BigDecimal(50).setScale(2, RoundingMode.HALF_UP), transaction.totalAmount)
    }

    @Test
    fun `should commit denied transaction when insufficient funds`() {
        val account = accountRepository.createAccount("John Doe")
        val accountBalance = accountBalanceRepository.createAccountBalance(
            amount = BigDecimal(30),
            accountBalanceType = AccountBalanceType.CASH,
            accountId = account.id!!
        )

        val request = ReceivedTransactionRequest(
            account = account.id.toString(),
            totalAmount = BigDecimal(50),
            mcc = "5711",
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("51", result)

        val remainingBalance = accountBalanceRepository.getAccountBalanceById(accountBalance.id!!).amount
        assertEquals(BigDecimal(30).setScale(2, RoundingMode.HALF_UP), remainingBalance)

        val transactions = cardTransactionRepository.getAllTransactionsByAccountId(request.account)
        assertEquals(1, transactions.size)
        val transaction = transactions.first()
        assertEquals(CardTransactionStatus.DENIED, transaction.cardTransactionStatus)
        assertEquals(account.id.toString(), transaction.account)
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
