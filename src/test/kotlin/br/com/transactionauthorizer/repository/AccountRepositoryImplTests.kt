package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.exceptions.AccountNotFoundByIdException
import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.factory.TestTableFactory
import br.com.transactionauthorizer.repository.table.AccountTable
import br.com.transactionauthorizer.repository.implementations.AccountRepositoryImpl
import br.com.transactionauthorizer.support.PostgresTestContainer
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@Order(10)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepositoryImplTest {

    private lateinit var repository: AccountRepository

    @BeforeAll
    fun setup() {
        PostgresTestContainer.connect()
        repository = AccountRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        transaction {
            exec("TRUNCATE TABLE card_transaction, account_balance, account CASCADE")
        }
    }

    @Test
    fun `test create account`() {
        val account = TestModelFactory.buildAccount(name = "Test Account")

        val createdAccount = repository.createAccount(account)

        assertNotNull(createdAccount)
        assertEquals(account, createdAccount)
    }

    @Test
    fun `test get all accounts with pagination`() {
        val accountsToCreate = listOf("Account 1", "Account 2", "Account 3")
        accountsToCreate.forEach {
            TestTableFactory.createAccount(name = it)
        }

        val firstPage = repository.getAllAccounts(offset = 0, limit = 2)
        val secondPage = repository.getAllAccounts(offset = 2, limit = 2)

        assertEquals(2, firstPage.size)
        assertEquals(1, secondPage.size)
    }

    @Test
    fun `test get account by id`() {
        val name = "Account by ID"
        val accountId = TestTableFactory.createAccount(name = name)

        val retrievedAccount = repository.getAccountById(accountId)

        assertNotNull(retrievedAccount)
        assertEquals(accountId, retrievedAccount.id)
        assertEquals(name, retrievedAccount.name)
    }

    @Test
    fun `test get account by non-existing ID`() {
        val accountId = UUID.randomUUID()
        val exception = Assertions.assertThrows(AccountNotFoundByIdException::class.java) {
            repository.getAccountById(accountId)
        }

        assertEquals("Account with accountId $accountId not found.", exception.message)
    }

}
