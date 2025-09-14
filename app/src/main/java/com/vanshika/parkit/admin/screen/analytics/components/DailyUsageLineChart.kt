package com.vanshika.parkit.admin.screen.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import kotlin.math.roundToInt

@Composable
fun DailyUsageLineChart(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val usage by viewModel.dailyUsage
    val days = usage.keys.toList()
    val values = usage.values.toList()
    val maxY = (values.maxOrNull() ?: 1).toFloat()

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val lineColor = Color.Blue // 🔹 Always Blue Line
    val gridColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
    val textColor = MaterialTheme.colorScheme.onBackground // 🔹 Auto black/white

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Daily Usage", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (days.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp) // fix height for labels + chart
                ) {
                    // Y-axis labels
                    Column(
                        modifier = Modifier
                            .width(32.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val step = (maxY / 4).coerceAtLeast(1f)
                        for (i in 4 downTo 0) {
                            Text((i * step).toInt().toString(), fontSize = 12.sp)
                        }
                    }

                    // Chart
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .pointerInput(values) {
                                detectTapGestures { offset ->
                                    val xStep = size.width / (days.size - 1).coerceAtLeast(1)
                                    selectedIndex = (offset.x / xStep)
                                        .roundToInt()
                                        .coerceIn(0, values.lastIndex)
                                }
                            }
                    ) {
                        Canvas(Modifier.matchParentSize()) {
                            val xStep = size.width / (days.size - 1).coerceAtLeast(1)
                            val yRatio = size.height / maxY

                            for (i in 0..4) {
                                val y = size.height - (i * size.height / 4f) // evenly divide height
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1f
                                )
                            }

                            val path = Path()
                            values.forEachIndexed { index, value ->
                                val x = index * xStep
                                val y = size.height - (value * yRatio)
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, color = lineColor, style = Stroke(width = 4f))

                            values.forEachIndexed { index, value ->
                                val x = index * xStep
                                val y = size.height - (value * yRatio)

                                val radius = if (selectedIndex == index) 10f else 6f

                                drawCircle(
                                    color = lineColor,
                                    radius = radius,
                                    center = Offset(x, y)
                                )

                                if (selectedIndex == index) {
                                    drawContext.canvas.nativeCanvas.drawText(
                                        value.toString(),
                                        x,
                                        y - 20,
                                        android.graphics.Paint().apply {
                                            textSize = 36f
                                            color = textColor.toArgb()
                                            textAlign = android.graphics.Paint.Align.CENTER
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // X-axis labels
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp)
                ) {
                    days.forEach { day -> Text(day, fontSize = 12.sp) }
                }
            } else {
                Text("No data available")
            }
        }
    }
}