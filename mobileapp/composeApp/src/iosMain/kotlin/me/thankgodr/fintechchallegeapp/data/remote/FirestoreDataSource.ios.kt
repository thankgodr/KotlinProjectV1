package me.thankgodr.fintechchallegeapp.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto

actual class FirestoreDataSource actual constructor() {
    actual suspend fun saveTransaction(transaction: TransactionDto): Result<Unit> {
        return Result.success(Unit)
    }

    actual fun observeTransactions(): Flow<List<TransactionDto>> {
        return emptyFlow()
    }
}
