package br.com.transactionauthorizer.model

import java.time.LocalDateTime
import java.util.*

abstract class BaseModel(
    open val id: UUID,
    open val version: Long,
    open val createdAt: LocalDateTime = LocalDateTime.now(),
    open val updatedAt: LocalDateTime = LocalDateTime.now()
)