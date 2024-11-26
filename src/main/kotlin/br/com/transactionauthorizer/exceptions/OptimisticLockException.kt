package br.com.transactionauthorizer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.BAD_REQUEST)
class OptimisticLockException(id: Long, dbVersion: Long, actualVersion: Long) :
    RuntimeException("Failed to update record with id=$id due to optimistic lock violation: dbVersion=$dbVersion, actualVersion=$actualVersion")
