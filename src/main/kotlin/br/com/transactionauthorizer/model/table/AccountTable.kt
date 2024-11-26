package br.com.transactionauthorizer.model.table


object AccountTable : BaseTable<Long>("account") {
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id, name = "PK_Account_Id")
}
