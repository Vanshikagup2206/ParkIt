package com.vanshika.parkit.user.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.vanshika.parkit.R
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import com.vanshika.parkit.user.data.model.ReservationRequestDataClass
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 🔹 Submit reservation request (User)
    suspend fun submitReservationRequest(request: ReservationRequestDataClass): Boolean {
        val userId = auth.currentUser?.uid ?: ""
        val finalRequest = request.copy(userId = userId)

        return try {
            val docRef = db.collection("parkingRequests").document()
            docRef.set(finalRequest.copy(id = docRef.id), SetOptions.merge()).await()

            // 🔔 Notify admin after request submission
            sendAdminNotification(
                title = "📥 New Reservation Request",
                message = "${request.userName} requested to reserve slot ${request.slotId} in ${request.zone}.",
                type = "new_reservation"
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 🔹 Fetch all reservation requests (Admin)
    suspend fun getAllReservationRequests(): List<ReservationRequestDataClass> {
        return try {
            Log.d("RepoDebug", "📥 Fetching all reservation requests from Firestore...")
            val snapshot = db.collection("parkingRequests").get().await()
            Log.d("RepoDebug", "📦 Documents fetched: ${snapshot.size()}")
            snapshot.documents.forEach {
                Log.d("RepoDebug", "📄 DocID: ${it.id}, Data: ${it.data}")
            }
            snapshot.documents.mapNotNull { it.toObject(ReservationRequestDataClass::class.java) }
        } catch (e: Exception) {
            Log.e("RepoDebug", "❌ Error fetching requests: ${e.message}", e)
            emptyList()
        }
    }

    // 🔹 Update request status (Approve / Reject)
    suspend fun updateRequestStatus(requestId: String, status: String) {
        try {
            val docRef = db.collection("parkingRequests").document(requestId)
            docRef.set(mapOf("requestStatus" to status), SetOptions.merge()).await()

            // Fetch that specific request to get user info
            val requestSnapshot = docRef.get().await()
            val request = requestSnapshot.toObject(ReservationRequestDataClass::class.java)

            val userCustomId = request?.customUserId ?: ""
            val userName = request?.userName ?: "User"

            // 🔹 Notify the user
            sendUserNotification(
                title = if (status == "Approved") "✅ Reservation Approved"
                else "❌ Reservation Rejected",
                message = "Hi $userName, your parking request has been $status.",
                userCustomId = userCustomId
            )

            // 🔹 Also keep a log for admin record
            sendAdminNotification(
                title = "📢 Reservation $status",
                message = "Request for ${request?.slotId} was $status by admin.",
                type = "status_update"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔔 Store + Push Notification
    private fun sendAdminNotification(title: String, message: String, type: String) {
        val adminId = "12200814" // 🔹 Static admin ID
        val notificationsCollection = db.collection("notifications")

        val notification = NotificationDataClass(
            title = title,
            message = message,
            type = type,
            userId = adminId,
            isRead = false
        )

        val docRef = notificationsCollection.document()
        docRef.set(notification.copy(id = docRef.id))

        // Push to admin segment
        sendPushToOneSignal(title, message, userIdOrSegment = adminId)
    }

    // 🔹 Send notification to a specific USER (after approval/rejection)
    private fun sendUserNotification(title: String, message: String, userCustomId: String) {
        val notificationsCollection = db.collection("notifications")

        val notification = NotificationDataClass(
            title = title,
            message = message,
            type = "status_update",
            userId = userCustomId,
            isRead = false
        )

        val docRef = notificationsCollection.document()
        docRef.set(notification.copy(id = docRef.id))

        // Push to user's segment (if you are using custom tags)
        sendPushToOneSignal(title, message, userIdOrSegment = userCustomId)
    }

    private fun sendPushToOneSignal(title: String, message: String, userIdOrSegment: String) {
        val apiKey = context.getString(R.string.onesignal_api_key)
        val url = "https://onesignal.com/api/v1/notifications"

        val json = JSONObject().apply {
            put("app_id", "531eda43-b91a-4e09-b931-0bd569b034e9")

            if (userIdOrSegment == "Admin") {
                put("included_segments", JSONArray().put("Admin"))
            } else {
                put("include_external_user_ids", JSONArray().put(userIdOrSegment))
            }

            put("headings", JSONObject().put("en", title))
            put("contents", JSONObject().put("en", message))
            put("priority", 10)
            put("android_visibility", 1)
            put("android_accent_color", "FF2196F3")
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", apiKey)
            .post(body)
            .build()

        Thread {
            try {
                OkHttpClient().newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    Log.d("OneSignalDebug", "📩 Sending push to OneSignal")
                    Log.d("OneSignalDebug", "➡️ Request JSON: $json")
                    Log.d("OneSignalDebug", "✅ Response Code: ${response.code}")
                    Log.d("OneSignalDebug", "📨 Response Body: $responseBody")
                }
            } catch (e: Exception) {
                Log.e("OneSignalDebug", "❌ Error sending push: ${e.message}", e)
            }
        }.start()
    }
}