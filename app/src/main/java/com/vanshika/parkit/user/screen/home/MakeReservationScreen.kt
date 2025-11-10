package com.vanshika.parkit.user.screen.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.vanshika.parkit.ui.theme.ThemePreference
import com.vanshika.parkit.user.data.model.ReservationRequestDataClass
import com.vanshika.parkit.user.viewmodel.ReservationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeReservation(
    slotId: String = "",
    zoneName: String = "",
    onNavigateUp: () -> Unit,
    viewModel: ReservationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    var name by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    var contactNo by remember { mutableStateOf("") }
    var priorityTag by remember { mutableStateOf("") }

    var datePicked by remember { mutableStateOf("") }
    var startTimePicked by remember { mutableStateOf("") }
    var endTimePicked by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var userIdError by remember { mutableStateOf("") }
    var vehicleNumberError by remember { mutableStateOf("") }
    var vehicleTypeError by remember { mutableStateOf("") }
    var contactError by remember { mutableStateOf("") }
    var priorityError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var startTimeError by remember { mutableStateOf("") }
    var endTimeError by remember { mutableStateOf("") }

    val priorityOptions = listOf("Student", "Staff")
    var expanded by remember { mutableStateOf(false) }
    var currentVoiceField by remember { mutableStateOf("") }

    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText =
            result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        spokenText?.let {
            when (currentVoiceField) {
                "name" -> name = it
                "userId" -> userId = it
                "vehicleNumber" -> vehicleNumber = it
                "vehicleType" -> vehicleType = it
                "contactNo" -> contactNo = it
            }
        }
    }

    fun launchVoiceInput(field: String) {
        currentVoiceField = field
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        voiceLauncher.launch(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reserve Slot",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section: Personal Information
                    Text(
                        "👤 User Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    CustomOutlinedTextField(
                        value = name,
                        onValueChange = { name = it; if (it.isNotEmpty()) nameError = "" },
                        label = "Full Name",
                        error = nameError,
                        onVoiceClick = { launchVoiceInput("name") }
                    )

                    CustomOutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it; if (it.isNotEmpty()) userIdError = "" },
                        label = "User ID",
                        error = userIdError,
                        onVoiceClick = { launchVoiceInput("userId") }
                    )

                    CustomOutlinedTextField(
                        value = contactNo,
                        onValueChange = { contactNo = it; if (it.isNotEmpty()) contactError = "" },
                        label = "Contact Number",
                        error = contactError,
                        keyboardType = KeyboardType.Number,
                        onVoiceClick = { launchVoiceInput("contactNo") }
                    )

                    // Priority Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = priorityTag,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Priority Tag", fontWeight = FontWeight.SemiBold) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            isError = priorityError.isNotEmpty()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(
                                color = if (isDarkTheme) Color.Black else Color.White
                            )
                        ) {
                            priorityOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        priorityTag = option
                                        expanded = false
                                        priorityError = ""
                                    }
                                )
                            }
                        }
                    }
                    if (priorityError.isNotEmpty()) {
                        Text(
                            priorityError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Section: Vehicle Information
                    Text(
                        "🚗 Vehicle Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    CustomOutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = {
                            vehicleNumber = it; if (it.isNotEmpty()) vehicleNumberError = ""
                        },
                        label = "Vehicle Number",
                        error = vehicleNumberError,
                        onVoiceClick = { launchVoiceInput("vehicleNumber") }
                    )

                    CustomOutlinedTextField(
                        value = vehicleType,
                        onValueChange = { vehicleType = it },
                        label = "Vehicle Type (e.g., Car, Bike)",
                        error = vehicleTypeError,
                        onVoiceClick = { launchVoiceInput("vehicleType") }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Section: Booking Details
                    Text(
                        "📅 Reservation Schedule",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = slotId,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Slot") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = zoneName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Zone") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Date Picker Button
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
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (dateError.isNotEmpty())
                                MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            if (datePicked.isEmpty()) "📅 Select Date" else "📅 $datePicked"
                        )
                    }
                    if (dateError.isNotEmpty()) {
                        Text(
                            dateError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    // Time Pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val cal = Calendar.getInstance()
                                        cal.set(Calendar.HOUR_OF_DAY, hour)
                                        cal.set(Calendar.MINUTE, minute)
                                        startTimePicked = timeFormatter.format(cal.time)
                                        startTimeError = ""
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                                ).show()
                            },
                            modifier = Modifier
                                .weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = if (startTimeError.isNotEmpty()) SolidColor(MaterialTheme.colorScheme.error)
                                else SolidColor(MaterialTheme.colorScheme.outline)
                            )
                        ) {
                            Text(
                                if (startTimePicked.isEmpty()) "🕐 Start Time: " else startTimePicked
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val cal = Calendar.getInstance()
                                        cal.set(Calendar.HOUR_OF_DAY, hour)
                                        cal.set(Calendar.MINUTE, minute)
                                        endTimePicked = timeFormatter.format(cal.time)
                                        endTimeError = ""
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                                ).show()
                            },
                            modifier = Modifier
                                .weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = if (endTimeError.isNotEmpty()) SolidColor(MaterialTheme.colorScheme.error)
                                else SolidColor(MaterialTheme.colorScheme.outline)
                            )
                        ) {
                            Text(
                                if (endTimePicked.isEmpty()) "🕐 End Time:" else endTimePicked
                            )
                        }
                    }

                    if (startTimeError.isNotEmpty()) {
                        Text(
                            startTimeError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                    if (endTimeError.isNotEmpty()) {
                        Text(
                            endTimeError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onNavigateUp,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                var isValid = true
                                if (name.isEmpty()) {
                                    nameError = "Enter name"; isValid = false
                                }
                                if (userId.isEmpty()) {
                                    userIdError = "Enter user ID"; isValid = false
                                }
                                if (vehicleNumber.isEmpty()) {
                                    vehicleNumberError = "Enter vehicle number"; isValid = false
                                }
                                if (vehicleType.isEmpty()) {
                                    vehicleTypeError = "Enter vehicle type"; isValid = false
                                }
                                if (contactNo.isEmpty()) {
                                    contactError = "Enter contact"; isValid = false
                                }
                                if (priorityTag.isEmpty()) {
                                    priorityError = "Select priority"; isValid = false
                                }
                                if (datePicked.isEmpty()) {
                                    dateError = "Select date"; isValid = false
                                }
                                if (startTimePicked.isEmpty()) {
                                    startTimeError = "Select start time"; isValid = false
                                }
                                if (endTimePicked.isEmpty()) {
                                    endTimeError = "Select end time"; isValid = false
                                }

                                if (isValid) {
                                    val dateTimeFormat =
                                        SimpleDateFormat(
                                            "dd MMM yyyy hh:mm a",
                                            Locale.getDefault()
                                        )
                                    val dayFormat =
                                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                                    val bookingDate = Timestamp(dayFormat.parse(datePicked)!!)
                                    val bookingStartTime =
                                        Timestamp(dateTimeFormat.parse("$datePicked $startTimePicked")!!)
                                    val bookingEndTime =
                                        Timestamp(dateTimeFormat.parse("$datePicked $endTimePicked")!!)

                                    coroutineScope.launch {
                                        viewModel.submitReservation(
                                            ReservationRequestDataClass(
                                                userName = name,
                                                customUserId = userId,
                                                vehicleNumber = vehicleNumber,
                                                vehicleType = vehicleType,
                                                contactNo = contactNo,
                                                priorityTag = priorityTag,
                                                date = bookingDate,
                                                bookingStartTime = bookingStartTime,
                                                bookingEndTime = bookingEndTime,
                                                zone = zoneName,
                                                slotId = slotId
                                            )
                                        )
                                        Toast.makeText(
                                            context,
                                            "Slot Reserved Successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onNavigateUp()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please fill all fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Reserve",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reserve Slot", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onVoiceClick: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontWeight = FontWeight.SemiBold) },
            trailingIcon = {
                IconButton(onClick = onVoiceClick) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = error.isNotEmpty(),
            singleLine = true
        )
        if (error.isNotEmpty()) {
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}