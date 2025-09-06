package com.vanshika.parkit.admin.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.vanshika.parkit.admin.screen.home.SlotStatus

data class BookingDetailsDataClass(
    val vehicleNumber: String? = null,
    val vehicleType: String = "",
    val customUserId: String = "",
    val userName: String = "",
    val slotId: String = "",
    val zone: String = "",
    val contactNo: String = "",
    val priorityTag: String = "",
    val status: SlotStatus = SlotStatus.AVAILABLE,
    val bookedBy: String? = null,
    val date: Timestamp ?= null,
    val bookingStartTime: Timestamp? = null,
    val bookingEndTime: Timestamp? = null,
    val createdAt: Timestamp? = null
)

//Helper to store data in fireStore
internal fun BookingDetailsDataClass.toFireStoreMap() : Map<String, Any?> = mapOf(
    "vehicleNumber" to vehicleNumber,
    "vehicleType" to vehicleType,
    "customUserId" to customUserId,
    "userName" to userName,
    "slotId" to slotId,
    "zone" to zone,
    "contactNo" to contactNo,
    "priorityTag" to priorityTag,
    "status" to status.name,
    "bookedBy" to bookedBy,
    "date" to date,
    "bookingStartTime" to bookingStartTime,
    "bookingEndTime" to bookingEndTime,
    "createdAt" to (createdAt ?: FieldValue.serverTimestamp())
)