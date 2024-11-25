package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransaction
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

class ReceiveTransactionServiceCashTest {

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
    fun `should approve cash transaction when cash account has sufficient balance`() {
        val cashAccountBalance = AccountBalance(id = 1, amount = BigDecimal(100), accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val cardTransaction = CardTransaction(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.APPROVED)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, cashAccountBalance.accountBalanceType)).thenReturn(cashAccountBalance)
        `when`(cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = request.merchant
        )).thenReturn(cardTransaction)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)  // Transaction approved
        verify(cardTransactionService, times(1)).createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = request.merchant
        )
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.FOOD)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.MEAL)
    }

    @Test
    fun `should deny cash transaction when cash account has insufficient balance`() {
        val cashAccountBalance = AccountBalance(id = 1, amount = BigDecimal(30), accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val cardTransaction = CardTransaction(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.DENIED)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, cashAccountBalance.accountBalanceType)).thenReturn(cashAccountBalance)
        `when`(cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.DENIED,
            merchant = request.merchant
        )).thenReturn(cardTransaction)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("51", result)  // Transaction denied
        verify(cardTransactionService, times(1)).createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.DENIED,
            merchant = request.merchant
        )
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.FOOD)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.MEAL)
    }
}
