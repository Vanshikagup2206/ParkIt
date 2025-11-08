package com.vanshika.parkit.admin.screen.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ZoneUsagePieChart(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val usage by viewModel.zoneUsage
    val total = usage.values.sum().takeIf { it > 0 } ?: 1

    val colors = listOf(
        Color(0xFF4285F4), Color(0xFF0F9D58), Color(0xFFF4B400), Color(0xFFDB4437),
        Color(0xFFAB47BC), Color(0xFF26C6DA), Color(0xFFEF5350), Color(0xFF8D6E63),
        Color(0xFF66BB6A), Color(0xFFFF7043), Color(0xFF5C6BC0), Color(0xFF9CCC65),
        Color(0xFFFFCA28), Color(0xFF26A69A), Color(0xFFEC407A)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = "Zone Usage",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Zone Usage Distribution",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Inner Card (Chart + Legend)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Zone Usage", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Pie Chart
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                var startAngle = 0f
                                usage.entries.forEachIndexed { index, entry ->
                                    val sweep = (entry.value.toFloat() / total) * 360f
                                    val percentage = (entry.value * 100f / total).toInt()

                                    // Draw slice
                                    drawArc(
                                        color = colors[index % colors.size],
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = true,
                                        size = Size(size.width, size.height)
                                    )

                                    // Label inside slice
                                    val angleRad = Math.toRadians((startAngle + sweep / 2).toDouble())
                                    val radius = size.minDimension / 3
                                    val textX = (size.center.x + radius * cos(angleRad)).toFloat()
                                    val textY = (size.center.y + radius * sin(angleRad)).toFloat()

                                    drawContext.canvas.nativeCanvas.apply {
                                        drawText(
                                            "$percentage%",
                                            textX,
                                            textY,
                                            android.graphics.Paint().apply {
                                                textSize = 32f
                                                color = android.graphics.Color.WHITE
                                                textAlign = android.graphics.Paint.Align.CENTER
                                            }
                                        )
                                    }

                                    startAngle += sweep
                                }
                            }
                        }

                        // Legend
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(220.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            usage.entries.forEachIndexed { index, entry ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(colors[index % colors.size])
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "${entry.key}: ${entry.value}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}