package com.vanshika.parkit.user.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.parkit.R
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import com.vanshika.parkit.user.data.model.IssuesDataClass
import com.vanshika.parkit.user.data.model.toFireStoreMap
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class IssuesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val collection = FirebaseFirestore.getInstance().collection("issues")
    private val notificationsCollection =
        FirebaseFirestore.getInstance().collection("notifications")

    //    fun addIssue(issue: IssuesDataClass) {
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        val userEmail = currentUser?.email ?: "Unknown"
//
//        val issueWithReporter = issue.copy(reportedBy = userEmail)
//
//        collection.document(issueWithReporter.issueId)
//            .set(issueWithReporter.toFireStoreMap())
//    }
    fun addIssue(issue: IssuesDataClass) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userEmail = currentUser.email ?: return

        // 🔹 Fetch customId from Firestore using email
        FirebaseFirestore.getInstance().collection("customId")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val customId = documents.documents[0].getString("customUserId") ?: "Unknown"

                    val issueWithReporter = issue.copy(reportedBy = customId)

                    collection.document(issueWithReporter.issueId)
                        .set(issueWithReporter.toFireStoreMap())
                } else {
                    android.util.Log.e("IssueRepo", "❌ No customId found for $userEmail")
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("IssueRepo", "❌ Error fetching customId: ${e.message}", e)
            }
    }

    fun fetchAllIssues(onIssueChanged: (List<IssuesDataClass>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val issues = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(IssuesDataClass::class.java)
            } ?: emptyList()
            onIssueChanged(issues)
        }
    }

    fun updateIssueStatus(issueId: String, newStatus: String) {
        collection.document(issueId).get().addOnSuccessListener { snapshot ->
            val issue =
                snapshot.toObject(IssuesDataClass::class.java) ?: return@addOnSuccessListener
            collection.document(issueId).update("status", newStatus)

            // Send notification to user
            val notificationTitle = when (newStatus) {
                "Pending" -> "🕒 Issue Pending"
                "In Progress" -> "🔧 Issue In Progress"
                "Resolved" -> "✅ Issue Resolved"
                else -> "ℹ️ Issue Status Updated"
            }

            val notificationMessage =
                "Your issue for slot ${issue.slotId} (${issue.zoneName}) is now '$newStatus'."

            sendNotification(
                title = notificationTitle,
                message = notificationMessage,
                type = notificationTitle,
                userId = issue.reportedBy
            )
        }
    }

    fun deleteIssue(issueId: String) {
        collection.document(issueId).delete()
    }

    fun sendNotificationToAdmin(issue: IssuesDataClass) {
        val notification = NotificationDataClass(
            title = "🚨 New Issue Reported",
            message = "Slot ${issue.slotId} (${issue.zoneName}): ${issue.issueType}" +
                    (issue.customDescription.let { "\nDetails: $it" }),
            type = "issue_reported",
            userId = null,
            isRead = false
        )
        val docRef = notificationsCollection.document()
        docRef.set(notification.copy(id = docRef.id))

        // Push to OneSignal Admin only
        sendPushToOneSignal(null, notification.title, notification.message)
    }

    private fun sendNotification(title: String, message: String, type: String, userId: String) {
        val notification = NotificationDataClass(
            title = title,
            message = message,
            type = type,
            userId = userId,
            isRead = false
        )
        val docRef = notificationsCollection.document()
        docRef.set(notification.copy(id = docRef.id))

        // Push to OneSignal for that specific user
        sendPushToOneSignal(userId, title, message)
    }

    private fun sendPushToOneSignal(userId: String?, title: String, message: String) {
        val apiKey = context.getString(R.string.onesignal_api_key)

        val url = "https://onesignal.com/api/v1/notifications"

        val adminId = "12200814"

        val json = JSONObject().apply {
            put("app_id", "531eda43-b91a-4e09-b931-0bd569b034e9")
            if (userId != null) {
                put("include_external_user_ids", JSONArray().put(userId))
            } else {
                put("include_external_user_ids", JSONArray().put(adminId))
            }
            put("headings", JSONObject().put("en", title))
            put("contents", JSONObject().put("en", message))
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader(
                "Authorization",
                apiKey
            )
            .post(body)
            .build()

        Thread {
            try {
                OkHttpClient().newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    android.util.Log.d("OneSignalDebug", "📩 Sending push to OneSignal")
                    android.util.Log.d("OneSignalDebug", "➡️ Request JSON: $json")
                    android.util.Log.d("OneSignalDebug", "✅ Response Code: ${response.code}")
                    android.util.Log.d("OneSignalDebug", "📨 Response Body: $responseBody")
                }
            } catch (e: Exception) {
                android.util.Log.e("OneSignalDebug", "❌ Error sending push: ${e.message}", e)
            }
        }.start()
    }
}