package me.thankgodr.mockapi

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val senderName: String? = null
)

@Serializable
data class Transaction(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val senderName: String,
    val status: String,
    val timestamp: String
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val errors: List<String>? = null
)
