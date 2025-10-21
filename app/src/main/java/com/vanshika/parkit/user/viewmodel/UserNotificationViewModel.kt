package com.vanshika.parkit.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserNotificationViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<NotificationDataClass>>(emptyList())
    val notifications: StateFlow<List<NotificationDataClass>> = _notifications

    fun observeUserNotifications(userId: String) {
        firestore.collection("notifications")
            .addSnapshotListener { snapshot, _ ->
                val allNotifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationDataClass::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                val userNotifications = allNotifications
                    .filter { it.userId == userId || it.userId == null }
                    .sortedByDescending { it.timestamp }
                    .map { notification ->
                        notification
                    }

                _notifications.value = userNotifications
            }
    }

    val unreadCount: StateFlow<Int> = _notifications
        .map { list ->
            list.count { !it.isRead }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun markAllAsRead() {
        val currentNotifications = _notifications.value

        // Optimistic local update
        _notifications.value = currentNotifications.map { it.copy(isRead = true) }

        // Fire store update in background
        currentNotifications.forEach { notification ->
            if (!notification.isRead) {
                firestore.collection("notifications")
                    .document(notification.id)
                    .update("isRead", true)
            }
        }
    }
}