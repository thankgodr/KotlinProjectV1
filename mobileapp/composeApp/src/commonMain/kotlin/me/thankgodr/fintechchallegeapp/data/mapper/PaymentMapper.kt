package me.thankgodr.fintechchallegeapp.data.mapper

import me.thankgodr.fintechchallegeapp.data.models.PaymentRequestDto
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto
import me.thankgodr.fintechchallegeapp.domain.model.Currency
import me.thankgodr.fintechchallegeapp.domain.model.PaymentRequest
import me.thankgodr.fintechchallegeapp.domain.model.Transaction
import me.thankgodr.fintechchallegeapp.domain.model.TransactionStatus

fun PaymentRequest.toDto(): PaymentRequestDto {
    return PaymentRequestDto(
        recipientEmail = recipientEmail,
        amount = amount,
        currency = currency.currencyCode,
        senderName = senderName
    )
}

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        recipientEmail = recipientEmail,
        amount = amount,
        currency = Currency.fromCode(currency) ?: Currency.USD,
        senderName = senderName,
        status = TransactionStatus.fromString(status),
        timestamp = timestamp
    )
}

fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        id = id,
        recipientEmail = recipientEmail,
        amount = amount,
        currency = currency.currencyCode,
        senderName = senderName,
        status = status.name.lowercase(),
        timestamp = timestamp
    )
}
