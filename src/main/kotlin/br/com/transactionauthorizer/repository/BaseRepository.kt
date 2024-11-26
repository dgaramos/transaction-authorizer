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

abstract class BaseRepository<T : BaseModel, U : BaseTable<Long>>(
    private val table: U,
    private val toModel: (ResultRow) -> T
) {
    fun findAll(): List<T> = transaction {
        table.selectAll().map {
            toModel(it)
        }
    }

    fun findById(id: Long): T? = transaction {
        table.select { table.id eq EntityID(id, table as IdTable<Long>) }
            .map(toModel)
            .singleOrNull()
    }

    fun update(entity: T, updateBlock: (UpdateStatement, T) -> Unit): T {
        return transaction {
            val dbVersion = table.select { table.id eq entity.id }
                .mapNotNull { it[table.version] }
                .singleOrNull() ?: throw IllegalArgumentException("Entity not found for ID ${entity.id}")

            if (dbVersion != entity.version) {
                throw OptimisticLockException(entity.id!!, dbVersion, entity.version)
            }

            table.update({ table.id eq entity.id }) { updateStatement ->
                updateBlock(updateStatement, entity)
                updateStatement[table.version] = dbVersion + 1
                updateStatement[table.updatedAt] = LocalDateTime.now()
            }

            findById(entity.id ?: throw IllegalArgumentException("Failed to retrieve updated entity."))!!
        }
    }

    fun create(entity: T, buildTable: (T) -> Long): T = transaction {
        findById(buildTable(entity))!!
    }
}
