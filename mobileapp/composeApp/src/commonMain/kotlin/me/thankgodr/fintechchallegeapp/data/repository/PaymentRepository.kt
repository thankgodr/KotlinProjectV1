package me.thankgodr.fintechchallegeapp.data.repository

import kotlinx.coroutines.flow.Flow
import me.thankgodr.fintechchallegeapp.data.models.PaymentRequestDto
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto

interface PaymentRepository {
    suspend fun sendPayment(request: PaymentRequestDto): Result<TransactionDto>
    fun observeTransactions(): Flow<List<TransactionDto>>
}
