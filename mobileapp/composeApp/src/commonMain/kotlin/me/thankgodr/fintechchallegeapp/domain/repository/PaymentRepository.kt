package me.thankgodr.fintechchallegeapp.domain.repository

import kotlinx.coroutines.flow.Flow
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction

interface PaymentRepository {
    suspend fun sendPayment(request: PaymentRequest): Result<Transaction>
    fun observeTransactions(): Flow<List<Transaction>>
}
