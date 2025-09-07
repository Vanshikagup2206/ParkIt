package com.vanshika.parkit.admin.data.model

data class NotificationDataClass(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "", // booking_confirmed, booking_cancelled, reminder, maintenance
    val userId: String? = null,
    val isRead: Boolean = false
)