package me.thankgodr.fintechchallegeapp.domain.validation

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaymentValidatorTest {
    // ── Email Validation ──

    @Test
    fun validEmail_returnsNull() {
        assertNull(PaymentValidator.validateEmail("user@example.com"))
    }

    @Test
    fun emptyEmail_returnsEmptyEmailError() {
        assertIs<ValidationError.EmptyEmail>(PaymentValidator.validateEmail(""))
    }

    @Test
    fun blankEmail_returnsEmptyEmailError() {
        assertIs<ValidationError.EmptyEmail>(PaymentValidator.validateEmail("   "))
    }

    @Test
    fun emailWithoutAtSign_returnsInvalidEmail() {
        assertIs<ValidationError.InvalidEmail>(PaymentValidator.validateEmail("userexample.com"))
    }

    @Test
    fun emailWithoutDomain_returnsInvalidEmail() {
        assertIs<ValidationError.InvalidEmail>(PaymentValidator.validateEmail("user@"))
    }

    @Test
    fun emailWithoutTld_returnsInvalidEmail() {
        assertIs<ValidationError.InvalidEmail>(PaymentValidator.validateEmail("user@example"))
    }

    // ── Amount Validation ──

    @Test
    fun positiveAmount_returnsNull() {
        assertNull(PaymentValidator.validateAmount(100.0))
    }

    @Test
    fun zeroAmount_returnsInvalidAmount() {
        assertIs<ValidationError.InvalidAmount>(PaymentValidator.validateAmount(0.0))
    }

    @Test
    fun negativeAmount_returnsInvalidAmount() {
        assertIs<ValidationError.InvalidAmount>(PaymentValidator.validateAmount(-10.0))
    }

    @Test
    fun smallPositiveAmount_returnsNull() {
        assertNull(PaymentValidator.validateAmount(0.01))
    }

    // ── Currency Validation ──

    @Test
    fun validCurrencyUSD_returnsNull() {
        assertNull(PaymentValidator.validateCurrency("USD"))
    }

    @Test
    fun validCurrencyEUR_returnsNull() {
        assertNull(PaymentValidator.validateCurrency("EUR"))
    }

    @Test
    fun caseInsensitiveCurrency_returnsNull() {
        assertNull(PaymentValidator.validateCurrency("usd"))
    }

    @Test
    fun unsupportedCurrency_returnsInvalidCurrency() {
        assertIs<ValidationError.InvalidCurrency>(PaymentValidator.validateCurrency("GBP"))
    }

    @Test
    fun emptyCurrency_returnsInvalidCurrency() {
        assertIs<ValidationError.InvalidCurrency>(PaymentValidator.validateCurrency(""))
    }

    // ── Sender Name Validation ──

    @Test
    fun validSenderName_returnsNull() {
        assertNull(PaymentValidator.validateSenderName("John Doe"))
    }

    @Test
    fun emptySenderName_returnsError() {
        assertIs<ValidationError.EmptySenderName>(PaymentValidator.validateSenderName(""))
    }

    @Test
    fun blankSenderName_returnsError() {
        assertIs<ValidationError.EmptySenderName>(PaymentValidator.validateSenderName("   "))
    }

    // ── validateAll ──

    @Test
    fun allValid_returnsEmptyList() {
        val errors = PaymentValidator.validateAll("a@b.com", 50.0, "USD", "Alice")
        assertTrue(errors.isEmpty())
    }

    @Test
    fun allInvalid_returnsFourErrors() {
        val errors = PaymentValidator.validateAll("", 0.0, "GBP", "")
        assertTrue(errors.size == 4)
    }

    @Test
    fun partiallyInvalid_returnsOnlyFailedValidations() {
        val errors = PaymentValidator.validateAll("valid@email.com", -5.0, "USD", "Name")
        assertTrue(errors.size == 1)
        assertIs<ValidationError.InvalidAmount>(errors.first())
    }
}
