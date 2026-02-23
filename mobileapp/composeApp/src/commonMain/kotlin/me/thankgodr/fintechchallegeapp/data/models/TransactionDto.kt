package me.thankgodr.fintechchallegeapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val senderName: String,
    val status: String,
    val timestamp: String
)
