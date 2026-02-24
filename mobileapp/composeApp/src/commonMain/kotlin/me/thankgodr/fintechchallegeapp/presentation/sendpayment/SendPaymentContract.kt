package me.thankgodr.fintechchallegeapp.presentation.sendpayment

import kotlinx.serialization.Serializable
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.Transaction

sealed class SendPaymentIntent {
    data class UpdateRecipientEmail(val value: String) : SendPaymentIntent()
    data class UpdateAmount(val value: String) : SendPaymentIntent()
    data class UpdateSenderName(val value: String) : SendPaymentIntent()
    data class SelectCurrency(val currency: Currency) : SendPaymentIntent()
    data object SubmitPayment : SendPaymentIntent()
    data object ResetForm : SendPaymentIntent()
}

sealed class SendPaymentEffect {
    data class NavigateToDetail(val transaction: Transaction) : SendPaymentEffect()
}

data class SendPaymentState(
    val recipientEmail: String = "",
    val amount: String = "",
    val senderName: String = "",
    val selectedCurrency: Currency = Currency.USD,
    val emailError: String? = null,
    val amountError: String? = null,
    val senderNameError: String? = null,
    val currencyError: String? = null,
    val isLoading: Boolean = false,
    val successTransaction: Transaction? = null,
    val generalError: String? = null
)

@Serializable
object SendPaymentDestination
