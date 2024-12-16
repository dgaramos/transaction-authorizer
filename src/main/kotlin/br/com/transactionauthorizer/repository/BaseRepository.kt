package br.com.transactionauthorizer.repository

import br.com.transactionauthorizer.model.BaseModel
import br.com.transactionauthorizer.model.table.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import br.com.transactionauthorizer.exceptions.OptimisticLockException
import java.util.UUID

abstract class BaseRepository<T : BaseModel, U : BaseTable<UUID>>(
    private val table: U,
    private val toModel: (ResultRow) -> T
) {
    fun findAll(offset: Int = 0, limit: Int = 10): List<T> = transaction {
        table.selectAll().limit(n = limit, offset = offset.toLong())
            .map {
                toModel(it)
            }
    }

    fun findById(id: UUID): T? = transaction {
        table.select { table.id eq EntityID(id, table as IdTable<UUID>) }
            .map(toModel)
            .singleOrNull()
    }

    fun update(entity: T, updateBlock: (UpdateStatement, T) -> Unit): T {
        return transaction {
            val dbVersion = table.select { table.id eq entity.id }
                .mapNotNull { it[table.version] }
                .singleOrNull() ?: throw IllegalArgumentException("Entity not found for ID ${entity.id}")

            if (dbVersion != entity.version) {
                throw OptimisticLockException(entity.id, dbVersion, entity.version)
            }

            table.update({ table.id eq entity.id }) { updateStatement ->
                updateBlock(updateStatement, entity)
                updateStatement[table.version] = dbVersion + 1
                updateStatement[table.updatedAt] = LocalDateTime.now()
            }

            findById(entity.id ?: throw IllegalArgumentException("Failed to retrieve updated entity."))!!
        }
    }

    fun create(entity: T, buildTable: (T) -> UUID): T = transaction {
        findById(buildTable(entity))!!
    }
}
