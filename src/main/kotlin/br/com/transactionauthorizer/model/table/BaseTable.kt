package br.com.transactionauthorizer.model.table

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

abstract class BaseTable<T : Comparable<T>>(name: String) : IdTable<UUID>(name) {
    override val id = uuid("id").entityId()
    val version = long("version").default(0)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

}
