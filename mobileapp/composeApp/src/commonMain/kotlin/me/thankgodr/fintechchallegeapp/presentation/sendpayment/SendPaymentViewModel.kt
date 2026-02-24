package me.thankgodr.fintechchallegeapp.presentation.sendpayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.thankgodr.fintechchallegeapp.domain.usecase.SendPaymentUseCase
import me.thankgodr.fintechchallegeapp.domain.validation.PaymentValidator
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SendPaymentViewModel(
    private val sendPaymentUseCase: SendPaymentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SendPaymentState())
    val state: StateFlow<SendPaymentState> = _state.asStateFlow()

    private val _events = Channel<SendPaymentEvents>()
    val events = _events.receiveAsFlow()


    fun onIntent(intent: SendPaymentIntent) {
        when (intent) {
            is SendPaymentIntent.UpdateRecipientEmail -> reduce {
                copy(recipientEmail = intent.value, emailError = null, generalError = null)
            }
            is SendPaymentIntent.UpdateAmount -> reduce {
                copy(amount = intent.value, amountError = null, generalError = null)
            }
            is SendPaymentIntent.UpdateSenderName -> reduce {
                copy(senderName = intent.value, senderNameError = null, generalError = null)
            }
            is SendPaymentIntent.SelectCurrency -> reduce {
                copy(selectedCurrency = intent.currency, currencyError = null, generalError = null)
            }
            is SendPaymentIntent.SubmitPayment -> submitPayment()
            is SendPaymentIntent.ResetForm -> reduce { SendPaymentState() }
            SendPaymentIntent.NavigateToHistory -> {
               viewModelScope.launch {
                   _events.send(SendPaymentEvents.NavigateToHistory)
                   reduce { SendPaymentState()  }
               }
            }
        }
    }

    private fun submitPayment() {
        val current = _state.value
        val parsedAmount = current.amount.toDoubleOrNull() ?: 0.0

        val emailErr = PaymentValidator.validateEmail(current.recipientEmail)
        val amountErr = PaymentValidator.validateAmount(parsedAmount)
        val nameErr = PaymentValidator.validateSenderName(current.senderName)

        if (emailErr != null || amountErr != null || nameErr != null) {
            reduce {
                copy(
                    emailError = emailErr?.message,
                    amountError = amountErr?.message,
                    senderNameError = nameErr?.message
                )
            }
            return
        }

        reduce { copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            val result = sendPaymentUseCase(
                recipientEmail = current.recipientEmail.trim(),
                amount = parsedAmount,
                currencyCode = current.selectedCurrency.currencyCode,
                senderName = current.senderName.trim()
            )


            result.fold(
                onSuccess = { transaction ->
                    reduce { copy(isLoading = false, successTransaction = transaction) }
                },
                onFailure = { error ->
                    reduce {
                        copy(
                            isLoading = false,
                            generalError = error.message ?: "Payment failed"
                        )
                    }
                }
            )
        }
    }

    private fun reduce(block: SendPaymentState.() -> SendPaymentState) {
        val newState = _state.value.block()
        _state.value = newState.copy(
            isFormValid = newState.recipientEmail.isNotBlank() &&
                    newState.amount.isNotBlank() &&
                    newState.senderName.isNotBlank()
        )
    }
}
