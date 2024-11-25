package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.CardTransaction
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.repository.CardTransactionRepository
import br.com.transactionauthorizer.service.implementations.CardTransactionServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CardTransactionServiceImplTest {

    private lateinit var cardTransactionRepository: CardTransactionRepository
    private lateinit var cardTransactionService: CardTransactionService

    @BeforeEach
    fun setUp() {
        cardTransactionRepository = mock(CardTransactionRepository::class.java)
        cardTransactionService = CardTransactionServiceImpl(cardTransactionRepository)
    }

    @Test
    fun `should return all transactions`() {
        val transactions = listOf(
            CardTransaction(id = 1L, account = "1", totalAmount = BigDecimal(100), mcc = "5411", cardTransactionStatus = CardTransactionStatus.APPROVED, merchant = "TestMerchant"),
            CardTransaction(id = 2L, account = "2", totalAmount = BigDecimal(200), mcc = "5812", cardTransactionStatus = CardTransactionStatus.DENIED, merchant = "AnotherMerchant")
        )
        `when`(cardTransactionRepository.getAllTransactions()).thenReturn(transactions)

        val result = cardTransactionService.getAllTransactions()

        assertEquals(transactions, result)
        verify(cardTransactionRepository, times(1)).getAllTransactions()
    }

    @Test
    fun `should return a transaction by ID`() {
        val transaction = CardTransaction(id = 1L, account = "1", totalAmount = BigDecimal(100), mcc = "5411", cardTransactionStatus = CardTransactionStatus.APPROVED, merchant = "TestMerchant")
        `when`(cardTransactionRepository.getTransactionById(1L)).thenReturn(transaction)

        val result = cardTransactionService.getTransactionById(1L)

        assertEquals(transaction, result)
        verify(cardTransactionRepository, times(1)).getTransactionById(1L)
    }

    @Test
    fun `should return null when transaction ID is not found`() {
        `when`(cardTransactionRepository.getTransactionById(99L)).thenReturn(null)

        val result = cardTransactionService.getTransactionById(99L)

        assertNull(result)
        verify(cardTransactionRepository, times(1)).getTransactionById(99L)
    }

    @Test
    fun `should create a transaction`() {
        val account = "1"
        val totalAmount = BigDecimal(100)
        val mcc = "5411"
        val cardTransactionStatus = CardTransactionStatus.APPROVED
        val merchant = "TestMerchant"

        cardTransactionService.createTransaction(account, totalAmount, mcc, cardTransactionStatus, merchant)

        verify(cardTransactionRepository, times(1)).createTransaction(
            account = account,
            totalAmount = totalAmount,
            mcc = mcc,
            transactionStatus = cardTransactionStatus,
            merchant = merchant
        )
    }
}
