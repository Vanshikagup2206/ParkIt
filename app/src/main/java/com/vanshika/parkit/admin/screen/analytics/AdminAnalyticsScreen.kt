package com.vanshika.parkit.admin.screen.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
    // Load data once when screen opens
    LaunchedEffect(Unit) {
        bookingViewModel.loadZoneUsage()
        bookingViewModel.loadHeatmapData()
        bookingViewModel.loadDailyUsage()
        bookingViewModel.loadTopZones()
        bookingViewModel.loadTopUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 🔹 Pie chart full width
        ZoneUsagePieChart(
            viewModel = bookingViewModel,
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate(NavRoutes.ZoneUsageDetail.route) }
        )

        // 🔹 Heatmap full width
        UsageHeatmap(
            viewModel = bookingViewModel,
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate(NavRoutes.HeatmapDetail.route) }
        )

        // 🔹 Daily usage full width
        DailyUsageLineChart(
            viewModel = bookingViewModel,
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.navigate(NavRoutes.DailyUsageDetail.route) }
        )

        // 🔹 Last row with 2 cards side by side
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