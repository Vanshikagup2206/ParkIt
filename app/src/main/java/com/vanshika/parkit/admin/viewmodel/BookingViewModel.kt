package com.vanshika.parkit.admin.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vanshika.parkit.admin.data.model.BookingDetailsDataClass
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import com.vanshika.parkit.admin.data.repository.BookingRepository
import com.vanshika.parkit.admin.screen.home.ParkingSlotData
import com.vanshika.parkit.admin.screen.home.SlotStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    var slotsMap = mutableStateMapOf<String, ParkingSlotData>()
    val slotPositions = mutableStateMapOf<String, Offset>()

    init {
        val blocks = listOf(
            "A" to 4, "B" to 4, "C" to 4, "D" to 4, "E" to 3,
            "F" to 3, "G" to 2, "H" to 2, "I" to 2, "J" to 2,
            "K" to 2, "L" to 3, "M" to 4, "N" to 4, "O" to 4
        )
        blocks.forEach { (block, count) ->
            for (i in count downTo 1) {
                slotsMap["$block$i"] = ParkingSlotData(id = "$block$i", zoneName = block)
            }
        }

        bookingRepository.fetchBookings { updatedBookings ->
            updatedBookings.forEach { (id, _, status) ->
                slotsMap[id]?.status?.value = SlotStatus.valueOf(status)
            }
        }
    }

    fun addBooking(booking: BookingDetailsDataClass) = bookingRepository.addBookings(booking)
    fun addBookingHistory(booking: BookingDetailsDataClass) = bookingRepository.addBookingHistory(booking)

    fun updateSlotStatus(slotId: String, newStatus: SlotStatus) {
        bookingRepository.updateSlotStatus(slotId, newStatus)
        slotsMap[slotId]?.status?.value = newStatus
    }

    fun updateBooking(booking: BookingDetailsDataClass) = bookingRepository.updateBooking(booking)
    fun updateSlotPosition(slotId: String, position: Offset) { slotPositions[slotId] = position }
    fun deleteBookings(id: String) = bookingRepository.deleteBookings(id)

    private val _allBookings = mutableStateOf<List<BookingDetailsDataClass>>(emptyList())
    val allBookings: State<List<BookingDetailsDataClass>> = _allBookings

    fun loadAllBookings() {
        bookingRepository.fetchAllUsers { bookings ->
            _allBookings.value = bookings
        }
    }

    private val _allBookingHistory = mutableStateOf<List<BookingDetailsDataClass>>(emptyList())
    val allBookingHistory: State<List<BookingDetailsDataClass>> = _allBookingHistory

    fun loadAllBookingHistory() {
        bookingRepository.fetchAllBookingHistory { bookings ->
            _allBookingHistory.value = bookings
        }
    }

    fun getBookingBySlotId(slotId: String): BookingDetailsDataClass? {
        return _allBookings.value.find { it.slotId == slotId }
    }

    private val _userBookings = mutableStateOf<List<BookingDetailsDataClass>>(emptyList())
    val userBookings: State<List<BookingDetailsDataClass>> = _userBookings

    private val _userBookingHistory = mutableStateOf<List<BookingDetailsDataClass>>(emptyList())
    val userBookingHistory: State<List<BookingDetailsDataClass>> = _userBookingHistory

    fun fetchUpcomingBookings(customUserId: String) {
        bookingRepository.fetchUpcomingBookings(customUserId) { updatedBookings ->
            _userBookings.value = updatedBookings
        }
    }

    fun fetchBookingHistory(customUserId: String) {
        bookingRepository.fetchBookingHistory(customUserId) { updatedBookings ->
            _userBookingHistory.value = updatedBookings
        }
    }

    fun sendNotificationToUser(notification: NotificationDataClass) {
        if (notification.userId == null) {
            sendNotificationToAllUsers(notification)
        } else {
            sendNotificationToSingleUser(notification.userId, notification)
        }
    }

    private fun sendNotificationToSingleUser(userId: String, notification: NotificationDataClass) {
        val db = Firebase.firestore
        db.collection("users")
            .document(userId)
            .collection("notifications")
            .add(notification)
    }

    private fun sendNotificationToAllUsers(notification: NotificationDataClass) {
        val db = Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val userId = document.id
                    db.collection("users")
                        .document(userId)
                        .collection("notifications")
                        .add(notification)
                }
            }
    }

    private val _zoneUsage = mutableStateOf<Map<String, Int>>(emptyMap())
    val zoneUsage: State<Map<String, Int>> = _zoneUsage
    fun loadZoneUsage() { bookingRepository.fetchZoneUsage { usage -> _zoneUsage.value = usage } }

    private val _heatmapData = mutableStateOf<Map<Pair<Int, Int>, Int>>(emptyMap())
    val heatmapData: State<Map<Pair<Int, Int>, Int>> = _heatmapData
    fun loadHeatmapData() { bookingRepository.fetchHeatmapData { data -> _heatmapData.value = data } }

    private val _dailyUsage = mutableStateOf<Map<String, Int>>(emptyMap())
    val dailyUsage: State<Map<String, Int>> = _dailyUsage
    fun loadDailyUsage() { bookingRepository.fetchDailyUsage { data -> _dailyUsage.value = data } }

    private val _topZones = mutableStateOf<List<Pair<String, Int>>>(emptyList())
    val topZones: State<List<Pair<String, Int>>> = _topZones
    fun loadTopZones() { bookingRepository.fetchTopZones { zones -> _topZones.value = zones } }

    private val _topUser = mutableStateOf<String?>(null)
    val topUser: State<String?> = _topUser
    fun loadTopUser() { bookingRepository.fetchTopUser { user -> _topUser.value = user } }

    private val _zoneBookings = MutableStateFlow<Map<String, List<BookingDetailsDataClass>>>(emptyMap())
    val zoneBookings: StateFlow<Map<String, List<BookingDetailsDataClass>>> = _zoneBookings

    fun fetchZoneBookings() {
        bookingRepository.fetchAllUsers { bookings ->
            val grouped = bookings.groupBy { it.zone ?: "Unknown" }
            _zoneBookings.value = grouped
        }
    }
}