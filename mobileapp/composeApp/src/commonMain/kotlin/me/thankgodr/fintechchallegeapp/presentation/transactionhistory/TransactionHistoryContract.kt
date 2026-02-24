package me.thankgodr.fintechchallegeapp.presentation.transactionhistory

import kotlinx.serialization.Serializable
import me.thankgodr.fintechchallegeapp.domain.model.Transaction

sealed class TransactionHistoryIntent {
    data object LoadTransactions : TransactionHistoryIntent()
    data object Retry : TransactionHistoryIntent()
}

data class TransactionHistoryState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)


@Serializable
object TransactionHistoryDestination
