package br.com.transactionauthorizer.controller.model.response

data class ReceiveTransactionResponse(
    val code: String
) {
    companion object {
        fun fromCode(code: String): ReceiveTransactionResponse {
            return ReceiveTransactionResponse(
                code = code
            )
        }
    }
}