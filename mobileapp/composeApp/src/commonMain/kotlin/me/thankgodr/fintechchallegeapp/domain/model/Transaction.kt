package me.thankgodr.fintechchallegeapp.domain.model

data class Transaction(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: Currency,
    val senderName: String,
    val status: TransactionStatus,
    val timestamp: String
)

enum class TransactionStatus {
    COMPLETED,
    PENDING,
    FAILED;

    companion object {
        fun fromString(value: String): TransactionStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}
