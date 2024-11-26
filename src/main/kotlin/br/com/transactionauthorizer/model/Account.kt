package br.com.transactionauthorizer.model

import java.time.LocalDateTime

data class Account(
    override val id: Long? = null, // Nullable for when the object is created but not yet persisted,
    val name: String,
    override val version: Long = 0,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, version, createdAt, updatedAt)