package me.thankgodr.fintechchallegeapp.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
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
    private val firestoreDataSource: FirestoreDataSource,
    private val applicationScope: CoroutineScope
) : PaymentRepository {

    override suspend fun sendPayment(request: PaymentRequest): Result<Transaction> {
        return try {
            val response = apiService.sendPayment(request.toDto())
            if (response.success && response.data != null) {
                val transaction = response.data.toDomain()
                // for the purpose of demo, Save to Firestore in background — log failure but don't block payment
                applicationScope.launch {
                    val saveResult = firestoreDataSource.saveTransaction(response.data)
                    saveResult.onFailure { error ->
                        // TODO: Replace with proper logging framework (e.g. Timber)
                        println("Firestore save failed: ${error.message}")
                    }
                }
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
        return firestoreDataSource.observeTransactions()
            .map { dtos -> dtos.map { it.toDomain() } }
            .retryWhen { cause, attempt ->
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    delay(RETRY_DELAY_MS * (attempt + 1)) // linear backoff
                    true // retry
                } else {
                    throw cause // re-throw after max retries — ViewModel catches this
                }
            }
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3L
        private const val RETRY_DELAY_MS = 2000L
    }
}
