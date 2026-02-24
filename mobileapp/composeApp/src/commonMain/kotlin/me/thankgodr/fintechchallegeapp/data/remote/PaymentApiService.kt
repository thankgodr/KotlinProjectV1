package me.thankgodr.fintechchallegeapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.thankgodr.fintechchallegeapp.data.models.ApiResponseDto
import me.thankgodr.fintechchallegeapp.data.models.PaymentRequestDto
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto
import me.thankgodr.fintechchallegeapp.di.HttpClientProvider
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

@Single
class PaymentApiService(
    private val clientProvider: HttpClientProvider,
) {
    private val baseUrl: String = "http://10.0.2.2:3000"

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendPayment(request: PaymentRequestDto, useApi: Boolean = true): ApiResponseDto<TransactionDto> {
        if (!useApi) {
            val localTransaction = TransactionDto(
                id = kotlin.uuid.Uuid.random().toString(),
                recipientEmail = request.recipientEmail,
                amount = request.amount,
                currency = request.currency,
                senderName = request.senderName.orEmpty(),
                status = "COMPLETED",
                timestamp = kotlin.random.Random.nextLong().toString()
            )
            return ApiResponseDto(success = true, data = localTransaction)
        }
        return clientProvider.client.post("$baseUrl/payments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getTransactions(): ApiResponseDto<List<TransactionDto>> {
        return clientProvider.client.get("$baseUrl/transactions").body()
    }
}
