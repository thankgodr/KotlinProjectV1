package me.thankgodr.fintechchallegeapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequestDto(
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val senderName: String = ""
)
