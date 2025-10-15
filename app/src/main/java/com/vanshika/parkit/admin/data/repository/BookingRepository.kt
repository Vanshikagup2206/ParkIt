package com.vanshika.parkit.admin.data.repository

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vanshika.parkit.R
import com.vanshika.parkit.admin.data.model.BookingDetailsDataClass
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import com.vanshika.parkit.admin.data.model.toFireStoreMap
import com.vanshika.parkit.admin.screen.home.SlotStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("bookings")
    private val notificationsCollection = firestore.collection("notifications")

    fun addBookings(booking: BookingDetailsDataClass) {
        collection.document(booking.slotId)
            .set(
                booking.toFireStoreMap()
                    .plus("status" to booking.status.name)
                    .plus("customUserId" to booking.customUserId)
            )
            .addOnSuccessListener {
                sendNotification(
                    title = "✅ Booking Confirmed",
                    message = "Your booking for slot ${booking.slotId} is confirmed!",
                    type = "booking_confirmed",
                    userId = booking.customUserId
                )
            }
    }

    fun fetchBookings(onBookingChanged: (List<Triple<String, String, String>>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val bookings = snapshot?.documents?.mapNotNull { document ->
                val id = document.id
                val userName = document.getString("userName") ?: ""
                val status = document.getString("status") ?: SlotStatus.AVAILABLE.name

                val endTime = document.getTimestamp("bookingEndTime")
                val currentStatus = document.getString("status")

                // Auto-cancel only if booking expired AND still booked
                if (endTime != null &&
                    endTime.toDate().before(Calendar.getInstance().time) &&
                    currentStatus == SlotStatus.BOOKED.name
                ) {
                    updateSlotStatus(id, SlotStatus.AVAILABLE, document.getString("customUserId"))
                }

                Triple(id, userName, status)
            } ?: emptyList()

            onBookingChanged(bookings)
        }
    }

    fun updateSlotStatus(slotId: String, newStatus: SlotStatus, userId: String? = null) {
        val docRef = collection.document(slotId)
        docRef.get().addOnSuccessListener { snapshot ->
            val oldStatus = snapshot.getString("status")

            // If status is already the same → do nothing
            if (oldStatus == newStatus.name) return@addOnSuccessListener

            if (snapshot.exists()) {
                docRef.update("status", newStatus.name)
            } else {
                docRef.set(mapOf("slotId" to slotId, "status" to newStatus.name))
            }

            // Send notification only on actual status change
            when (newStatus) {
                SlotStatus.AVAILABLE -> userId?.let {
                    sendNotification(
                        title = "🚨 Spot Freed Up!",
                        message = "Your booking for slot $slotId expired ⏰",
                        type = "booking_cancelled",
                        userId = it
                    )
                }

                SlotStatus.MAINTENANCE -> sendNotification(
                    title = "⛔ Slot Under Maintenance",
                    message = "Slot $slotId is under maintenance. Sorry for inconvenience.",
                    type = "maintenance",
                    userId = null
                )

                SlotStatus.BOOKED -> userId?.let {
                    sendNotification(
                        title = "✅ Booking Confirmed",
                        message = "Your booking for slot $slotId is confirmed.",
                        type = "booking_confirmed",
                        userId = it
                    )
                }

                else -> {}
            }
        }
    }

    fun fetchUpcomingBookings(
        userId: String,
        onBookingChanged: (List<BookingDetailsDataClass>) -> Unit
    ) {
        collection.whereEqualTo("customUserId", userId)
            .whereGreaterThan("bookingEndTime", Timestamp.now())
            .orderBy("bookingEndTime")
            .addSnapshotListener { snapshot, _ ->
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(BookingDetailsDataClass::class.java)
                } ?: emptyList()
                onBookingChanged(bookings)
            }
    }

    fun fetchBookingHistory(
        userId: String,
        onBookingChanged: (List<BookingDetailsDataClass>) -> Unit
    ) {
        collection.whereEqualTo("customUserId", userId)
            .whereLessThan("bookingEndTime", Timestamp.now())
            .orderBy("bookingEndTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(BookingDetailsDataClass::class.java)
                } ?: emptyList()
                onBookingChanged(bookings)
            }
    }

    fun addBookingHistory(booking: BookingDetailsDataClass) {
        firestore.collection("bookingHistory")
            .add(booking)
    }

    fun fetchAllBookingHistory(onResult: (List<BookingDetailsDataClass>) -> Unit) {
        firestore.collection("bookingHistory")
            .addSnapshotListener { snapshot, _ ->
                val bookings = snapshot?.documents
                    ?.mapNotNull { it.toObject(BookingDetailsDataClass::class.java) }
                    ?: emptyList()
                onResult(bookings)
            }
    }

    fun fetchAllUsers(onResult: (List<BookingDetailsDataClass>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val users = snapshot?.documents
                ?.mapNotNull { it.toObject(BookingDetailsDataClass::class.java) }
                ?: emptyList()
            onResult(users)
        }
    }

    fun updateBooking(booking: BookingDetailsDataClass) {
        collection.document(booking.slotId)
            .set(
                booking.toFireStoreMap()
                    .plus("status" to booking.status.name)
                    .plus("customUserId" to booking.customUserId)
            )
            .addOnSuccessListener {
                sendNotification(
                    title = "✅ Booking Updated",
                    message = "Your booking for slot ${booking.slotId} was updated.",
                    type = "booking_updated",
                    userId = booking.customUserId
                )
            }
    }

    fun deleteBookings(id: String) {
        collection.document(id).delete()
    }

    fun fetchZoneUsage(onResult: (Map<String, Int>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val zoneCounts = snapshot?.documents
                ?.mapNotNull { it.getString("zone") }
                ?.groupingBy { it }
                ?.eachCount()
                ?: emptyMap()
            onResult(zoneCounts)
        }
    }

    fun fetchHeatmapData(onResult: (Map<Pair<Int, Int>, Int>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val counts = snapshot?.documents
                ?.mapNotNull { doc ->
                    val timestamp =
                        doc.getTimestamp("bookingStartTime")?.toDate() ?: return@mapNotNull null
                    val cal = Calendar.getInstance().apply { time = timestamp }

                    val rawDay = cal.get(Calendar.DAY_OF_WEEK)
                    val dayIndex = (rawDay + 5) % 7
                    val hour = cal.get(Calendar.HOUR_OF_DAY)
                    val hourBucket = (hour / 4) * 4
                    Pair(dayIndex, hourBucket)
                }
                ?.groupingBy { it }
                ?.eachCount()
                ?: emptyMap()

            onResult(counts)
        }
    }

    fun fetchDailyUsage(onResult: (Map<String, Int>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val counts = snapshot?.documents
                ?.mapNotNull { doc ->
                    val timestamp =
                        doc.getTimestamp("createdAt")?.toDate() ?: return@mapNotNull null
                    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                    sdf.format(timestamp)
                }
                ?.groupingBy { it }
                ?.eachCount()
                ?: emptyMap()

            val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val finalMap = allDays.associateWith { counts[it] ?: 0 }
            onResult(finalMap)
        }
    }

    fun fetchTopZones(onResult: (List<Pair<String, Int>>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val zoneCounts = snapshot?.documents
                ?.mapNotNull { it.getString("zone") }
                ?.groupingBy { it }
                ?.eachCount()
                ?.toList()
                ?.sortedByDescending { it.second }
                ?: emptyList()
            onResult(zoneCounts.take(3))
        }
    }

    fun fetchTopUser(onResult: (String?) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val userCounts = snapshot?.documents
                ?.mapNotNull { it.getString("userName") }
                ?.groupingBy { it }
                ?.eachCount()
                ?.maxByOrNull { it.value }
            onResult(userCounts?.key)
        }
    }

    private fun sendNotification(
        title: String,
        message: String,
        type: String,
        userId: String?
    ) {
        val notification = NotificationDataClass(
            title = title,
            message = message,
            type = type,
            userId = userId,
            isRead = false
        )
        val docRef = notificationsCollection.document()
        docRef.set(notification.copy(id = docRef.id))

        if (userId != null) {
            sendPushToOneSignal(userId, title, message)
        } else {
            sendPushToOneSignal(null, title, message)
        }
    }

    private fun sendPushToOneSignal(userId: String?, title: String, message: String) {
        val apiKey = context.getString(R.string.onesignal_api_key)
        val url = "https://onesignal.com/api/v1/notifications"

        val json = JSONObject().apply {
            put("app_id", "531eda43-b91a-4e09-b931-0bd569b034e9")

            if (userId != null) {
                put("include_external_user_ids", JSONArray().put(userId))
            } else {
                put("included_segments", JSONArray().put("All"))
            }

            put("headings", JSONObject().put("en", title))
            put("contents", JSONObject().put("en", message))

            // ADD THESE LINES FOR HIGH PRIORITY POP-UP NOTIFICATIONS
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