package br.com.transactionauthorizer.service.receive_transaction_service

import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByAccountIdAndTypeException
import br.com.transactionauthorizer.exceptions.AccountNotFoundByIdException
import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.*
import br.com.transactionauthorizer.model.TransactionCommand
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.AccountService
import br.com.transactionauthorizer.service.CardTransactionService
import br.com.transactionauthorizer.service.ReceiveTransactionService
import br.com.transactionauthorizer.service.implementations.ReceiveTransactionServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertEquals

class ReceiveTransactionServiceTest {

    private lateinit var cardTransactionService: CardTransactionService
    private lateinit var accountBalanceService: AccountBalanceService
    private lateinit var accountService: AccountService
    private lateinit var receiveTransactionService: ReceiveTransactionService

    @BeforeEach
    fun setUp() {
        cardTransactionService = mockk()
        accountBalanceService = mockk()
        accountService = mockk()
        receiveTransactionService = ReceiveTransactionServiceImpl(cardTransactionService, accountBalanceService, accountService)
    }

    @Test
    fun `should return ERROR when account is not found`() {
        val accountId = UUID.randomUUID()
        val request = TransactionCommand(account = accountId.toString(), totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        every { accountService.getAccountById(UUID.fromString(request.account)) } throws AccountNotFoundByIdException(UUID.fromString(request.account))

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("07", result)
    }

    @Test
    fun `should approve cash transaction when cash account has sufficient balance`() {
        val account = TestModelFactory.buildAccount(name = "Jane Doe")
        val cashAccountBalance = TestModelFactory.buildAccountBalance(amount = BigDecimal(100), accountBalanceType = AccountBalanceType.CASH, accountId = account.id)
        val cardTransaction = TestModelFactory.buildCardTransaction(account = account.id.toString(), totalAmount = BigDecimal(50), mcc = "5811", merchant = "MealMerchant", cardTransactionStatus = CardTransactionStatus.APPROVED)
        val request = TransactionCommand(account = account.id.toString(), totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        every { accountService.getAccountById(cashAccountBalance.accountId) } returns account
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, cashAccountBalance.accountBalanceType) } returns cashAccountBalance
        every { cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            accountBalanceId = cashAccountBalance.id,
            merchant = request.merchant
        ) } returns cardTransaction
        every { accountBalanceService.updateAccountBalanceAmount(cashAccountBalance.id, cashAccountBalance.amount - request.totalAmount) } returns mockk()

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)
        verify(exactly = 1) { cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            accountBalanceId = cashAccountBalance.id,
            merchant = request.merchant
        ) }
        verify(exactly = 1) { accountService.getAccountById(UUID.fromString(request.account)) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH) }
        verify(exactly = 0) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.FOOD) }
        verify(exactly = 0) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.MEAL) }
    }

    @Test
    fun `should deny cash transaction when cash account has insufficient balance`() {
        val account = TestModelFactory.buildAccount(name = "Jane Doe")
        val cashAccountBalance = TestModelFactory.buildAccountBalance(amount = BigDecimal(30), accountBalanceType = AccountBalanceType.CASH, accountId = account.id)
        val request = TransactionCommand(account = account.id.toString(), totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        every { accountService.getAccountById(cashAccountBalance.accountId) } returns account
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, cashAccountBalance.accountBalanceType) } returns cashAccountBalance
        every { cardTransactionService.createTransaction(any(), any(), any(), any(), any(), any()) } returns mockk()

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("51", result)

        verify(exactly = 1) { accountService.getAccountById(UUID.fromString(request.account)) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH) }
        verify(exactly = 0) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.FOOD) }
        verify(exactly = 0) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.MEAL) }
    }


    @Test
    fun `should deny cash transaction when cash account is not found`() {
        val account = TestModelFactory.buildAccount(name = "Jane Doe")
        val request = TransactionCommand(account = account.id.toString(), totalAmount = BigDecimal(50), mcc = "5011", merchant = "CashMerchant")

        every { accountService.getAccountById(account.id) } returns account
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(account.id, AccountBalanceType.CASH) } throws AccountBalanceNotFoundByAccountIdAndTypeException(account.id, AccountBalanceType.CASH)

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("07", result)
        verify(exactly = 1) { accountService.getAccountById(UUID.fromString(request.account)) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(account.id, AccountBalanceType.CASH) }
        verify(exactly = 0) { accountBalanceService.getAccountBalanceByAccountIdAndType(account.id, AccountBalanceType.FOOD) }
        verify(exactly = 0) { accountBalanceService.getAccountBalanceByAccountIdAndType(account.id, AccountBalanceType.MEAL) }
    }

    @ParameterizedTest
    @MethodSource("provideAccountBalanceType")
    fun `should fallback to cash account when account balance is sufficient`(
        accountBalanceType: AccountBalanceType,
        merchantName: String,
        mcc: String
    ) {
        val account = TestModelFactory.buildAccount(name = "Jane Doe")
        val accountBalance = TestModelFactory.buildAccountBalance(amount = BigDecimal(60), accountBalanceType = accountBalanceType, accountId = account.id)
        val request = TransactionCommand(account = account.id.toString(), totalAmount = BigDecimal(50), mcc = mcc, merchant = merchantName)

        every { accountService.getAccountById(accountBalance.accountId) } returns account
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(accountBalance.accountId, accountBalanceType) } returns accountBalance
        every { cardTransactionService.createTransaction(any(), any(), any(), any(), any(), any()) } returns mockk()
        every { accountBalanceService.updateAccountBalanceAmount(accountBalance.id, accountBalance.amount - request.totalAmount) } returns mockk()

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals("00", result)
        verify(exactly = 1) { accountService.getAccountById(accountBalance.accountId) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(accountBalance.accountId, accountBalanceType) }
        verify(exactly = 0) { accountBalanceService.getAccountBalanceByAccountIdAndType(accountBalance.accountId, AccountBalanceType.CASH) }

        verify(exactly = 1) { cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            accountBalanceId = accountBalance.id,
            merchant = request.merchant
        ) }
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
        val account = TestModelFactory.buildAccount(name = "Jane Doe")
        val nonCashAccountBalance = TestModelFactory.buildAccountBalance(amount = primaryAccountBalance, accountBalanceType = accountBalanceType, accountId = account.id)
        val cashAccountBalance = TestModelFactory.buildAccountBalance(amount = fallbackAccountBalance, accountBalanceType = AccountBalanceType.CASH, accountId = account.id)
        val request = TransactionCommand(account = account.id.toString(), totalAmount = BigDecimal(50), mcc = mcc, merchant = merchantName)

        every { accountService.getAccountById(nonCashAccountBalance.accountId) } returns account
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(nonCashAccountBalance.accountId, accountBalanceType) } returns nonCashAccountBalance
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH) } returns cashAccountBalance
        every { cardTransactionService.createTransaction(any(), any(), any(), any(), any(), any()) } returns mockk()
        every { accountBalanceService.updateAccountBalanceAmount(any(), any()) } returns mockk()

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals(expectedResult, result)
        verify(exactly = 1) { accountService.getAccountById(nonCashAccountBalance.accountId) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(nonCashAccountBalance.accountId, accountBalanceType) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH) }

        if (expectedResult == "00") {
            verify(exactly = 1) { cardTransactionService.createTransaction(
                account = request.account,
                totalAmount = request.totalAmount,
                mcc = request.mcc,
                transactionStatus = CardTransactionStatus.APPROVED,
                accountBalanceId = cashAccountBalance.id,
                merchant = request.merchant
            ) }
        } else {
            verify(exactly = 1) { cardTransactionService.createTransaction(
                account = request.account,
                totalAmount = request.totalAmount,
                mcc = request.mcc,
                transactionStatus = CardTransactionStatus.DENIED,
                accountBalanceId = nonCashAccountBalance.id,
                merchant = request.merchant
            ) }
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
        val account = TestModelFactory.buildAccount(name = "Jane Doe")
        val cashAccountBalance = TestModelFactory.buildAccountBalance(amount = fallbackAccountBalance, accountBalanceType = AccountBalanceType.CASH, accountId = account.id)
        val request = TransactionCommand(account = account.id.toString(), totalAmount = BigDecimal(50), mcc = mcc, merchant = merchantName)

        every { accountService.getAccountById(cashAccountBalance.accountId) } returns account
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, accountBalanceType) } throws AccountBalanceNotFoundByAccountIdAndTypeException(cashAccountBalance.accountId, accountBalanceType)
        every { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH) } returns cashAccountBalance
        every { cardTransactionService.createTransaction(any(), any(), any(), any(), any(), any()) } returns mockk()
        every { accountBalanceService.updateAccountBalanceAmount(any(), any()) } returns mockk()

        val result = receiveTransactionService.receiveTransaction(request)

        assertEquals(expectedResult, result)
        verify(exactly = 1) { accountService.getAccountById(cashAccountBalance.accountId) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, accountBalanceType) }
        verify(exactly = 1) { accountBalanceService.getAccountBalanceByAccountIdAndType(cashAccountBalance.accountId, AccountBalanceType.CASH) }

        verify(exactly = 1) { cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = expectedTransactionStatus,
            accountBalanceId = cashAccountBalance.id,
            merchant = request.merchant
        ) }
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
