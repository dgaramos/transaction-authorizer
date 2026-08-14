package br.com.transactionauthorizer.service

import br.com.transactionauthorizer.model.TransactionCommand

interface ReceiveTransactionService {
    fun receiveTransaction(command: TransactionCommand): String
}
