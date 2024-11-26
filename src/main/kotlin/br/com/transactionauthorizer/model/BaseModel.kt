package br.com.transactionauthorizer.model

import java.time.LocalDateTime

abstract class BaseModel(
    open val id: Long?,
    open val version: Long,
    open val createdAt: LocalDateTime = LocalDateTime.now(),
    open val updatedAt: LocalDateTime = LocalDateTime.now()
)