package me.thankgodr.fintechchallegeapp.domain.model

data class PaymentRequest(
    val recipientEmail: String,
    val amount: Double,
    val currency: Currency,
    val senderName: String
)
