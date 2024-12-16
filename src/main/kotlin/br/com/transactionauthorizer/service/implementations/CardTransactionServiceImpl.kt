package br.com.transactionauthorizer.service.implementations

import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.repository.CardTransactionRepository
import br.com.transactionauthorizer.service.CardTransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID


@Service
class CardTransactionServiceImpl(
    private val cardTransactionRepository: CardTransactionRepository
) : CardTransactionService {

    override fun getAllTransactionsByAccountBalanceId(accountBalanceId: UUID, offset: Int, limit: Int) =
        cardTransactionRepository.getAllTransactionsByAccountBalanceId(accountBalanceId, offset, limit)

    override fun createTransaction(
        account: String,
        totalAmount: BigDecimal,
        mcc: String,
        accountBalanceId: UUID,
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
