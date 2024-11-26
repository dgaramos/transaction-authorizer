package br.com.transactionauthorizer.model.table

import br.com.transactionauthorizer.model.CardTransactionStatus
import org.jetbrains.exposed.sql.Column

object CardTransactionTable : BaseTable<Long>("card_transaction") {
    val account = varchar("account", 50)
    val totalAmount = decimal(
        name = "total_amount",
        precision = 10,
        scale = 2
    )
    val mcc = varchar("mcc", 4)
    val merchant = varchar("merchant", 255)
    val accountBalanceId: Column<Long> = long("account_balance_id")
    val cardTransactionStatus: Column<CardTransactionStatus> =
        CardTransactionTable.enumerationByName(
            name ="card_transaction_status",
            length = 10,
            klass = CardTransactionStatus::class
        )

    override val primaryKey = PrimaryKey(id, name = "PK_CardTransaction_Id")
}
