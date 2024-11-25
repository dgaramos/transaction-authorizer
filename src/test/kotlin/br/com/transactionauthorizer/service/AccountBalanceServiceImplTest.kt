package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.repository.AccountBalanceRepository
import br.com.transactionauthorizer.service.implementations.AccountBalanceServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import org.junit.jupiter.api.*
import org.mockito.Mockito.*

class AccountBalanceServiceImplTest {
    @Mock
    private lateinit var accountBalanceRepository: AccountBalanceRepository

    private lateinit var accountBalanceService: AccountBalanceService

    private val accountId = 123L
    private val balanceType = AccountBalanceType.CASH
    private val amount = BigDecimal("100.00")

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        accountBalanceService = AccountBalanceServiceImpl(accountBalanceRepository)
    }

    @Test
    fun `should create an account balance successfully`() {
        val accountBalance = AccountBalance(
            id = 1L,
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = amount
        )

        `when`(accountBalanceRepository.upsertAccountBalance(accountId, balanceType,)).thenReturn(accountBalance)

        val createdBalance = accountBalanceService.createAccountBalance(accountId, balanceType)

        Assertions.assertNotNull(createdBalance)
        Assertions.assertEquals(accountId, createdBalance.accountId)
        Assertions.assertEquals(balanceType, createdBalance.accountBalanceType)
        Assertions.assertEquals(amount, createdBalance.amount)

        verify(accountBalanceRepository).upsertAccountBalance(accountId, balanceType)
    }

    @Test
    fun `should get account balance by accountId and type successfully`() {
        val accountBalance = AccountBalance(
            id = 1L,
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = amount
        )

        `when`(accountBalanceRepository.getAccountBalanceByAccountIdAndType(accountId, balanceType)).thenReturn(accountBalance)

        val fetchedBalance = accountBalanceService.getAccountBalanceByAccountIdAndType(accountId, balanceType)

        Assertions.assertNotNull(fetchedBalance)
        Assertions.assertEquals(accountId, fetchedBalance.accountId)
        Assertions.assertEquals(balanceType, fetchedBalance.accountBalanceType)
        Assertions.assertEquals(amount, fetchedBalance.amount)

        verify(accountBalanceRepository).getAccountBalanceByAccountIdAndType(accountId, balanceType)
    }

    @Test
    fun `should get account balances by accountId successfully`() {
        val accountBalance1 = AccountBalance(
            id = 1L,
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = amount
        )
        val accountBalance2 = AccountBalance(
            id = 2L,
            accountId = accountId,
            accountBalanceType = AccountBalanceType.MEAL,
            amount = BigDecimal("50.00")
        )

        `when`(accountBalanceRepository.getAccountBalancesByAccountId(accountId)).thenReturn(listOf(accountBalance1, accountBalance2))

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

        val accountBalance = AccountBalance(
            id = 1L,
            accountId = accountId,
            accountBalanceType = balanceType,
            amount = initialAmount
        )

        val updatedBalance = accountBalance.copy(amount = newAmount)

        `when`(accountBalanceRepository.updateAccountBalanceAmount(accountBalance.id!!, newAmount)).thenReturn(updatedBalance)

        val result = accountBalanceService.updateAccountBalanceAmount(accountBalance.id!!, newAmount)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(newAmount, result.amount)

        verify(accountBalanceRepository).updateAccountBalanceAmount(accountBalance.id!!, newAmount)
    }
}