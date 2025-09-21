package com.vanshika.parkit.admin.screen.home

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.vanshika.parkit.R
import com.vanshika.parkit.admin.data.model.BookingDetailsDataClass
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.ui.theme.ThemePreference
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPage(
    slotId: String = "",
    zoneName: String = "",
    originalStatus: SlotStatus,
    viewModel: BookingViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var vehicleNo by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var contactNo by remember { mutableStateOf("") }
    var priorityTag by remember { mutableStateOf("") }

    var datePicked by remember { mutableStateOf("") }
    var startTimePicked by remember { mutableStateOf("") }
    var endTimePicked by remember { mutableStateOf("") }

    var vehicleNoError by remember { mutableStateOf("") }
    var vehicleTypeError by remember { mutableStateOf("") }
    var userIdError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var contactNoError by remember { mutableStateOf("") }
    var priorityTagError by remember { mutableStateOf("") }

    var dateError by remember { mutableStateOf("") }
    var startTimeError by remember { mutableStateOf("") }
    var endTimeError by remember { mutableStateOf("") }

    val priorityOptions = listOf("Normal", "Staff", "Student")
    var priorityExpanded by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    val allBookings by viewModel.allBookings
    val currentBooking = remember { mutableStateOf<BookingDetailsDataClass?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAllBookings()
    }

    val vehicleNoSuggestions by remember(vehicleNo, allBookings) {
        derivedStateOf {
            if (vehicleNo.isBlank()) emptyList()
            else allBookings
                .mapNotNull { it -> it.vehicleNumber?.takeIf { it.isNotBlank() } }
                .distinct()
                .filter { it.contains(vehicleNo, ignoreCase = true) }
        }
    }
    var vehicleNoExpanded by remember { mutableStateOf(false) }

    val userIdSuggestions by remember(userId, allBookings) {
        derivedStateOf {
            if (userId.isBlank()) emptyList()
            else allBookings
                .filter { it.customUserId.contains(userId, ignoreCase = true) }
        }
    }
    var userIdExpanded by remember { mutableStateOf(false) }

    val usernameSuggestions by remember(username, allBookings) {
        derivedStateOf {
            if (username.isBlank()) emptyList()
            else allBookings
                .filter { it.userName.contains(username, ignoreCase = true) }
        }
    }
    var usernameExpanded by remember { mutableStateOf(false) }

    // Speech-to-Text Setup
    var activeField by remember { mutableStateOf<String?>(null) }
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            spokenText?.let {
                when (activeField) {
                    "vehicleNo" -> vehicleNo = it
                    "vehicleType" -> vehicleType = it
                    "userId" -> userId = it
                    "username" -> username = it
                    "contactNo" -> contactNo = it
                }
            }
        }
    }

    fun startListeningFor(field: String) {
        activeField = field
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        speechLauncher.launch(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Booking Page",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color.Black else Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                /*** Vehicle Number with suggestions ***/
                ExposedDropdownMenuBox(
                    expanded = vehicleNoExpanded && vehicleNoSuggestions.isNotEmpty(),
                    onExpandedChange = { vehicleNoExpanded = !vehicleNoExpanded }
                ) {
                    OutlinedTextField(
                        value = vehicleNo,
                        onValueChange = {
                            vehicleNo = it
                            vehicleNoError = ""
                            vehicleNoExpanded = true
                        },
                        label = { Text("Vehicle Number") },
                        isError = vehicleNoError.isNotEmpty(),
                        placeholder = { Text("e.g. MH12AB1234") },
                        trailingIcon = {
                            IconButton(onClick = { startListeningFor("vehicleNo") }) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_mic_24),
                                    contentDescription = "Speak Vehicle Number"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = vehicleNoExpanded && vehicleNoSuggestions.isNotEmpty(),
                        onDismissRequest = { vehicleNoExpanded = false },
                        modifier = Modifier.background(
                            color = if (isDarkTheme) Color.Black else Color.White
                        )
                    ) {
                        vehicleNoSuggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = suggestion,
                                        color = if (isDarkTheme) Color.White else Color.Black
                                    )
                                },
                                onClick = {
                                    vehicleNo = suggestion
                                    val user = allBookings.find { it.vehicleNumber == suggestion }
                                    username = user?.userName ?: ""
                                    userId = user?.customUserId ?: ""
                                    contactNo = user?.contactNo ?: ""
                                    vehicleType = user?.vehicleType ?: ""
                                    priorityTag = user?.priorityTag ?: ""
                                    vehicleNoExpanded = false
                                }
                            )
                        }
                    }
                }

                // Vehicle Type
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = {
                        vehicleType = it
                        vehicleTypeError = ""
                    },
                    label = { Text("Vehicle Type") },
                    isError = vehicleTypeError.isNotEmpty(),
                    trailingIcon = {
                        IconButton(onClick = { startListeningFor("vehicleType") }) {
                            Icon(
                                painterResource(id = R.drawable.baseline_mic_24),
                                contentDescription = "Speak Vehicle Type"
                            )
                        }
                    },
                    singleLine = true,
                    placeholder = { Text("Car, Bike, etc.") },
                    modifier = Modifier.fillMaxWidth()
                )

                // User ID
                ExposedDropdownMenuBox(
                    expanded = userIdExpanded && userIdSuggestions.isNotEmpty(),
                    onExpandedChange = { userIdExpanded = !userIdExpanded }
                ) {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = {
                            userId = it
                            userIdError = ""
                            userIdExpanded = true
                        },
                        label = { Text("User ID") },
                        isError = userIdError.isNotEmpty(),
                        trailingIcon = {
                            IconButton(onClick = { startListeningFor("userId") }) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_mic_24),
                                    contentDescription = "Speak User Id"
                                )
                            }
                        },
                        singleLine = true,
                        placeholder = { Text("Enter User ID") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = userIdExpanded && userIdSuggestions.isNotEmpty(),
                        onDismissRequest = { userIdExpanded = false },
                        modifier = Modifier.background(
                            color = if (isDarkTheme) Color.Black else Color.White
                        )
                    ) {
                        userIdSuggestions.forEach { user ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${user.customUserId} - ${user.userName}",
                                        color = if (isDarkTheme) Color.White else Color.Black
                                    )
                                },
                                onClick = {
                                    userId = user.customUserId
                                    username = user.userName
                                    contactNo = user.contactNo
                                    vehicleNo = user.vehicleNumber.toString()
                                    vehicleType = user.vehicleType
                                    priorityTag = user.priorityTag
                                    userIdExpanded = false
                                }
                            )
                        }
                    }
                }

                // Username
                ExposedDropdownMenuBox(
                    expanded = usernameExpanded && usernameSuggestions.isNotEmpty(),
                    onExpandedChange = { usernameExpanded = !usernameExpanded }
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = ""
                            usernameExpanded = true
                        },
                        label = { Text("Username") },
                        isError = usernameError.isNotEmpty(),
                        trailingIcon = {
                            IconButton(onClick = { startListeningFor("username") }) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_mic_24),
                                    contentDescription = "Speak Username"
                                )
                            }
                        },
                        singleLine = true,
                        placeholder = { Text("Enter Customer Name") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = usernameExpanded && usernameSuggestions.isNotEmpty(),
                        onDismissRequest = { usernameExpanded = false },
                        modifier = Modifier.background(
                            color = if (isDarkTheme) Color.Black else Color.White
                        )
                    ) {
                        usernameSuggestions.forEach { user ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${user.userName} - ${user.customUserId}",
                                        color = if (isDarkTheme) Color.White else Color.Black
                                    )
                                },
                                onClick = {
                                    username = user.userName
                                    userId = user.customUserId
                                    contactNo = user.contactNo
                                    vehicleNo = user.vehicleNumber.toString()
                                    vehicleType = user.vehicleType
                                    priorityTag = user.priorityTag
                                    usernameExpanded = false
                                }
                            )
                        }
                    }
                }

                // Slot ID
                OutlinedTextField(
                    value = slotId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Slot Id") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Zone Name
                OutlinedTextField(
                    value = zoneName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Zone Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Contact Number
                OutlinedTextField(
                    value = contactNo,
                    onValueChange = {
                        contactNo = it
                        contactNoError = ""
                    },
                    label = { Text("Contact Number") },
                    isError = contactNoError.isNotEmpty(),
                    trailingIcon = {
                        IconButton(onClick = { startListeningFor("contactNo") }) {
                            Icon(
                                painterResource(id = R.drawable.baseline_mic_24),
                                contentDescription = "Speak Contact Number"
                            )
                        }
                    },
                    supportingText = {
                        if (contactNoError.isNotEmpty()) {
                            Text(text = contactNoError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    placeholder = { Text("Enter Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Tag
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded }
                ) {
                    OutlinedTextField(
                        value = priorityTag,
                        onValueChange = { priorityTagError = "" },
                        readOnly = true,
                        label = { Text("Priority Tag") },
                        isError = priorityTagError.isNotEmpty(),
                        supportingText = {
                            if (priorityTagError.isNotEmpty()) {
                                Text(
                                    text = priorityTagError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        placeholder = { Text("Select Priority") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false },
                        modifier = Modifier.background(
                            color = if (isDarkTheme) Color.Black else Color.White
                        )
                    ) {
                        priorityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        color = if (isDarkTheme) Color.White else Color.Black
                                    )
                                },
                                onClick = {
                                    priorityTag = option
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Date Picker
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val cal = Calendar.getInstance()
                                cal.set(year, month, dayOfMonth)
                                datePicked = dateFormatter.format(cal.time)
                                dateError = ""
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = if (dateError.isNotEmpty()) SolidColor(MaterialTheme.colorScheme.error)
                        else SolidColor(MaterialTheme.colorScheme.outline)
                    )
                ) {
                    Text(if (datePicked.isEmpty()) "Select Date" else "Date: $datePicked")
                }

                if (dateError.isNotEmpty()) {
                    Text(
                        text = dateError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Time pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val cal = Calendar.getInstance()
                                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    cal.set(Calendar.MINUTE, minute)
                                    startTimePicked = timeFormatter.format(cal.time)
                                    startTimeError = ""
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = if (startTimeError.isNotEmpty()) SolidColor(MaterialTheme.colorScheme.error)
                            else SolidColor(MaterialTheme.colorScheme.outline)
                        )
                    ) {
                        Text(if (startTimePicked.isEmpty()) "Start Time" else "Time: $startTimePicked")
                    }

                    OutlinedButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val cal = Calendar.getInstance()
                                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    cal.set(Calendar.MINUTE, minute)
                                    endTimePicked = timeFormatter.format(cal.time)
                                    endTimeError = ""
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = if (endTimeError.isNotEmpty()) SolidColor(MaterialTheme.colorScheme.error)
                            else SolidColor(MaterialTheme.colorScheme.outline)
                        )
                    ) {
                        Text(if (endTimePicked.isEmpty()) "End Time" else "Time: $endTimePicked")
                    }
                }

                // Confirm & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            currentBooking.value?.let {
                                viewModel.deleteBookings(it.slotId)
                            }
                            viewModel.updateSlotStatus(slotId, SlotStatus.AVAILABLE)
                            vehicleNo = ""
                            vehicleType = ""
                            userId = ""
                            username = ""
                            contactNo = ""
                            priorityTag = ""
                            datePicked = ""
                            startTimePicked = ""
                            endTimePicked = ""
                            Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                            onNavigateUp()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }

                    Button(
                        onClick = {
                            when {
                                vehicleNo.isBlank() -> vehicleNoError = "Enter vehicle no."
                                vehicleType.isBlank() -> vehicleTypeError = "Enter vehicle type"
                                userId.isBlank() -> userIdError = "Enter user ID"
                                username.isBlank() -> usernameError = "Enter username"
                                contactNo.isBlank() -> contactNoError = "Enter contact no."
                                contactNo.length < 10 -> contactNoError = "Enter valid mobile no"
                                datePicked.isBlank() -> dateError = "Select date"
                                startTimePicked.isBlank() -> startTimeError = "Select start time"
                                endTimePicked.isBlank() -> endTimeError = "Select end time"
                                priorityTag.isBlank() -> priorityTagError = "Select one"
                                else -> {
                                    val dateTimeFormat =
                                        SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
                                    val dayFormat =
                                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                                    val bookingDate = Timestamp(dayFormat.parse(datePicked)!!)
                                    val bookingStartTime =
                                        Timestamp(dateTimeFormat.parse("$datePicked $startTimePicked")!!)
                                    val bookingEndTime =
                                        Timestamp(dateTimeFormat.parse("$datePicked $endTimePicked")!!)

                                    val booking = BookingDetailsDataClass(
                                        bookedBy = username,
                                        vehicleNumber = vehicleNo,
                                        vehicleType = vehicleType,
                                        customUserId = userId,
                                        userName = username,
                                        slotId = slotId,
                                        zone = zoneName,
                                        date = bookingDate,
                                        status = SlotStatus.BOOKED,
                                        bookingStartTime = bookingStartTime,
                                        bookingEndTime = bookingEndTime,
                                        contactNo = contactNo,
                                        priorityTag = priorityTag
                                    )

                                    viewModel.addBooking(booking)
                                    viewModel.addBookingHistory(booking)
                                    viewModel.updateSlotStatus(slotId, SlotStatus.BOOKED)

                                    Toast.makeText(
                                        context,
                                        "Booking confirmed!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onNavigateUp()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm Booking")
                    }
                }
            }
        }
    }
}