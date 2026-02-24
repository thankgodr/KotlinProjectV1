package me.thankgodr.fintechchallegeapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.thankgodr.fintechchallegeapp.data.mapper.toDomain
import me.thankgodr.fintechchallegeapp.data.mapper.toDto
import me.thankgodr.fintechchallegeapp.data.remote.FirestoreDataSource
import me.thankgodr.fintechchallegeapp.data.remote.PaymentApiService
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.repository.PaymentRepository
import org.koin.core.annotation.Single

@Single
class PaymentRepositoryImpl(
    private val apiService: PaymentApiService,
    private val firestoreDataSource: FirestoreDataSource
) : PaymentRepository {

    override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> {
        return try {
            val response = apiService.sendPayment(request.toDto())
            if (response.success && response.data != null) {
                val transaction = response.data.toDomain()
                firestoreDataSource.saveTransaction(response.data)
                Result.success(transaction)
            } else {
                Result.failure(
                    Exception(response.errors?.joinToString(", ") ?: "Unknown error")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTransactions(): Flow<List<Transaction>> {
        return firestoreDataSource.observeTransactions().map { dtos ->
            dtos.map { it.toDomain() }
        }
    }
}
