package me.thankgodr.mockapi

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.*

// In-memory transaction store
val transactions = mutableListOf<Transaction>()

fun main() {
    embeddedServer(Netty, port = 3000) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Get)
        }

        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse<Nothing>(success = false, errors = listOf(cause.localizedMessage ?: "Unknown error"))
                )
            }
        }

        routing {
            get("/health") {
                call.respond(mapOf("status" to "ok"))
            }

            post("/payments") {
                val request = call.receive<PaymentRequest>()

                // Validation
                val errors = mutableListOf<String>()

                if (request.recipientEmail.isBlank()) {
                    errors.add("recipientEmail is required")
                } else {
                    val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                    if (!emailRegex.matches(request.recipientEmail)) {
                        errors.add("recipientEmail must be a valid email address")
                    }
                }

                if (request.amount <= 0) {
                    errors.add("amount must be a positive number")
                }

                val allowedCurrencies = listOf("USD", "EUR")
                if (request.currency !in allowedCurrencies) {
                    errors.add("currency must be one of: ${allowedCurrencies.joinToString(", ")}")
                }

                if (errors.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Nothing>(success = false, errors = errors)
                    )
                    return@post
                }

                // Create transaction
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    recipientEmail = request.recipientEmail,
                    amount = request.amount,
                    currency = request.currency,
                    senderName = request.senderName ?: "Anonymous",
                    status = "completed",
                    timestamp = Instant.now().toString()
                )

                transactions.add(transaction)

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(success = true, data = transaction)
                )
            }

            get("/transactions") {
                call.respond(
                    ApiResponse(success = true, data = transactions.toList())
                )
            }
        }
    }.start(wait = true)
}
