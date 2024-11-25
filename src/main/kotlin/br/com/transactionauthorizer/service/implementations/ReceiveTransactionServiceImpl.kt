package br.com.transactionauthorizer.service.implementations

import br.com.transactionauthorizer.controller.model.request.ReceivedTransactionRequest
import br.com.transactionauthorizer.exceptions.AccountBalanceNotFoundByAccountIdAndTypeException
import br.com.transactionauthorizer.model.AccountBalance
import br.com.transactionauthorizer.model.AccountBalanceType
import br.com.transactionauthorizer.model.CardTransactionStatus
import br.com.transactionauthorizer.service.CardTransactionService
import br.com.transactionauthorizer.service.AccountBalanceService
import br.com.transactionauthorizer.service.AccountService
import br.com.transactionauthorizer.service.ReceiveTransactionService
import br.com.transactionauthorizer.utils.AccountBalanceTypeUtils
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
    private val accountBalanceService: AccountBalanceService,
    private val accountService: AccountService
) : ReceiveTransactionService {

    override fun receiveTransaction(request: ReceivedTransactionRequest): String {
        val accountId = request.account.toLong()
        val transactionAmount = request.totalAmount
        val accountBalanceType = AccountBalanceTypeUtils.determineBalanceType(request.merchant, request.mcc)

        return try {
            val account = accountService.getAccountById(accountId)
            if (accountBalanceType.isCash()){
                processCashTransaction(account.id!!, transactionAmount, request)
            } else {
                val accountBalance = accountBalanceService.getAccountBalanceByAccountIdAndType(
                    accountId = account.id!!,
                    type = accountBalanceType
                )
                processTransaction(accountBalance, transactionAmount, request, account.id)
            }

        } catch (ex: AccountBalanceNotFoundByAccountIdAndTypeException) {
            processCashTransaction(accountId, transactionAmount, request)
        } catch (ex: Exception) {
            TransactionStatus.ERROR.code
        }
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
            else -> processCashTransaction(accountId, transactionAmount, request)
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

    private fun processCashTransaction(
        accountId: Long,
        transactionAmount: BigDecimal,
        request: ReceivedTransactionRequest
    ) = try {
        val cashAccountBalance = getCashAccountBalance(accountId)
        if (cashAccountBalance.amount >= transactionAmount) {
            updateBalance(cashAccountBalance, request)
        } else {
            denyTransaction(request)
        }
    } catch (ex: AccountBalanceNotFoundByAccountIdAndTypeException) {
        denyTransaction(request)
    }

    @Transactional
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
}
