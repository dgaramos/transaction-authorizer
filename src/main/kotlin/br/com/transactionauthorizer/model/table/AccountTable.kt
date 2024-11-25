package br.com.transactionauthorizer.model.table

import org.jetbrains.exposed.dao.id.IdTable

object AccountTable : IdTable<Long>("account") {
    override val id = long("id").autoIncrement().entityId()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id, name = "PK_Account_Id")
}
