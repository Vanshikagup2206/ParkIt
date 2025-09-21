package com.vanshika.parkit.admin.screen.issues

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vanshika.parkit.user.viewmodel.IssuesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDetail(
    issueId: String,
    onBack: () -> Unit,
    viewModel: IssuesViewModel = hiltViewModel()
) {
    val issue = viewModel.getIssueById(issueId) // helper function in ViewModel

    if (issue != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Issue Details")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.deleteIssue(issue.issueId)
                            onBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Issue")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Zone: ${issue.zoneName}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Slot ID: ${issue.slotId}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Reported By: ${issue.reportedBy}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Reported At: ${issue.reportedAt.toReadableDate()}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Issue Type:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = issue.issueType,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (issue.issueType == "Other" && issue.customDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Custom Description:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = issue.customDescription,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.titleMedium
                )
                StatusChip(status = issue.status)

                Spacer(modifier = Modifier.height(12.dp))

                // 🔹 Admin Controls
                Text(
                    text = "Update Status:",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.updateIssueStatus(issue.issueId, "Pending") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC80))
                    ) {
                        Text(
                            text = "Pending",
                            color = Color(0xFF424242)
                        )
                    }

                    Button(
                        onClick = { viewModel.updateIssueStatus(issue.issueId, "In Progress") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A65))
                    ) {
                        Text(
                            text = "In Progress",
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { viewModel.updateIssueStatus(issue.issueId, "Resolved") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                    ) {
                        Text(
                            text = "Resolved",
                            color = Color.White
                        )
                    }
                }
            }
        }
    } else {
        Text(
            "Issue not found",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun Long.toReadableDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}