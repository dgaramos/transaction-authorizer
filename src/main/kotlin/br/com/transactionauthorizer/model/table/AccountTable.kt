package br.com.transactionauthorizer.model.table

import org.jetbrains.exposed.sql.Column
import java.util.UUID


object AccountTable : BaseTable<UUID>("account") {
    val name: Column<String> = varchar("name", 50)

    override val primaryKey = PrimaryKey(id, name = "PK_Account_Id")
}
