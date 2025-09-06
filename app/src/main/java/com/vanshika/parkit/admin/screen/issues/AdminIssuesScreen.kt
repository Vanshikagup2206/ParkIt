package com.vanshika.parkit.admin.screen.issues

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vanshika.parkit.user.data.model.IssuesDataClass
import com.vanshika.parkit.user.viewmodel.IssuesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminIssuesScreen(
    onIssueClick: (String) -> Unit,
    viewModel: IssuesViewModel = hiltViewModel()
) {
    val allIssues = viewModel.allIssues

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "In Progress", "Resolved")

    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Latest First") }
    val sortOptions = listOf("Latest First", "Oldest First")

    val filteredIssues = allIssues
        .filter { issue ->
            (selectedFilter == "All" || issue.status.equals(selectedFilter, ignoreCase = true)) &&
                    (issue.zoneName.contains(searchQuery, ignoreCase = true) ||
                            issue.issueType.contains(searchQuery, ignoreCase = true) ||
                            issue.reportedBy.contains(searchQuery, ignoreCase = true))
        }
        .sortedBy { it.reportedAt }
        .let { list ->
            if (sortOption == "Latest First") list.reversed() else list
        }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // 🔹 Filter Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Search + Sort Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search issues...") },
                modifier = Modifier.weight(1f)
            )

            SortDropdown(
                sortOption = sortOption,
                onSortChange = { sortOption = it },
                sortOptions = sortOptions
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Issues List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredIssues) { issue ->
                IssueCard(
                    issue = issue,
                    onClick = { onIssueClick(issue.issueId) },
                    onDelete = { viewModel.deleteIssue(issue.issueId) }
                )
            }
        }
    }
}

@Composable
fun IssueCard(
    issue: IssuesDataClass,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Slot: ${issue.slotId}", style = MaterialTheme.typography.titleMedium)
                Text("Zone: ${issue.zoneName}", style = MaterialTheme.typography.titleMedium)
                Text("Issue: ${issue.issueType}", style = MaterialTheme.typography.bodyMedium)
                Text("Reported by: ${issue.reportedBy}", style = MaterialTheme.typography.bodySmall)

                // Convert timestamp to a readable date
                val formattedDate = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(issue.reportedAt))
                Text("Date: $formattedDate", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(status = issue.status)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Issue")
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bgColor, textColor) = when (status) {
        "Pending" -> Color(0xFFFFC107) to Color.Black
        "In Progress" -> Color(0xFF2196F3) to Color.White
        "Resolved" -> Color(0xFF4CAF50) to Color.White
        else -> Color.Gray to Color.White
    }

    Box(
        modifier = Modifier
            .background(bgColor, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(status, color = textColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SortDropdown(
    sortOption: String,
    onSortChange: (String) -> Unit,
    sortOptions: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Sort Options")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sortOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}