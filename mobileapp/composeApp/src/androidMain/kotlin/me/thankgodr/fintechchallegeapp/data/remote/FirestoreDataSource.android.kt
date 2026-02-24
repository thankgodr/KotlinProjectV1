package me.thankgodr.fintechchallegeapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto

actual class FirestoreDataSource actual constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val transactionsCollection = db.collection("transactions")

    actual suspend fun saveTransaction(transaction: TransactionDto): Result<Unit> {
        return try {
            val data =
                hashMapOf(
                    "id" to transaction.id,
                    "recipientEmail" to transaction.recipientEmail,
                    "amount" to transaction.amount,
                    "currency" to transaction.currency,
                    "senderName" to transaction.senderName,
                    "status" to transaction.status,
                    "timestamp" to transaction.timestamp,
                )
            transactionsCollection.document(transaction.id).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(FirestoreException("Failed to save transaction: ${e.message}", e))
        }
    }

    actual fun observeTransactions(): Flow<List<TransactionDto>> =
        callbackFlow {
            val listener: ListenerRegistration =
                transactionsCollection
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(FirestoreException("Failed to observe transactions: ${error.message}", error))
                            return@addSnapshotListener
                        }
                        val transactions =
                            snapshot?.documents?.mapNotNull { doc ->
                                try {
                                    TransactionDto(
                                        id = doc.getString("id") ?: doc.id,
                                        recipientEmail = doc.getString("recipientEmail") ?: "",
                                        amount = doc.getDouble("amount") ?: 0.0,
                                        currency = doc.getString("currency") ?: "",
                                        senderName = doc.getString("senderName") ?: "",
                                        status = doc.getString("status") ?: "",
                                        timestamp = doc.getString("timestamp") ?: "",
                                    )
                                } catch (e: Exception) {
                                    // Skip malformed documents but don't crash the stream
                                    null
                                }
                            } ?: emptyList()
                        trySend(transactions)
                    }
            awaitClose { listener.remove() }
        }
}

/**
 * Custom exception for Firestore operations.
 * Allows callers to distinguish Firestore errors from other failures.
 */
class FirestoreException(message: String, cause: Throwable? = null) : Exception(message, cause)
