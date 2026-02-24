package me.thankgodr.fintechchallegeapp.presentation.sendpayment

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SendPaymentStateTest {
    private fun stateWith(
        email: String = "",
        amount: String = "",
        name: String = "",
        isFormValid: Boolean = email.isNotBlank() && amount.isNotBlank() && name.isNotBlank(),
    ) = SendPaymentState(
        recipientEmail = email,
        amount = amount,
        senderName = name,
        isFormValid = isFormValid,
    )

    @Test
    fun defaultState_isNotValid() {
        assertFalse(SendPaymentState().isFormValid)
    }

    @Test
    fun allFieldsFilled_isValid() {
        assertTrue(stateWith(email = "a@b.com", amount = "50", name = "Alice").isFormValid)
    }

    @Test
    fun missingEmail_isNotValid() {
        assertFalse(stateWith(email = "", amount = "50", name = "Alice").isFormValid)
    }

    @Test
    fun missingAmount_isNotValid() {
        assertFalse(stateWith(email = "a@b.com", amount = "", name = "Alice").isFormValid)
    }

    @Test
    fun missingSenderName_isNotValid() {
        assertFalse(stateWith(email = "a@b.com", amount = "50", name = "").isFormValid)
    }

    @Test
    fun blankFields_areNotValid() {
        assertFalse(stateWith(email = "   ", amount = "  ", name = "  ").isFormValid)
    }
}
