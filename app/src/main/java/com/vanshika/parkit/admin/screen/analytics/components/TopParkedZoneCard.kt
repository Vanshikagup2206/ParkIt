package com.vanshika.parkit.admin.screen.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanshika.parkit.admin.viewmodel.BookingViewModel

@Composable
fun TopParkedZonesCard(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val zones by viewModel.topZones
    val maxCount = zones.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1

    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Top Parked Zones", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (zones.isNotEmpty()) {
                zones.forEach { (zone, count) ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(zone, fontSize = 14.sp)
                            Text("$count", fontSize = 14.sp)
                        }

                        // 🔹 Progress bar
                        LinearProgressIndicator(
                            progress = count / maxCount.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF4285F4),
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }
            } else {
                Text("No data")
            }
        }
    }
}
