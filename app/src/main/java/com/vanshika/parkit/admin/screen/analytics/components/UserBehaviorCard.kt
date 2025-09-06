package com.vanshika.parkit.admin.screen.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanshika.parkit.admin.viewmodel.BookingViewModel

@Composable
fun UserBehaviorCard(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val topUser by viewModel.topUser

    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("User Behavior", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(topUser ?: "No top user", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}