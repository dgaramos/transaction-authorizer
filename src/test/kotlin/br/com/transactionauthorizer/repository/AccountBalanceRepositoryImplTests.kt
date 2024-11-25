package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.exceptions.*
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.table.AccountBalanceTable
import br.com.transactionauthorizer.repository.implementations.AccountBalanceRepositoryImpl
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.math.BigDecimal
import java.math.RoundingMode

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountBalanceRepositoryImplTest {

    private lateinit var repository: AccountBalanceRepository

    @BeforeAll
    fun setup() {
        // Connect to an in-memory H2 database
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(AccountBalanceTable)
        }
        repository = AccountBalanceRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        // Clean up the database after each test
        transaction {
            SchemaUtils.drop(AccountBalanceTable)
            SchemaUtils.create(AccountBalanceTable)
        }
    }

    @Test
    fun `test create account balance`() {
        val accountId = 123L
        val balanceType = AccountBalanceType.CASH
        val amount = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)

        val createdAccountBalance = repository.upsertAccountBalance(accountId, balanceType)

        Assertions.assertNotNull(createdAccountBalance)
        Assertions.assertEquals(accountId, createdAccountBalance.accountId)
        Assertions.assertEquals(balanceType, createdAccountBalance.accountBalanceType)
        Assertions.assertEquals(amount, createdAccountBalance.amount)
    }

    @Test
    fun `test create account balance returns already existing account balance`() {
        val accountId = 123L
        val balanceType = AccountBalanceType.CASH
        val amount = BigDecimal("100.00")

        val alreadyExistingAccountBalance = repository.upsertAccountBalance(accountId, balanceType)
        repository.updateAccountBalanceAmount(alreadyExistingAccountBalance.id!!, amount)

        val createdAccountBalance = repository.upsertAccountBalance(accountId, balanceType)

        Assertions.assertNotNull(createdAccountBalance)
        Assertions.assertEquals(alreadyExistingAccountBalance.accountId, createdAccountBalance.accountId)
        Assertions.assertEquals(alreadyExistingAccountBalance.accountBalanceType, createdAccountBalance.accountBalanceType)
        Assertions.assertEquals(amount, createdAccountBalance.amount)
    }

    @Test
    fun `test get account balance by ID`() {
        val accountId = 123L
        val balanceType = AccountBalanceType.MEAL

        val createdAccountBalance = repository.upsertAccountBalance(accountId, balanceType)

        val fetchedBalance = repository.getAccountBalanceById(createdAccountBalance.id!!)

        Assertions.assertNotNull(fetchedBalance)
        Assertions.assertEquals(createdAccountBalance.id, fetchedBalance.id)
        Assertions.assertEquals(createdAccountBalance.accountId, fetchedBalance.accountId)
        Assertions.assertEquals(createdAccountBalance.amount, fetchedBalance.amount)
    }

    @Test
    fun `test get account balance by non-existing ID`() {
        val exception = Assertions.assertThrows(AccountBalanceNotFoundByIdException::class.java) {
            repository.getAccountBalanceById(999L)
        }

        Assertions.assertEquals("Account balance with ID 999 not found.", exception.message)
    }

    @Test
    fun `test get account balance by account ID and type`() {
        val accountId = 123L
        val balanceType = AccountBalanceType.MEAL

        val createdAccountBalance = repository.upsertAccountBalance(accountId, balanceType)

        val fetchedBalance = repository.getAccountBalanceByAccountIdAndType(accountId, balanceType)

        Assertions.assertNotNull(fetchedBalance)
        Assertions.assertEquals(createdAccountBalance.id, fetchedBalance.id)
        Assertions.assertEquals(createdAccountBalance.accountId, fetchedBalance.accountId)
        Assertions.assertEquals(createdAccountBalance.amount, fetchedBalance.amount)
    }

    @Test
    fun `test get account balance by non-existing account ID and type`() {
        val accountId = 123L
        val balanceType = AccountBalanceType.MEAL

        val exception = Assertions.assertThrows(AccountBalanceNotFoundByAccountIdAndTypeException::class.java) {
            repository.getAccountBalanceByAccountIdAndType(accountId, balanceType)
        }

        Assertions.assertEquals("Account balance with accountId $accountId and type $balanceType not found.", exception.message)
    }

    @Test
    fun `test get account balances by account ID`() {
        val accountId = 123L
        repository.upsertAccountBalance(accountId, AccountBalanceType.CASH)
        repository.upsertAccountBalance(accountId, AccountBalanceType.MEAL)

        val balances = repository.getAccountBalancesByAccountId(accountId)

        Assertions.assertEquals(2, balances.size)
    }

    @Test
    fun `test get account balances by non-existing account ID`() {
        val exception = Assertions.assertThrows(AccountBalancesNotFoundByAccountIdException::class.java) {
            repository.getAccountBalancesByAccountId(999L)
        }

        Assertions.assertEquals("Account balances with accountId 999 were not found.", exception.message)
    }

    @Test
    fun `test update account balance amount`() {
        val accountId = 123L
        val balanceType = AccountBalanceType.CASH
        val updatedAmount = BigDecimal("200.00")

        val createdAccountBalance = repository.upsertAccountBalance(accountId, balanceType)

        val updatedBalance = repository.updateAccountBalanceAmount(createdAccountBalance.id!!, updatedAmount)

        Assertions.assertNotNull(updatedBalance)
        Assertions.assertEquals(updatedAmount, updatedBalance.amount)
    }

    @Test
    fun `test update non-existing account balance`() {
        val nonExistentId = 999L
        val updatedAmount = BigDecimal("150.00")

        val exception = Assertions.assertThrows(AccountBalanceNotFoundByIdException::class.java) {
            repository.updateAccountBalanceAmount(nonExistentId, updatedAmount)
        }

        Assertions.assertEquals("Account balance with ID $nonExistentId not found.", exception.message)
    }
}
