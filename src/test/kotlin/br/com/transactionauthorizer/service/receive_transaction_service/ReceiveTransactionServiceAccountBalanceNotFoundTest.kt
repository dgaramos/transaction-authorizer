package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByAccountIdAndTypeException
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.CardTransactionService
import br.com.transactionauthorizer.service.ReceiveTransactionService
import br.com.transactionauthorizer.service.implementations.ReceiveTransactionServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import kotlin.test.assertEquals

class ReceiveTransactionServiceAccountBalanceNotFoundTest {

    private lateinit var cardTransactionService: CardTransactionService
    private lateinit var accountBalanceService: AccountBalanceService
    private lateinit var receiveTransactionService: ReceiveTransactionService

    @BeforeEach
    fun setUp() {
        cardTransactionService = mock(CardTransactionService::class.java)
        accountBalanceService = mock(AccountBalanceService::class.java)
        receiveTransactionService = ReceiveTransactionServiceImpl(cardTransactionService, accountBalanceService)
    }

    @Test
    fun `should return ERROR when account balance is not found`() {
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(request.account.toLong(), AccountBalanceType.CASH)).thenThrow(AccountBalanceNotFoundByAccountIdAndTypeException::class.java)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("07", result)  // Transaction error (not found)

        verify(cardTransactionService, times(0)).createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = request.merchant
        )
    }
}
