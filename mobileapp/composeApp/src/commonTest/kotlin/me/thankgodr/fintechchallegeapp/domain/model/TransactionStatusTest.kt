package me.thankgodr.fintechchallegeapp.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionStatusTest {

    @Test
    fun fromString_completed() {
        assertEquals(TransactionStatus.COMPLETED, TransactionStatus.fromString("COMPLETED"))
    }

    @Test
    fun fromString_pending() {
        assertEquals(TransactionStatus.PENDING, TransactionStatus.fromString("PENDING"))
    }

    @Test
    fun fromString_failed() {
        assertEquals(TransactionStatus.FAILED, TransactionStatus.fromString("FAILED"))
    }

    @Test
    fun fromString_caseInsensitive() {
        assertEquals(TransactionStatus.COMPLETED, TransactionStatus.fromString("completed"))
        assertEquals(TransactionStatus.FAILED, TransactionStatus.fromString("Failed"))
    }

    @Test
    fun fromString_unknown_defaultsToPending() {
        assertEquals(TransactionStatus.PENDING, TransactionStatus.fromString("unknown"))
        assertEquals(TransactionStatus.PENDING, TransactionStatus.fromString(""))
    }
}
