package me.thankgodr.fintechchallegeapp.data.remote

import kotlinx.coroutines.flow.Flow
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto
import org.koin.core.annotation.Single

@Single
expect class FirestoreDataSource() {
    suspend fun saveTransaction(transaction: TransactionDto)
    fun observeTransactions(): Flow<List<TransactionDto>>
}
