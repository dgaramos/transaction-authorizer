package br.com.transactionauthorizer.model

data class Account(
    val id: Long? = null, // Nullable for when the object is created but not yet persisted,
    val name: String
)