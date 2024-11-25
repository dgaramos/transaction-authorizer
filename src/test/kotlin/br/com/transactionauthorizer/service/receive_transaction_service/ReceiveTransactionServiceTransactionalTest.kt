package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
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
class ReceiveTransactionServiceTransactionalTest(
    @Autowired private val receiveTransactionService: ReceiveTransactionService,
    @Autowired private val cardTransactionRepository: CardTransactionRepository,
    @Autowired private val accountBalanceRepository: AccountBalanceRepository
) {

    @Test
    fun `should commit transaction when all operations succeed`() {
        val accountBalance = accountBalanceRepository.createAccountBalance(
            amount = BigDecimal(200),
            accountBalanceType = AccountBalanceType.MEAL,
            accountId = 99L
        )

        val request = ReceivedTransactionRequest(
            account = "99",
            totalAmount = BigDecimal(50),
            mcc = "5811",
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)
        val remainingBalance = accountBalanceRepository.getAccountBalanceById(accountBalance.id!!).amount
        assertEquals(BigDecimal(150).setScale(2, RoundingMode.HALF_UP), remainingBalance)

        val transactions = cardTransactionRepository.getAllTransactions()

        assertEquals(1, transactions.size)
        val transaction = transactions.first()
        assertEquals(CardTransactionStatus.APPROVED, transaction.cardTransactionStatus)
        assertEquals("99", transaction.account)
        assertEquals(BigDecimal(50).setScale(2, RoundingMode.HALF_UP), transaction.totalAmount)
    }

    @Test
    fun `should rollback transaction when an exception occurs`() {
        val accountBalance = accountBalanceRepository.createAccountBalance(
            amount = BigDecimal(200),
            accountBalanceType = AccountBalanceType.CASH,
            accountId = 1L
        )

        val request = ReceivedTransactionRequest(
            account = "1",
            totalAmount = BigDecimal(50),
            mcc = "5811",
            merchant = "TestMerchant"
        )

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("07", result)

        val remainingBalance = accountBalanceRepository.getAccountBalanceById(accountBalance.id!!).amount
        assertEquals(BigDecimal(200).setScale(2, RoundingMode.HALF_UP), remainingBalance)

        val transactions = cardTransactionRepository.getAllTransactions()
        assertTrue(transactions.isEmpty())

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
