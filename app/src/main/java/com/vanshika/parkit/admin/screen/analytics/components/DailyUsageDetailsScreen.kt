package com.vanshika.parkit.admin.screen.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.user.screen.bookings.BookingCard
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DailyUsageDetailsScreen(
    viewModel: BookingViewModel = hiltViewModel()
) {
    val allBookings by viewModel.allBookings

    LaunchedEffect(Unit) {
        viewModel.loadAllBookings()
    }

    var selectedDay by remember { mutableStateOf(0) }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Daily Usage",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedDay,
            edgePadding = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            days.forEachIndexed { index, day ->
                Tab(
                    selected = selectedDay == index,
                    onClick = { selectedDay = index },
                    text = {
                        Text(
                            text = day,
                            color = if (selectedDay == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        // Filter bookings for selected day
        val dayBookings = allBookings.filter { booking ->
            booking.createdAt?.toDate()?.let {
                val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                sdf.format(it) == days[selectedDay]
            } ?: false
        }

        if (dayBookings.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bookings for ${days[selectedDay]}")
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dayBookings) { booking ->
                    BookingCard(booking)
                }
            }
        }
    }
}