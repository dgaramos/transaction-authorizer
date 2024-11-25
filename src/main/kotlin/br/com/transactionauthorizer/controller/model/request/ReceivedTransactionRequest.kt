package br.com.transactionauthorizer.controller.model.request

import java.math.BigDecimal

data class ReceivedTransactionRequest(
    val account: String,
    val totalAmount: BigDecimal,
    val mcc: String,
    val merchant: String
)