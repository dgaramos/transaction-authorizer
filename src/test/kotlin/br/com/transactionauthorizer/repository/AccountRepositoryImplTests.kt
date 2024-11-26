package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.exceptions.AccountNotFoundByIdException
import br.com.transactionauthorizer.factory.TestModelFactory
import br.com.transactionauthorizer.factory.TestTableFactory
import br.com.transactionauthorizer.model.Account
import br.com.transactionauthorizer.model.table.AccountTable
import br.com.transactionauthorizer.repository.implementations.AccountRepositoryImpl
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepositoryImplTest {

    private lateinit var repository: AccountRepository

    @BeforeAll
    fun setup() {
        // Initialize H2 in-memory database
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(AccountTable)
        }
        repository = AccountRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        // Clean up database after each test
        transaction {
            SchemaUtils.drop(AccountTable)
        }
        transaction {
            SchemaUtils.create(AccountTable)
        }
    }

    @Test
    fun `test create account`() {
        val account = TestModelFactory.buildAccount(id = 1L, name = "Test Account")

        val createdAccount = repository.createAccount(account)

        Assertions.assertNotNull(createdAccount)
        Assertions.assertEquals(account, createdAccount)
    }

    @Test
    fun `test get all accounts`() {
        val accountsToCreate = listOf("Account 1", "Account 2", "Account 3")
        accountsToCreate.forEach {
            TestTableFactory.createAccount(name = it)
        }

        val accounts = repository.getAllAccounts()

        Assertions.assertEquals(accountsToCreate.size, accounts.size)
        Assertions.assertTrue(accounts.map(Account::name).containsAll(accountsToCreate))
    }

    @Test
    fun `test get account by id`() {
        val name = "Account by ID"
        val accountId = TestTableFactory.createAccount(name = name)

        val retrievedAccount = repository.getAccountById(accountId)

        Assertions.assertNotNull(retrievedAccount)
        Assertions.assertEquals(accountId, retrievedAccount.id)
        Assertions.assertEquals(name, retrievedAccount.name)
    }

    @Test
    fun `test get account by non-existing ID`() {
        val exception = Assertions.assertThrows(AccountNotFoundByIdException::class.java) {
            repository.getAccountById(999L)
        }

        Assertions.assertEquals("Account with accountId 999 not found.", exception.message)
    }

}
