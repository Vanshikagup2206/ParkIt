package com.vanshika.parkit.admin.screen.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanshika.parkit.admin.viewmodel.BookingViewModel

@Composable
fun UsageHeatmap(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val heatmap by viewModel.heatmapData

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val hours = (0 until 24 step 4) // 4 hr step
    val maxUsage = (heatmap.values.maxOrNull() ?: 1).toFloat()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Heatmap", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Each row = Day + heatmap cells
            Column {
                days.forEachIndexed { dayIndex, day ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Day label
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            modifier = Modifier.width(32.dp)
                        )

                        // Row of cells for this day
                        hours.forEach { hour ->
                            val count = heatmap[Pair(dayIndex, hour)] ?: 0
                            val intensity = (count / maxUsage).coerceIn(0f, 1f)

                            val baseColors = listOf(
                                Color(0xFFBBDEFB), // light blue (low usage)
                                Color(0xFF2196F3), // medium blue
                                Color(0xFF0D47A1)  // dark blue (high usage)
                            )

                            // Map intensity (0f..1f) to color from gradient
                            val usageColor = if (count == 0) {
                                Color.LightGray
                            } else {
                                androidx.compose.ui.graphics.lerp(
                                    baseColors[0],
                                    baseColors[2],
                                    intensity
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(1.dp)
                                    .background(usageColor)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Hour labels
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp) // align with grid
            ) {
                hours.forEach { hour ->
                    Text("${hour}:00", fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}