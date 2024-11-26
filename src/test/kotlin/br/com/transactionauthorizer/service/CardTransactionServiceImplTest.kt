package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.repository.CardTransactionRepository
import br.com.transactionauthorizer.service.implementations.CardTransactionServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal

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
        val accountBalanceId = 1L
        val cardTransactionStatus = CardTransactionStatus.APPROVED
        val merchant = "TestMerchant"

        val cardTransaction = TestModelFactory.buildCardTransaction(
            account = account,
            totalAmount = totalAmount,
            mcc = mcc,
            accountBalanceId = accountBalanceId,
            cardTransactionStatus = cardTransactionStatus,
            merchant = merchant
        )
        `when`(cardTransactionRepository.createTransaction(account, totalAmount, mcc, accountBalanceId, cardTransactionStatus, merchant)).thenReturn(cardTransaction)

        val result = cardTransactionService.createTransaction(account, totalAmount, mcc, accountBalanceId, cardTransactionStatus, merchant)

        verify(cardTransactionRepository, times(1)).createTransaction(
            account = account,
            totalAmount = totalAmount,
            mcc = mcc,
            accountBalanceId = accountBalanceId,
            cardTransactionStatus = cardTransactionStatus,
            merchant = merchant
        )

        Assertions.assertEquals(cardTransaction, result)
    }
}
