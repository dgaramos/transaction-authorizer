package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.constants.MerchantNames.MEAL_MERCHANTS
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

class ReceiveTransactionServiceMealTest {

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
    fun `should approve meal transaction when meal account has sufficient balance`() {
        val accountBalance = AccountBalance(id = 1, amount = BigDecimal(100), accountBalanceType = AccountBalanceType.MEAL, accountId = 1L)
        val cardTransaction = CardTransaction(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.APPROVED)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant")

        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(accountBalance.accountId, accountBalance.accountBalanceType)).thenReturn(accountBalance)
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
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.MEAL)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.CASH)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.FOOD)
    }

    @Test
    fun `should approve meal transaction when meal account has sufficient balance and MCC is wrong`() {
        val accountBalance = AccountBalance(id = 1, amount = BigDecimal(100), accountBalanceType = AccountBalanceType.MEAL, accountId = 1L)
        val cardTransaction = CardTransaction(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.APPROVED)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5711", merchant = MEAL_MERCHANTS.first())

        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(accountBalance.accountId, accountBalance.accountBalanceType)).thenReturn(accountBalance)
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
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.MEAL)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.CASH)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.FOOD)
    }

    @Test
    fun `should approve meal transaction with fallback to cash when meal account balance is insufficient`() {
        val mealAccountBalance = AccountBalance(id = 1, amount = BigDecimal(30), accountBalanceType = AccountBalanceType.MEAL, accountId = 1L)
        val cashAccountBalance = AccountBalance(id = 2, amount = BigDecimal(100), accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val cardTransaction = CardTransaction(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.APPROVED)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant")

        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(mealAccountBalance.accountId, mealAccountBalance.accountBalanceType)).thenReturn(mealAccountBalance)
        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, cashAccountBalance.accountBalanceType)).thenReturn(cashAccountBalance)
        `when`(cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = request.merchant
        )).thenReturn(cardTransaction)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)  // Transaction approved from cash account
        verify(cardTransactionService, times(1)).createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = request.merchant
        )
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(mealAccountBalance.accountId, AccountBalanceType.MEAL)
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)
    }

    @Test
    fun `should deny meal transaction when both meal and cash accounts have insufficient balance`() {
        val mealAccountBalance = AccountBalance(id = 1, amount = BigDecimal(30), accountBalanceType = AccountBalanceType.MEAL, accountId = 1L)
        val cashAccountBalance = AccountBalance(id = 2, amount = BigDecimal(20), accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val cardTransaction = CardTransaction(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.DENIED)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant")

        `when`(accountBalanceService.getAccountBalanceByAccountIdAndType(mealAccountBalance.accountId, mealAccountBalance.accountBalanceType)).thenReturn(mealAccountBalance)
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
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(mealAccountBalance.accountId, AccountBalanceType.MEAL)
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)
    }
}
