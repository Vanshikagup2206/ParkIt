package com.vanshika.parkit.admin.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vanshika.parkit.admin.data.model.BookingDetailsDataClass
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import com.vanshika.parkit.admin.data.model.toFireStoreMap
import com.vanshika.parkit.admin.screen.home.SlotStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor() {
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
                val notification = NotificationDataClass(
                    title = "Booking Confirmed",
                    message = "Your booking for slot ${booking.slotId} is confirmed!",
                    type = "booking_confirmed",
                    userId = booking.customUserId
                )
                notificationsCollection.add(notification)
            }
    }

    fun fetchBookings(onBookingChanged: (List<Triple<String, String, String>>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            val bookings = snapshot?.documents?.mapNotNull { document ->
                val id = document.id
                val userName = document.getString("userName") ?: ""
                val status = document.getString("status") ?: SlotStatus.AVAILABLE.name
                Triple(id, userName, status)
            } ?: emptyList()

            onBookingChanged(bookings)
        }
    }

    fun fetchUpcomingBookings(userId: String, onBookingChanged: (List<BookingDetailsDataClass>) -> Unit) {
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

    fun fetchBookingHistory(userId: String, onBookingChanged: (List<BookingDetailsDataClass>) -> Unit) {
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
                val notification = NotificationDataClass(
                    title = "Booking Updated",
                    message = "Your booking for slot ${booking.slotId} was updated.",
                    type = "booking_updated",
                    userId = booking.customUserId
                )
                notificationsCollection.add(notification)
            }
    }

    fun updateSlotStatus(slotId: String, newStatus: SlotStatus, userId: String? = null) {
        val docRef = collection.document(slotId)
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                docRef.update("status", newStatus.name)
            } else {
                docRef.set(mapOf("slotId" to slotId, "status" to newStatus.name))
            }

            when (newStatus) {
                SlotStatus.AVAILABLE -> userId?.let {
                    sendNotification(
                        title = "Booking Auto-Cancelled",
                        message = "Your booking for slot $slotId expired and was cancelled.",
                        type = "booking_cancelled",
                        userId = it
                    )
                }

                SlotStatus.MAINTENANCE -> sendNotification(
                    title = "Slot Under Maintenance",
                    message = "Slot $slotId is under maintenance. Sorry for inconvenience.",
                    type = "maintenance",
                    userId = null
                )

                SlotStatus.BOOKED -> userId?.let {
                    sendNotification(
                        title = "Booking Confirmed",
                        message = "Your booking for slot $slotId is confirmed.",
                        type = "booking_confirmed",
                        userId = it
                    )
                }

                else -> {}
            }
        }
    }

    fun sendMaintenanceNotice(title: String, message: String) {
        sendNotification(title, message, type = "maintenance", userId = null)
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
            userId = userId
        )
        notificationsCollection.add(notification)
    }
}