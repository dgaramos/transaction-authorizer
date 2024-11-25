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
