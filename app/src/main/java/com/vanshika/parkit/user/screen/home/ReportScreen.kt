package com.vanshika.parkit.user.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vanshika.parkit.user.data.model.IssuesDataClass
import com.vanshika.parkit.user.viewmodel.IssuesViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    slotId: String = "",
    zoneName: String = "",
    userId: String = "",
    onNavigationUp: () -> Unit = {},
    viewModel: IssuesViewModel = hiltViewModel()
) {
    val issues = listOf(
        "Someone else has parked in my slot",
        "Slot is occupied without booking",
        "Parking slot is damaged",
        "Lights not working",
        "Water leakage / flooding",
        "Improper marking of slot",
        "Obstruction blocking entry",
        "Security / CCTV issue",
        "Cleanliness issue",
        "Other"
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var customIssue by remember { mutableStateOf("") }

    // 👇 track selected issue
    var selectedIssue by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Issue - $slotId ($zoneName)") },
                navigationIcon = {
                    IconButton(onClick = onNavigationUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (selectedIssue != null) {
                Button(
                    onClick = {
                        if (selectedIssue == "Other") {
                            showBottomSheet = true
                            coroutineScope.launch { sheetState.show() }
                        } else {
                            val newIssue = IssuesDataClass(
                                issueId = UUID.randomUUID().toString(),
                                slotId = slotId,
                                zoneName = zoneName,
                                issueType = selectedIssue!!,
                                reportedBy = userId
                            )
                            viewModel.addIssue(newIssue)
                            onNavigationUp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Select the issue you faced:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            issues.forEach { issue ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedIssue = issue },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = issue,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Checkbox(
                            checked = selectedIssue == issue,
                            onCheckedChange = { selectedIssue = issue }
                        )
                    }
                }
            }
        }
    }

    // ---------------- Bottom Sheet ----------------
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Describe the issue", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customIssue,
                    onValueChange = { customIssue = it },
                    label = { Text("Enter your issue") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (customIssue.isNotBlank()) {
                            val newIssue = IssuesDataClass(
                                issueId = UUID.randomUUID().toString(),
                                slotId = slotId,
                                zoneName = zoneName,
                                issueType = "Other",
                                customDescription = customIssue,
                                reportedBy = userId
                            )
                            viewModel.addIssue(newIssue)
                            showBottomSheet = false
                            coroutineScope.launch { sheetState.hide() }
                            onNavigationUp()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit")
                }
            }
        }
    }
}