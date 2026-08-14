package br.com.transactionauthorizer.model

import java.math.BigDecimal

data class TransactionCommand(
    val account: String,
    val totalAmount: BigDecimal,
    val mcc: String,
    val merchant: String
)
