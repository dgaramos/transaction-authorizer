package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.exceptions.OptimisticLockException
import br.com.transactionauthorizer.model.BaseModel
import br.com.transactionauthorizer.model.table.BaseTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Repository
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

data class TestModel(
    override val id: UUID = UUID.randomUUID(),
    val name: String,
    override val version: Long = 0,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, version, createdAt, updatedAt)

object TestTable : BaseTable<UUID>("Test") {
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id, name = "PK_Test_Id")
}

@Repository
class TestRepository : BaseRepository<TestModel, TestTable>(
    TestTable,
    { row ->
        TestModel(
            id = row[TestTable.id].value,
            name = row[TestTable.name],
            version = row[TestTable.version],
            createdAt = row[TestTable.createdAt],
            updatedAt = row[TestTable.updatedAt]
        )
    }
) {

    private fun buildTestTable(testModel: TestModel): UUID {
        return TestTable.insertAndGetId {
            it[id] = testModel.id
            it[name] = testModel.name
            it[TestTable.version] = testModel.version
            it[TestTable.createdAt] = testModel.createdAt
            it[TestTable.updatedAt] = testModel.updatedAt
        }.value
    }

    fun createTest(testModel: TestModel) =
        super.create(testModel, ::buildTestTable)

    fun updateTest(testModel: TestModel) =
        super.update(testModel) { updateStatement, entity ->
            updateStatement[TestTable.name] = entity.name
            updateStatement[TestTable.version] = entity.version
            updateStatement[TestTable.createdAt] = entity.createdAt
            updateStatement[TestTable.updatedAt] = entity.updatedAt
        }

    fun findTestById(id: UUID) =
        super.findById(id)

    fun findAllTests(offset: Int = 0, limit: Int = 10) =
        super.findAll(offset, limit)
}

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class BaseRepositoryIntegrationTest {

    @Autowired
    lateinit var testRepository: TestRepository

    @BeforeEach
    fun cleanup() {
        transaction {
            TestTable.deleteAll()
        }
    }

    @Test
    fun `should create an entity`() {
        val test = TestModel(name = "John Doe")

        val createdTest = testRepository.createTest(test)

        assertNotNull(createdTest.id)
        assertEquals("John Doe", createdTest.name)
        assertEquals(0, createdTest.version)
    }

    @Test
    fun `should update an entity`() {
        val test = TestModel(name = "John Doe")

        val createdTest = testRepository.createTest(test)

        val updatedTest = createdTest.copy(name = "Jane Doe")
        val result = testRepository.updateTest(updatedTest)

        assertNotNull(result)
        assertEquals("Jane Doe", result.name)
        assertTrue(result.version > createdTest.version)
        assertTrue(result.updatedAt > createdTest.updatedAt)
    }

    @Test
    fun `should throw OptimisticLockException when updating an entity with outdated version`() {
        val test = TestModel(name = "John Doe")

        val createdTest = testRepository.createTest(test)

        val updatedTest = createdTest.copy(name = "Jane Doe", version = createdTest.version + 2)

        assertThrows(OptimisticLockException::class.java) {
            testRepository.updateTest(updatedTest)
        }
    }

    @Test
    fun `should find an entity by id`() {
        val test = TestModel(name = "John Doe")
        val createdTest = testRepository.createTest(test)

        val foundAccount = testRepository.findTestById(createdTest.id)

        assertNotNull(foundAccount)
        assertEquals(createdTest.id, foundAccount?.id)
        assertEquals(createdTest.name, foundAccount?.name)
    }

    @Test
    fun `should retrieve entities with pagination`() {
        val entities = listOf(
            TestModel(name = "Entity 1"),
            TestModel(name = "Entity 2"),
            TestModel(name = "Entity 3"),
            TestModel(name = "Entity 4"),
            TestModel(name = "Entity 5")
        )

        entities.forEach { testRepository.createTest(it) }

        // First page
        val page1 = testRepository.findAllTests(offset = 0, limit = 2)
        assertEquals(2, page1.size)
        assertEquals("Entity 1", page1[0].name)
        assertEquals("Entity 2", page1[1].name)

        // Second page
        val page2 = testRepository.findAllTests(offset = 2, limit = 2)
        assertEquals(2, page2.size)
        assertEquals("Entity 3", page2[0].name)
        assertEquals("Entity 4", page2[1].name)

        // Third page
        val page3 = testRepository.findAllTests(offset = 4, limit = 2)
        assertEquals(1, page3.size)
        assertEquals("Entity 5", page3[0].name)

        // Fourth page (empty)
        val emptyPage = testRepository.findAllTests(offset = 6, limit = 2)
        assertTrue(emptyPage.isEmpty())
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            // Connect to in-memory database for testing
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

            transaction {
                // Create the tables for testing
                SchemaUtils.create(TestTable)
            }
        }
    }
}
