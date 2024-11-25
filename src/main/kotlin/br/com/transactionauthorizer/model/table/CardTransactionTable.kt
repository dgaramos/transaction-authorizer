package br.com.transactionauthorizer.model.table

import br.com.transactionauthorizer.model.CardTransactionStatus
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object CardTransactionTable : IdTable<Long>("card_transaction") {
    override val id: Column<EntityID<Long>> = long("id").autoIncrement().entityId()
    val account = varchar("account", 50)
    val totalAmount = decimal(
        name = "total_amount",
        precision = 10,
        scale = 2
    )
    val mcc = varchar("mcc", 4)
    val merchant = varchar("merchant", 255)
    val cardTransactionStatus: Column<CardTransactionStatus> =
        CardTransactionTable.enumerationByName(
            name ="card_transaction_status",
            length = 10,
            klass = CardTransactionStatus::class
        )
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id, name = "PK_CardTransaction_Id")
}
