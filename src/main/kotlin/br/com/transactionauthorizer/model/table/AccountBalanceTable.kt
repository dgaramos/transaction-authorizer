package br.com.transactionauthorizer.model.table

import br.com.transactionauthorizer.model.AccountBalanceType
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import java.math.BigDecimal

object AccountBalanceTable: IdTable<Long>("account_balance") {
    override val id: Column<EntityID<Long>> = long("id").autoIncrement().entityId()
    val accountId: Column<Long> = long("account_id")
    val accountBalanceType: Column<AccountBalanceType> = enumerationByName(
        name ="account_balance_type",
        length = 10,
        klass = AccountBalanceType::class
    )
    val amount: Column<BigDecimal> = decimal(
        name = "amount",
        precision = 10,
        scale = 2
    )

    override val primaryKey = PrimaryKey(id, name = "PK_AccountBalance_Id")
}
