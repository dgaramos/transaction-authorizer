package br.com.transactionauthorizer.service.implementations

import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.repository.CardTransactionRepository
import br.com.transactionauthorizer.service.CardTransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal


@Service
class CardTransactionServiceImpl(
    private val cardTransactionRepository: CardTransactionRepository
) : CardTransactionService {

    override fun getAllTransactionsByAccountBalanceId(accountBalanceId: Long) =
        cardTransactionRepository.getAllTransactionsByAccountBalanceId(accountBalanceId)

    override fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: Long,
        transactionStatus: CardTransactionStatus,
        merchant: String
    ) = cardTransactionRepository.createTransaction(
        account = account,
        totalAmount = totalAmount,
        mcc = mcc,
        accountBalanceId = accountBalanceId,
        cardTransactionStatus = transactionStatus,
        merchant = merchant
    )
}
