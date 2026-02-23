package me.thankgodr.fintechchallegeapp.domain.validation

import me.thankgodr.fintechchallegeapp.domain.model.Currency

sealed class ValidationError(val message: String) {
    data object EmptyEmail : ValidationError("Email is required")
    data object InvalidEmail : ValidationError("Invalid email address")
    data object InvalidAmount : ValidationError("Amount must be greater than 0")
    data object InvalidCurrency : ValidationError("Currency must be USD or EUR")
    data object EmptySenderName : ValidationError("Sender name is required")
}

object PaymentValidator {
    private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun validateEmail(email: String): ValidationError? {
        return when {
            email.isBlank() -> ValidationError.EmptyEmail
            !EMAIL_REGEX.matches(email) -> ValidationError.InvalidEmail
            else -> null
        }
    }

    fun validateAmount(amount: Double): ValidationError? {
        return if (amount <= 0) ValidationError.InvalidAmount else null
    }

    fun validateCurrency(currencyCode: String): ValidationError? {
        return if (Currency.fromCode(currencyCode) == null) ValidationError.InvalidCurrency else null
    }

    fun validateSenderName(name: String): ValidationError? {
        return if (name.isBlank()) ValidationError.EmptySenderName else null
    }

    fun validateAll(
        email: String,
        amount: Double,
        currencyCode: String,
        senderName: String
    ): List<ValidationError> {
        return listOfNotNull(
            validateEmail(email),
            validateAmount(amount),
            validateCurrency(currencyCode),
            validateSenderName(senderName)
        )
    }
}
