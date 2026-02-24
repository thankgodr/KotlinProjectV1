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

@Single
class PaymentApiService(
    private val clientProvider: HttpClientProvider,
) {
    private val baseUrl: String = "http://10.0.2.2:3000"

    suspend fun sendPayment(request: PaymentRequestDto): ApiResponseDto<TransactionDto> {
        return clientProvider.client.post("$baseUrl/payments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getTransactions(): ApiResponseDto<List<TransactionDto>> {
        return clientProvider.client.get("$baseUrl/transactions").body()
    }
}
