package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.repository.CardTransactionRepository
import br.com.transactionauthorizer.service.implementations.CardTransactionServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.math.BigDecimal

class CardTransactionServiceImplTest {

    private lateinit var cardTransactionRepository: CardTransactionRepository
    private lateinit var cardTransactionService: CardTransactionService

    @BeforeEach
    fun setUp() {
        cardTransactionRepository = mock()
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
        whenever(cardTransactionRepository.createTransaction(account, totalAmount, mcc, accountBalanceId, cardTransactionStatus, merchant)).thenReturn(cardTransaction)

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

    @Test
    fun `should find a transaction by account balance Id`() {
        val accountBalanceId = 1L

        val cardTransactions = listOf(
            TestModelFactory.buildCardTransaction(
                accountBalanceId = accountBalanceId,
            )
        )

        whenever(cardTransactionRepository.getAllTransactionsByAccountBalanceId(accountBalanceId)).thenReturn(cardTransactions)

        val result = cardTransactionService.getAllTransactionsByAccountBalanceId(accountBalanceId)

        verify(cardTransactionRepository, times(1)).getAllTransactionsByAccountBalanceId(accountBalanceId = accountBalanceId)

        Assertions.assertEquals(cardTransactions, result)
    }
}
