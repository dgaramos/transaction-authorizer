package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import br.com.transactionauthorizer.service.implementations.AccountBalanceServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import java.util.*

class AccountBalanceServiceImplTest {
    @Mock
    private lateinit var accountBalanceRepository: AccountBalanceRepository

    private lateinit var accountBalanceService: AccountBalanceService

    private val accountId = UUID.randomUUID()
    private val balanceType = AccountBalanceType.CASH
    private val amount = BigDecimal("100.00")

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        accountBalanceService = AccountBalanceServiceImpl(accountBalanceRepository)
    }

    @Test
    fun `should get account balances by Id successfully`() {
        val accountBalance1 = TestModelFactory.buildAccountBalance(
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = amount
        )
        whenever(accountBalanceRepository.getAccountBalanceById(accountBalance1.id)).thenReturn(accountBalance1)

        val balance = accountBalanceService.getAccountBalanceById(accountBalance1.id)

        Assertions.assertNotNull(balance)
        verify(accountBalanceRepository).getAccountBalanceById(accountBalance1.id)
    }

    @Test
    fun `should create an account balance successfully`() {
        val accountBalance = TestModelFactory.buildAccountBalance(
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = amount
        )

        whenever(accountBalanceRepository.upsertAccountBalance(accountId, balanceType,)).thenReturn(accountBalance)

        val createdBalance = accountBalanceService.upsertAccountBalance(accountId, balanceType)

        Assertions.assertNotNull(createdBalance)
        Assertions.assertEquals(accountId, createdBalance.accountId)
        Assertions.assertEquals(balanceType, createdBalance.accountBalanceType)
        Assertions.assertEquals(amount, createdBalance.amount)

        verify(accountBalanceRepository).upsertAccountBalance(accountId, balanceType)
    }

    @Test
    fun `should get account balance by accountId and type successfully`() {
        val accountBalance = TestModelFactory.buildAccountBalance(
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = amount
        )

        whenever(accountBalanceRepository.getAccountBalanceByAccountIdAndType(accountId, balanceType)).thenReturn(accountBalance)

        val fetchedBalance = accountBalanceService.getAccountBalanceByAccountIdAndType(accountId, balanceType)

        Assertions.assertNotNull(fetchedBalance)
        Assertions.assertEquals(accountId, fetchedBalance.accountId)
        Assertions.assertEquals(balanceType, fetchedBalance.accountBalanceType)
        Assertions.assertEquals(amount, fetchedBalance.amount)

        verify(accountBalanceRepository).getAccountBalanceByAccountIdAndType(accountId, balanceType)
    }

    @Test
    fun `should get account balances by accountId successfully`() {
        val accountBalance1 = TestModelFactory.buildAccountBalance(
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = amount
        )
        val accountBalance2 = TestModelFactory.buildAccountBalance(
            accountId = accountId,
            accountBalanceType = AccountBalanceType.MEAL,
            amount = BigDecimal("50.00")
        )

        whenever(accountBalanceRepository.getAccountBalancesByAccountId(accountId)).thenReturn(listOf(accountBalance1, accountBalance2))

        val balances = accountBalanceService.getAccountBalancesByAccountId(accountId)

        Assertions.assertNotNull(balances)
        Assertions.assertEquals(2, balances.size)
        Assertions.assertEquals(accountId, balances[0].accountId)
        Assertions.assertEquals(accountId, balances[1].accountId)

        verify(accountBalanceRepository).getAccountBalancesByAccountId(accountId)
    }

    @Test
    fun `should update account balance amount successfully`() {
        val initialAmount = BigDecimal("100.00")
        val newAmount = BigDecimal("200.00")

        val accountBalance = TestModelFactory.buildAccountBalance(
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = initialAmount
        )

        val updatedBalance = accountBalance.copy(amount = newAmount)

        whenever(accountBalanceRepository.updateAccountBalanceAmount(accountBalance.id, newAmount)).thenReturn(updatedBalance)

        val result = accountBalanceService.updateAccountBalanceAmount(accountBalance.id, newAmount)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(newAmount, result.amount)

        verify(accountBalanceRepository).updateAccountBalanceAmount(accountBalance.id, newAmount)
    }
}