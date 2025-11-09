package com.vanshika.parkit.user.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.vanshika.parkit.admin.screen.home.SlotStatus

data class ReservationRequestDataClass(
    val id: String = "",
    val userId: String = "",
    val requestStatus: String = "Pending",
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
    val date: Timestamp?= null,
    val bookingStartTime: Timestamp? = null,
    val bookingEndTime: Timestamp? = null,
    val createdAt: Timestamp? = null
)

internal fun ReservationRequestDataClass.toFireStoreMap() : Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
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
    "requestStatus" to requestStatus,
    "bookingStartTime" to bookingStartTime,
    "bookingEndTime" to bookingEndTime,
    "createdAt" to (createdAt ?: FieldValue.serverTimestamp())
)