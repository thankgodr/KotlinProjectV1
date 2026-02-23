package me.thankgodr.fintechchallegeapp.domain.usecase

import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import me.thankgodr.fintechchallegeapp.domain.validation.PaymentValidator
import me.thankgodr.fintechchallegeapp.domain.validation.ValidationError

class SendPaymentUseCase(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(
        recipientEmail: String,
        amount: Double,
        currencyCode: String,
        senderName: String
    ): Result<Transaction> {
        val errors = PaymentValidator.validateAll(recipientEmail, amount, currencyCode, senderName)
        if (errors.isNotEmpty()) {
            return Result.failure(
                ValidationException(errors)
            )
        }

        val currency = Currency.fromCode(currencyCode)!!
        val request = PaymentRequest(
            recipientEmail = recipientEmail,
            amount = amount,
            currency = currency,
            senderName = senderName
        )

        return repository.sendPayment(request)
    }
}

class ValidationException(val errors: List<ValidationError>) :
    Exception(errors.joinToString(", ") { it.message })
