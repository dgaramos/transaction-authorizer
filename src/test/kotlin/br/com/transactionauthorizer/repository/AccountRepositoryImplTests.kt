package br.com.transactionauthorizer.repository

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
        val name = "Test Account"

        val createdAccount = repository.createAccount(name)

        Assertions.assertNotNull(createdAccount)
        Assertions.assertEquals(name, createdAccount.name)
        Assertions.assertNotNull(createdAccount.id)
    }

    @Test
    fun `test get all accounts`() {
        val accountsToCreate = listOf("Account 1", "Account 2", "Account 3")
        accountsToCreate.forEach { repository.createAccount(it) }

        val accounts = repository.getAllAccounts()

        Assertions.assertEquals(accountsToCreate.size, accounts.size)
        Assertions.assertTrue(accounts.map(Account::name).containsAll(accountsToCreate))
    }

    @Test
    fun `test get account by id`() {
        val name = "Account by ID"
        val createdAccount = repository.createAccount(name)

        val retrievedAccount = repository.getAccountById(createdAccount.id!!)

        Assertions.assertNotNull(retrievedAccount)
        Assertions.assertEquals(createdAccount.id, retrievedAccount?.id)
        Assertions.assertEquals(createdAccount.name, retrievedAccount?.name)
    }

    @Test
    fun `test get account by non-existent id returns null`() {
        val retrievedAccount = repository.getAccountById(999L) // Non-existent ID

        Assertions.assertNull(retrievedAccount)
    }
}
