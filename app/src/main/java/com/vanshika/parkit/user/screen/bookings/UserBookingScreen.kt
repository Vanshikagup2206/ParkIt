package com.vanshika.parkit.user.screen.bookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vanshika.parkit.admin.data.model.BookingDetailsDataClass
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import com.vanshika.parkit.ui.theme.ThemePreference
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UserBookingScreen(
    authViewModel: AuthenticationViewModel,
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    val upcomingBookings by bookingViewModel.userBookings
    val bookingHistory by bookingViewModel.userBookingHistory

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "History")

    val customUserId by authViewModel.customUserId.collectAsStateWithLifecycle()

    LaunchedEffect(customUserId) {
        if (customUserId != null) {
            bookingViewModel.fetchUpcomingBookings(customUserId!!)
            bookingViewModel.fetchBookingHistory(customUserId!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Bookings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 🔹 Tab Row for "Upcoming" and "History"
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Content based on selected tab
        val currentBookings = if (selectedTabIndex == 0) upcomingBookings else bookingHistory

        if (currentBookings.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (selectedTabIndex == 0) "You have no upcoming bookings." else "You have no booking history.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(currentBookings) { booking ->
                    BookingCard(booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingDetailsDataClass) {
    val startTime = booking.bookingStartTime?.toDate()
        ?.let { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it) } ?: "-"
    val endTime = booking.bookingEndTime?.toDate()
        ?.let { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it) } ?: "-"
    val date = booking.date?.toDate()
        ?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) } ?: "-"

    val context = LocalContext.current

    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    val cardColor = if (isDarkTheme) {
        Color(0xFF1565C0) // darker blue for dark theme
    } else {
        Color(0xFFD0E8FF) // light blue for light theme
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booking ID: ${booking.slotId}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = booking.status.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Zone: ${booking.zone}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Vehicle: ${booking.vehicleType}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Vehicle No: ${booking.vehicleNumber}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start: $startTime",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "End: $endTime",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: $date",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Contact: ${booking.contactNo}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}