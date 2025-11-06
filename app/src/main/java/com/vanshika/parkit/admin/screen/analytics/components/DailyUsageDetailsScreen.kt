package com.vanshika.parkit.admin.screen.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.user.screen.bookings.BookingCard
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyUsageDetailsScreen(
    viewModel: BookingViewModel = hiltViewModel()
) {
    val allBookings by viewModel.allBookings
    LaunchedEffect(Unit) { viewModel.loadAllBookings() }

    var selectedDay by remember { mutableStateOf(0) }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    if (dragAmount > 50 && selectedDay > 0) {
                        selectedDay -= 1 // Swipe right → previous day
                    } else if (dragAmount < -50 && selectedDay < days.lastIndex) {
                        selectedDay += 1 // Swipe left → next day
                    }
                }
            }
    ) {
        Column(Modifier.fillMaxSize()) {

            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Daily Usage",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Daily Usage Analysis",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "View bookings by day of week",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs without boxes
            ScrollableTabRow(
                selectedTabIndex = selectedDay,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.background,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedDay]),
                        height = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                days.forEachIndexed { index, day ->
                    Tab(
                        selected = selectedDay == index,
                        onClick = { selectedDay = index },
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        Text(
                            text = day,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 18.dp),
                            color = if (selectedDay == index)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontWeight = if (selectedDay == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            val dayBookings = allBookings.filter { booking ->
                booking.createdAt?.toDate()?.let {
                    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                    sdf.format(it) == days[selectedDay]
                } ?: false
            }

            if (dayBookings.isEmpty()) {
                // Clean no-bookings message
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "No bookings",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No bookings for ${days[selectedDay]}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dayBookings) { booking ->
                        BookingCard(booking)
                    }
                }
            }
        }
    }
}