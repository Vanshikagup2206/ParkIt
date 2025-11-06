package com.vanshika.parkit.admin.screen.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vanshika.parkit.admin.navigation.NavRoutes
import com.vanshika.parkit.admin.screen.analytics.components.DailyUsageLineChart
import com.vanshika.parkit.admin.screen.analytics.components.TopParkedZonesCard
import com.vanshika.parkit.admin.screen.analytics.components.UsageHeatmap
import com.vanshika.parkit.admin.screen.analytics.components.UserBehaviorCard
import com.vanshika.parkit.admin.screen.analytics.components.ZoneUsagePieChart
import com.vanshika.parkit.admin.viewmodel.BookingViewModel

@Composable
fun AdminAnalyticsScreen(
    navController: NavHostController,
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        bookingViewModel.loadZoneUsage()
        bookingViewModel.loadHeatmapData()
        bookingViewModel.loadDailyUsage()
        bookingViewModel.loadTopZones()
        bookingViewModel.loadTopUser()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Analytics",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Analytics Dashboard",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Parking insights & statistics",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Zone Usage Pie Chart
            ZoneUsagePieChart(
                viewModel = bookingViewModel,
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(NavRoutes.ZoneUsageDetail.route) }
            )

            // Usage Heatmap
            UsageHeatmap(
                viewModel = bookingViewModel,
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(NavRoutes.HeatmapDetail.route) }
            )

            // Daily Usage Line Chart
            DailyUsageLineChart(
                viewModel = bookingViewModel,
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(NavRoutes.DailyUsageDetail.route) }
            )

            // Bottom Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TopParkedZonesCard(
                    viewModel = bookingViewModel,
                    modifier = Modifier.weight(1f)
                )
                UserBehaviorCard(
                    viewModel = bookingViewModel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}