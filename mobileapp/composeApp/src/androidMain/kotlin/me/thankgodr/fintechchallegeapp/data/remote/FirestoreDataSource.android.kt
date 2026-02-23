package me.thankgodr.fintechchallegeapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import me.thankgodr.fintechchallegeapp.data.models.TransactionDto

actual class FirestoreDataSource actual constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val transactionsCollection = db.collection("transactions")

    actual suspend fun saveTransaction(transaction: TransactionDto) {
        val data = hashMapOf(
            "id" to transaction.id,
            "recipientEmail" to transaction.recipientEmail,
            "amount" to transaction.amount,
            "currency" to transaction.currency,
            "senderName" to transaction.senderName,
            "status" to transaction.status,
            "timestamp" to transaction.timestamp
        )
        transactionsCollection.document(transaction.id).set(data).await()
    }

    actual fun observeTransactions(): Flow<List<TransactionDto>> = callbackFlow {
        val listener: ListenerRegistration = transactionsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val transactions = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        TransactionDto(
                            id = doc.getString("id") ?: doc.id,
                            recipientEmail = doc.getString("recipientEmail") ?: "",
                            amount = doc.getDouble("amount") ?: 0.0,
                            currency = doc.getString("currency") ?: "",
                            senderName = doc.getString("senderName") ?: "",
                            status = doc.getString("status") ?: "",
                            timestamp = doc.getString("timestamp") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(transactions)
            }
        awaitClose { listener.remove() }
    }
}
