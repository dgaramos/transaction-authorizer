package br.com.transactionauthorizer.service;

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest;

interface ReceiveTransactionService {
    fun receiveTransaction(request:ReceivedTransactionRequest): String
}