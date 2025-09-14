import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun ZoneUsageDetailsScreen(
    viewModel: BookingViewModel = hiltViewModel()
) {
    val zoneBookings by viewModel.zoneBookings.collectAsState()

    // Trigger fetch
    LaunchedEffect(Unit) {
        viewModel.fetchZoneBookings()
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val zones = ('A'..'O').map { "Zone $it" } // A to O

    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Zone Usage",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            zones.forEachIndexed { index, zone ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = zone,
                            color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        val currentZone = zones[selectedTabIndex].removePrefix("Zone ").trim()
        val bookings = zoneBookings[currentZone] ?: emptyList()

        if (bookings.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bookings found for Zone $currentZone")
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookings) { booking ->
                    BookingCard(booking)
                }
            }
        }
    }
}