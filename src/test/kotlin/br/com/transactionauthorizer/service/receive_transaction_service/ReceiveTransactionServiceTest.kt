package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByAccountIdAndTypeException
import br.com.transactionauthorizer.exceptions.AccountNotFoundByIdException
import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.*
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.AccountService
import br.com.transactionauthorizer.service.CardTransactionService
import br.com.transactionauthorizer.service.ReceiveTransactionService
import br.com.transactionauthorizer.service.implementations.ReceiveTransactionServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.stream.Stream
import kotlin.test.assertEquals

class ReceiveTransactionServiceTest {

    private lateinit var cardTransactionService: CardTransactionService
    private lateinit var accountBalanceService: AccountBalanceService
    private lateinit var accountService: AccountService
    private lateinit var receiveTransactionService: ReceiveTransactionService

    @BeforeEach
    fun setUp() {
        cardTransactionService = mock()
        accountBalanceService = mock()
        accountService = mock()
        receiveTransactionService = ReceiveTransactionServiceImpl(cardTransactionService, accountBalanceService, accountService)
    }

    @Test
    fun `should return ERROR when account is not found`() {
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        whenever(accountService.getAccountById(request.account.toLong())).thenThrow(AccountNotFoundByIdException::class.java)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("07", result)
    }

    @Test
    fun `should approve cash transaction when cash account has sufficient balance`() {
        val account = TestModelFactory.buildAccount(id = 1, name = "Jane Doe")
        val cashAccountBalance = TestModelFactory.buildAccountBalance(id = 1, amount = BigDecimal(100), accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val cardTransaction = TestModelFactory.buildCardTransaction(account = "1", totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.APPROVED)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        whenever(accountService.getAccountById(cashAccountBalance.accountId)).thenReturn(account)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, cashAccountBalance.accountBalanceType)).thenReturn(cashAccountBalance)
        whenever(cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            accountBalanceId = cashAccountBalance.id!!,
            merchant = request.merchant
        )).thenReturn(cardTransaction)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)  // Transaction approved
        verify(cardTransactionService, times(1)).createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            accountBalanceId = cashAccountBalance.id!!,
            merchant = request.merchant
        )
        verify(accountService, times(1)).getAccountById(request.account.toLong())
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.FOOD)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.MEAL)
    }

    @Test
    fun `should deny cash transaction when cash account has insufficient balance`() {
        val account = TestModelFactory.buildAccount(id = 1, name = "Jane Doe")
        val cashAccountBalance = TestModelFactory.buildAccountBalance(id = 1, amount = BigDecimal(30), accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        whenever(accountService.getAccountById(cashAccountBalance.accountId)).thenReturn(account)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, cashAccountBalance.accountBalanceType)).thenReturn(cashAccountBalance)


        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("51", result)  // Transaction denied

        verify(accountService, times(1)).getAccountById(request.account.toLong())
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.FOOD)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.MEAL)
    }


    @Test
    fun `should deny cash transaction when cash account is not found`() {
        val account = TestModelFactory.buildAccount(id = 1, name = "Jane Doe")
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        whenever(accountService.getAccountById(account.id!!)).thenReturn(account)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(account.id!!, AccountBalanceType.CASH)).thenThrow(AccountBalanceNotFoundByAccountIdAndTypeException::class.java)


        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("07", result) // Error in transaction
        verify(accountService, times(1)).getAccountById(request.account.toLong())
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(account.id!!, AccountBalanceType.CASH)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(account.id!!, AccountBalanceType.FOOD)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(account.id!!, AccountBalanceType.MEAL)
    }

    @ParameterizedTest
    @MethodSource("provideAccountBalanceType")
    fun `should fallback to cash account when account balance is sufficient`(
        accountBalanceType: AccountBalanceType,
        merchantName: String,
        mcc: String
    ) {
        val account = TestModelFactory.buildAccount(id = 1, name = "Jane Doe")
        val accountBalance = TestModelFactory.buildAccountBalance(id = 2, amount = BigDecimal(60), accountBalanceType = accountBalanceType, accountId = 1L)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = mcc, merchant = merchantName)

        whenever(accountService.getAccountById(accountBalance.accountId)).thenReturn(account)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(accountBalance.accountId, accountBalanceType)).thenReturn(accountBalance)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)
        verify(accountService, times(1)).getAccountById(accountBalance.accountId)
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, accountBalanceType)
        verify(accountBalanceService, times(0)).getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.CASH)

        verify(cardTransactionService, times(1)).createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            accountBalanceId = accountBalance.id!!,
            merchant = request.merchant
        )
    }

    @ParameterizedTest
    @MethodSource("provideAccountBalanceAndFallbackCases")
    fun `should fallback to cash account when account balance is insufficient`(
        accountBalanceType: AccountBalanceType,
        merchantName: String,
        mcc: String,
        primaryAccountBalance: BigDecimal,
        fallbackAccountBalance: BigDecimal,
        expectedResult: String
    ) {
        val account = TestModelFactory.buildAccount(id = 1, name = "Jane Doe")
        val nonCashAccountBalance = TestModelFactory.buildAccountBalance(id = 2, amount = primaryAccountBalance, accountBalanceType = accountBalanceType, accountId = 1L)
        val cashAccountBalance = TestModelFactory.buildAccountBalance(id = 1, amount = fallbackAccountBalance, accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = mcc, merchant = merchantName)

        whenever(accountService.getAccountById(nonCashAccountBalance.accountId)).thenReturn(account)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(nonCashAccountBalance.accountId, accountBalanceType)).thenReturn(nonCashAccountBalance)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)).thenReturn(cashAccountBalance)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals(expectedResult, result)
        verify(accountService, times(1)).getAccountById(nonCashAccountBalance.accountId)
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(nonCashAccountBalance.accountId, accountBalanceType)
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)

        if (expectedResult == "00") { // Approved case
            verify(cardTransactionService, times(1)).createTransaction(
                account = request.account,
                totalAmount = request.totalAmount,
                mcc = request.mcc,
                transactionStatus = CardTransactionStatus.APPROVED,
                accountBalanceId = cashAccountBalance.id!!,
                merchant = request.merchant
            )
        } else { // Denied case
            verify(cardTransactionService, times(1)).createTransaction(
                account = request.account,
                totalAmount = request.totalAmount,
                mcc = request.mcc,
                transactionStatus = CardTransactionStatus.DENIED,
                accountBalanceId = nonCashAccountBalance.id!!,
                merchant = request.merchant
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideAccountBalanceAndFallbackCases")
    fun `should fallback to cash account when account balance is not found`(
        accountBalanceType: AccountBalanceType,
        merchantName: String,
        mcc: String,
        primaryAccountBalance: BigDecimal,
        fallbackAccountBalance: BigDecimal,
        expectedResult: String,
        expectedTransactionStatus: CardTransactionStatus
    ) {
        val account = TestModelFactory.buildAccount(id = 1, name = "Jane Doe")
        val cashAccountBalance = TestModelFactory.buildAccountBalance(id = 1, amount = fallbackAccountBalance, accountBalanceType = AccountBalanceType.CASH, accountId = 1L)
        val request = ReceivedTransactionRequest(account = "1", totalAmount = BigDecimal(50), mcc = mcc, merchant = merchantName)

        whenever(accountService.getAccountById(cashAccountBalance.accountId)).thenReturn(account)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, accountBalanceType)).thenThrow(AccountBalanceNotFoundByAccountIdAndTypeException::class.java)
        whenever(accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)).thenReturn(cashAccountBalance)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals(expectedResult, result)
        verify(accountService, times(1)).getAccountById(cashAccountBalance.accountId)
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, accountBalanceType)
        verify(accountBalanceService, times(1)).getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH)


        verify(cardTransactionService, times(1)).createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = expectedTransactionStatus,
            accountBalanceId = cashAccountBalance.id!!,
            merchant = request.merchant
        )

    }

    companion object {
        @JvmStatic
        fun provideAccountBalanceType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(AccountBalanceType.FOOD, "FoodMerchant", "5411"),
                Arguments.of(AccountBalanceType.MEAL, "FoodMerchant", "5811"),
            )
        }

        @JvmStatic
        fun provideAccountBalanceAndFallbackCases(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(AccountBalanceType.FOOD, "FoodMerchant", "5411", BigDecimal(30), BigDecimal(100), "00", CardTransactionStatus.APPROVED),
                Arguments.of(AccountBalanceType.FOOD, "MealMerchant", "5411", BigDecimal(30), BigDecimal(40), "51", CardTransactionStatus.DENIED),
                Arguments.of(AccountBalanceType.MEAL, "FoodMerchant", "5811", BigDecimal(30), BigDecimal(100), "00", CardTransactionStatus.APPROVED),
                Arguments.of(AccountBalanceType.MEAL, "MealMerchant", "5811", BigDecimal(30), BigDecimal(40), "51", CardTransactionStatus.DENIED)
            )
        }
    }
}
