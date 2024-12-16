package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*


@ResponseStatus(HttpStatus.BAD_REQUEST)
class OptimisticLockException(id: UUID, dbVersion: Long, actualVersion: Long) :
    RuntimeException("Failed to update record with id=$id due to optimistic lock violation: dbVersion=$dbVersion, actualVersion=$actualVersion")
