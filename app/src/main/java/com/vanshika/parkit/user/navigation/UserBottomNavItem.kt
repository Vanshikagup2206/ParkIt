package com.vanshika.parkit.user.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class UserBottomNavItem(
    val route: String,
    val icon: ImageVector,
){
    object Home: UserBottomNavItem("home_screen", Icons.Default.Home)
    object MyBookings: UserBottomNavItem("my_booking_screen", Icons.Default.DateRange)
    object Notification: UserBottomNavItem("notification_screen", Icons.Default.Notifications)
    object Profile: UserBottomNavItem("profile", Icons.Default.Person)
}
