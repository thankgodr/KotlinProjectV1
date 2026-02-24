package me.thankgodr.fintechchallegeapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import me.thankgodr.fintechchallegeapp.data.models.ApiResponseDto
import me.thankgodr.fintechchallegeapp.data.models.PaymentRequestDto
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto
import me.thankgodr.fintechchallegeapp.di.HttpClientProvider
import org.koin.core.annotation.Single
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@Single
class PaymentApiService(
    private val clientProvider: HttpClientProvider,
) {
    companion object {
        // TODO: Inject via BuildConfig for per-environment configuration
        private const val BASE_URL = "http://10.0.2.2:3000"
        private const val USE_LOCAL_MODE = true
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    suspend fun sendPayment(request: PaymentRequestDto): ApiResponseDto<TransactionDto> {
        if (USE_LOCAL_MODE) {
            val now = kotlin.time.Clock.System.now()
            val localTransaction = TransactionDto(
                id = kotlin.uuid.Uuid.random().toString(),
                recipientEmail = request.recipientEmail,
                amount = request.amount,
                currency = request.currency,
                senderName = request.senderName,
                status = "COMPLETED",
                timestamp = now.toString()
            )
            delay(3000)
            return ApiResponseDto(success = true, data = localTransaction)
        }
        return clientProvider.client.post("$BASE_URL/payments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getTransactions(): ApiResponseDto<List<TransactionDto>> {
        return clientProvider.client.get("$BASE_URL/transactions").body()
    }
}
