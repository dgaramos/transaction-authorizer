package br.com.transactionauthorizer.service.implementations

import br.com.transactionauthorizer.constants.MerchantNames
import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByAccountIdAndTypeException
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.service.CardTransactionService
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.ReceiveTransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import org.springframework.transaction.annotation.Transactional

enum class TransactionStatus(val code: String) {
    APPROVED("00"),
    DENIED("51"),
    ERROR("07");
}

@Service
class ReceiveTransactionServiceImpl(
    private val cardTransactionService: CardTransactionService,
    private val accountBalanceService: AccountBalanceService
) : ReceiveTransactionService {

    @Transactional
    override fun receiveTransaction(request: ReceivedTransactionRequest): String {
        val accountId = request.account.toLong()
        val transactionAmount = request.totalAmount
        val merchantName = request.merchant

        val accountBalanceType = determineBalanceType(merchantName, request.mcc)

        try {
            val accountBalance = accountBalanceService.getAccountBalanceByAccountIdAndType(
                accountId = accountId,
                type = accountBalanceType
            )
            return processTransaction(accountBalance, transactionAmount, request, accountId)
        } catch (e: AccountBalanceNotFoundByAccountIdAndTypeException) {
            return TransactionStatus.ERROR.code
        }
    }

    private fun determineBalanceType(merchantName: String, mcc: String): AccountBalanceType {
        return balanceTypeBasedOnMerchantName(merchantName) ?: balanceTypeBasedOnMCC(mcc)
    }

    private fun processTransaction(
        accountBalance: AccountBalance,
        transactionAmount: BigDecimal,
        request: ReceivedTransactionRequest,
        accountId: Long
    ): String {
        return when {
            accountBalance.isCash() -> processCashAccount(accountBalance, transactionAmount, request)
            accountBalance.amount >= transactionAmount -> updateBalance(accountBalance, request)
            else -> processFallbackToCashAccount(accountId, transactionAmount, request)
        }
    }

    private fun processCashAccount(
        accountBalance: AccountBalance,
        transactionAmount: BigDecimal,
        request: ReceivedTransactionRequest
    ): String {
        return if (accountBalance.amount >= transactionAmount) {
            updateBalance(accountBalance, request)
        } else {
            denyTransaction(request)
        }
    }

    private fun processFallbackToCashAccount(
        accountId: Long,
        transactionAmount: BigDecimal,
        request: ReceivedTransactionRequest
    ): String {
        val cashAccountBalance = getCashAccountBalance(accountId)
        return if (cashAccountBalance.amount >= transactionAmount) {
            updateBalance(cashAccountBalance, request)
        } else {
            denyTransaction(request)
        }
    }

    private fun updateBalance(accountBalance: AccountBalance, request: ReceivedTransactionRequest): String {
        cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.APPROVED,
            merchant = request.merchant
        )
        val updatedAmount = accountBalance.amount - request.totalAmount
        accountBalanceService.updateAccountBalanceAmount(accountBalance.id!!, updatedAmount)
        return TransactionStatus.APPROVED.code
    }

    private fun denyTransaction(request: ReceivedTransactionRequest): String {
        cardTransactionService.createTransaction(
            account = request.account,
            totalAmount = request.totalAmount,
            mcc = request.mcc,
            transactionStatus = CardTransactionStatus.DENIED,
            merchant = request.merchant
        )
        return TransactionStatus.DENIED.code
    }

    private fun getCashAccountBalance(accountId: Long): AccountBalance =
        accountBalanceService.getAccountBalanceByAccountIdAndType(accountId, AccountBalanceType.CASH)

    private fun balanceTypeBasedOnMerchantName(merchantName: String) =
        when (merchantName) {
            in MerchantNames.FOOD_MERCHANTS -> AccountBalanceType.FOOD
            in MerchantNames.MEAL_MERCHANTS -> AccountBalanceType.MEAL
            else -> null
        }

    private fun balanceTypeBasedOnMCC(mcc: String) =
        when (mcc) {
            "5411", "5412" -> AccountBalanceType.FOOD
            "5811", "5812" -> AccountBalanceType.MEAL
            else -> AccountBalanceType.CASH
        }

}