package br.com.transactionauthorizer.model.table

import br.com.transactionauthorizer.model.CardTransactionStatus
import org.jetbrains.exposed.sql.Column
import java.math.BigDecimal
import java.util.UUID

object CardTransactionTable : BaseTable<UUID>("card_transaction") {
    val account: Column<String> = varchar("account", 50)
    val accountId: Column<UUID> = uuid("account_id")
    val totalAmount: Column<BigDecimal> = decimal(
        name = "total_amount",
        precision = 10,
        scale = 2
    )
    val mcc: Column<String>  = varchar("mcc", 4)
    val merchant: Column<String>  = varchar("merchant", 255)
    val accountBalanceId: Column<UUID> = uuid("account_balance_id")
    val cardTransactionStatus: Column<CardTransactionStatus> =
        CardTransactionTable.enumerationByName(
            name ="card_transaction_status",
            length = 10,
            klass = CardTransactionStatus::class
        )

    override val primaryKey = PrimaryKey(id, name = "PK_CardTransaction_Id")
}
