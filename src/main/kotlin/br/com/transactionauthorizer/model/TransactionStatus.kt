package br.com.transactionauthorizer.model

enum class TransactionStatus(val code: String) {
    APPROVED("00"),
    DENIED("51"),
    ERROR("07")
}
