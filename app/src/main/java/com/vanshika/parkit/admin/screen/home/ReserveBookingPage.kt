package com.vanshika.parkit.admin.screen.home

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.vanshika.parkit.R
import com.vanshika.parkit.admin.data.model.BookingDetailsDataClass
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.ui.theme.ThemePreference
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveBookingPage(
    slotId: String = "",
    zoneName: String = "",
    originalStatus: SlotStatus,
    viewModel: BookingViewModel,
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val previousStatus = remember { originalStatus }

    // Form fields
    var vehicleNo by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var contactNo by remember { mutableStateOf("") }
    var priorityTag by remember { mutableStateOf("") }

    var datePicked by remember { mutableStateOf("") }
    var startTimePicked by remember { mutableStateOf("") }
    var endTimePicked by remember { mutableStateOf("") }

    // Error states
    var vehicleNoError by remember { mutableStateOf("") }
    var vehicleTypeError by remember { mutableStateOf("") }
    var userIdError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var contactNoError by remember { mutableStateOf("") }
    var priorityTagError by remember { mutableStateOf("") }

    var dateError by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }

    // Dropdown states
    val priorityOptions = listOf("Normal", "Staff", "Student")
    var priorityExpanded by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    val allBookings by viewModel.allBookings

    LaunchedEffect(Unit) {
        viewModel.loadAllBookings()
    }

    // Current booking for this slot
    val currentBooking = remember { mutableStateOf<BookingDetailsDataClass?>(null) }

    LaunchedEffect(allBookings) {
        currentBooking.value = allBookings.find { it.slotId == slotId }
    }

    // Vehicle Number suggestions from bookings
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

    // ---- Speech-to-Text Setup ----
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
            text = "Reserve Booking",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(0.dp), // prevents overlay
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme)
                    Color.Black
                else
                    Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                /*** Vehicle Number ***/
                ExposedDropdownMenuBox(expanded = vehicleNoExpanded && vehicleNoSuggestions.isNotEmpty(),
                    onExpandedChange = { vehicleNoExpanded = !vehicleNoExpanded }) {
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
                    ExposedDropdownMenu(expanded = vehicleNoExpanded && vehicleNoSuggestions.isNotEmpty(),
                        onDismissRequest = { vehicleNoExpanded = false }) {
                        vehicleNoSuggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
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

                /*** Vehicle Type ***/
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = {
                        vehicleType = it
                        vehicleTypeError = ""
                    },
                    label = { Text("Vehicle Type") },
                    isError = vehicleTypeError.isNotEmpty(),
                    supportingText = {
                        if (vehicleTypeError.isNotEmpty()) Text(
                            vehicleTypeError, color = MaterialTheme.colorScheme.error
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { startListeningFor("vehicleType") }) {
                            Icon(
                                painterResource(id = R.drawable.baseline_mic_24),
                                contentDescription = "Speak Vehicle Type"
                            )
                        }
                    },
                    placeholder = { Text("Car, Bike, etc.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                /*** User ID ***/
                ExposedDropdownMenuBox(expanded = userIdExpanded && userIdSuggestions.isNotEmpty(),
                    onExpandedChange = { userIdExpanded = !userIdExpanded }) {
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
                                    contentDescription = "Speak User ID"
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
                        onDismissRequest = { userIdExpanded = false }
                    ) {
                        userIdSuggestions.forEach { user ->
                            DropdownMenuItem(
                                text = { Text("${user.customUserId} - ${user.userName}") },
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

                /*** Username ***/
                ExposedDropdownMenuBox(expanded = usernameExpanded && usernameSuggestions.isNotEmpty(),
                    onExpandedChange = { usernameExpanded = !usernameExpanded }) {
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
                        onDismissRequest = { usernameExpanded = false }
                    ) {
                        usernameSuggestions.forEach { user ->
                            DropdownMenuItem(
                                text = { Text("${user.userName} - ${user.customUserId}") },
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

                /*** Slot ID & Zone Name ***/
                OutlinedTextField(
                    value = slotId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Slot Id") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = zoneName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Zone Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                /*** Contact Number ***/
                OutlinedTextField(
                    value = contactNo,
                    onValueChange = { contactNo = it; contactNoError = "" },
                    label = { Text("Contact Number") },
                    isError = contactNoError.isNotEmpty(),
                    supportingText = {
                        if (contactNoError.isNotEmpty()) Text(
                            contactNoError, color = MaterialTheme.colorScheme.error
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { startListeningFor("contactNo") }) {
                            Icon(
                                painterResource(id = R.drawable.baseline_mic_24),
                                contentDescription = "Speak Contact Number"
                            )
                        }
                    },
                    singleLine = true,
                    placeholder = { Text("Enter Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                /*** Priority Dropdown ***/
                ExposedDropdownMenuBox(expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded }) {
                    OutlinedTextField(
                        value = priorityTag,
                        onValueChange = { priorityTagError = "" },
                        readOnly = true,
                        label = { Text("Priority Tag") },
                        isError = priorityTagError.isNotEmpty(),
                        placeholder = { Text("Select Priority") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }) {
                        priorityOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) },
                                onClick = { priorityTag = option; priorityExpanded = false })
                        }
                    }
                }

                /*** Date Picker ***/
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val cal = Calendar.getInstance()
                                cal.set(y, m, d)
                                datePicked = dateFormatter.format(cal.time)
                                dateError = ""
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = if (dateError.isNotEmpty()) SolidColor(
                            MaterialTheme.colorScheme.error
                        ) else SolidColor(MaterialTheme.colorScheme.outline)
                    )
                ) { Text(if (datePicked.isEmpty()) "Select Date" else "Date: $datePicked") }
                if (dateError.isNotEmpty()) Text(dateError, color = MaterialTheme.colorScheme.error)

                /*** Start & End Time Pickers ***/
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context, { _, h, m ->
                                    cal.set(Calendar.HOUR_OF_DAY, h)
                                    cal.set(Calendar.MINUTE, m)
                                    startTimePicked = timeFormatter.format(cal.time)
                                    timeError = ""
                                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = if (timeError.isNotEmpty()) SolidColor(
                                MaterialTheme.colorScheme.error
                            ) else SolidColor(MaterialTheme.colorScheme.outline)
                        )
                    ) { Text(if (startTimePicked.isEmpty()) "Start Time" else "Time: $startTimePicked") }

                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context, { _, h, m ->
                                    cal.set(Calendar.HOUR_OF_DAY, h)
                                    cal.set(Calendar.MINUTE, m)
                                    endTimePicked = timeFormatter.format(cal.time)
                                    timeError = ""
                                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = if (timeError.isNotEmpty()) SolidColor(
                                MaterialTheme.colorScheme.error
                            ) else SolidColor(MaterialTheme.colorScheme.outline)
                        )
                    ) { Text(if (endTimePicked.isEmpty()) "End Time" else "Time: $endTimePicked") }
                }

                /*** Confirm & Cancel ***/
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

                            // Reset fields and revert slot status
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
                        }, modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }

                    Button(
                        onClick = {
                            // Validation
                            when {
                                vehicleNo.isBlank() -> vehicleNoError = "Enter vehicle no."
                                vehicleType.isBlank() -> vehicleTypeError = "Enter vehicle type"
                                username.isBlank() -> usernameError = "Enter username"
                                contactNo.isBlank() -> contactNoError = "Enter contact no."
                                contactNo.length < 10 -> contactNoError = "Enter valid mobile no"
                                datePicked.isBlank() -> dateError = "Select date"
                                startTimePicked.isBlank() || endTimePicked.isBlank() -> timeError =
                                    "Select time"

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
                                        status = SlotStatus.RESERVED,
                                        bookingStartTime = bookingStartTime,
                                        bookingEndTime = bookingEndTime,
                                        contactNo = contactNo,
                                        priorityTag = priorityTag
                                    )

                                    currentBooking.value?.let {
                                        viewModel.deleteBookings(it.slotId)
                                    }

                                    viewModel.addBooking(booking)
                                    viewModel.updateSlotStatus(slotId, SlotStatus.RESERVED)
                                    Toast.makeText(
                                        context, "Booking confirmed!", Toast.LENGTH_SHORT
                                    ).show()
                                    onNavigateUp()
                                }
                            }
                        }, modifier = Modifier.weight(1f)
                    ) { Text("Confirm Booking") }
                }

                Spacer(Modifier.height(16.dp))
                Text("DEBUG: All Bookings", style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(allBookings) { booking ->
                        Text("${booking.vehicleNumber} - ${booking.userName} - ${booking.customUserId}")
                    }
                }
            }
        }
    }
}