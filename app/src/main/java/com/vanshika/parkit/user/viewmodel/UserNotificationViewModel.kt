package com.vanshika.parkit.user.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.parkit.admin.data.model.NotificationDataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

                val userNotifications = allNotifications.filter { it.userId == userId || it.userId == null }
                _notifications.value = userNotifications.sortedByDescending { it.timestamp }
            }
    }
}