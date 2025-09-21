package com.vanshika.parkit.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserNotificationViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<NotificationDataClass>>(emptyList())
    val notifications: StateFlow<List<NotificationDataClass>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    fun observeUserNotifications(userId: String) {
        firestore.collection("notifications")
            .addSnapshotListener { snapshot, _ ->
                val allNotifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationDataClass::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                val userNotifications = allNotifications.filter { it.userId == userId || it.userId == null }
                    .sortedByDescending { it.timestamp }

                _notifications.value = userNotifications
                _unreadCount.value = userNotifications.count { !it.isRead }
            }
    }

    fun markAsRead(notificationId: String) {
        firestore.collection("notifications")
            .document(notificationId)
            .update("isRead", true)
    }
}