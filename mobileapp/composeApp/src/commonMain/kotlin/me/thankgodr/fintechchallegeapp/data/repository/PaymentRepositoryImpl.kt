package me.thankgodr.fintechchallegeapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import me.thankgodr.fintechchallegeapp.data.models.PaymentRequestDto
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto
import me.thankgodr.fintechchallegeapp.data.remote.PaymentApiService

class PaymentRepositoryImpl(
    private val apiService: PaymentApiService
) : PaymentRepository {

    override suspend fun sendPayment(request: PaymentRequestDto): Result<TransactionDto> {
        return try {
            val response = apiService.sendPayment(request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(
                    Exception(response.errors?.joinToString(", ") ?: "Unknown error")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTransactions(): Flow<List<TransactionDto>>{
       return  emptyFlow()
    }
}
