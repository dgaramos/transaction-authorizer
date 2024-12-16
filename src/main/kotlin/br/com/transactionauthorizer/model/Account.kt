package br.com.transactionauthorizer.model

import java.time.LocalDateTime
import java.util.*

data class Account(
    override val id: UUID = UUID.randomUUID(),
    val name: String,
    override val version: Long = 0,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, version, createdAt, updatedAt)