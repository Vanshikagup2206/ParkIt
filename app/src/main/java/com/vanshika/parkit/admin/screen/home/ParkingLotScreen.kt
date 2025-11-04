package com.vanshika.parkit.admin.screen.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.vanshika.parkit.admin.navigation.NavRoutes
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.ui.theme.ThemePreference

data class ParkingSlotData(
    val id: String,
    val zoneName: String,
    val status: MutableState<SlotStatus> = mutableStateOf(SlotStatus.AVAILABLE)
)

@Composable
fun ParkingLotLayout(
    navController: NavHostController,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val slotsMap = viewModel.slotsMap
    val context = LocalContext.current
    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Enhanced Entrance Header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .shadow(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Entrance",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ENTRANCE",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Entrance",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Legend Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem("Available", if (isDarkTheme) Color(0xFFA5D6A7) else Color(0xFFC8E6C9))
                LegendItem("Booked", if (isDarkTheme) Color(0xFFFFCC80) else Color.Yellow)
                LegendItem("Reserved", Color.Red)
                LegendItem("Maintenance", Color.Gray)
            }
        }

        // Parking Area
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            // Left Blocks
            Column(modifier = Modifier.weight(0.62f)) {
                val blocks = listOf(
                    "A" to 4, "B" to 4, "C" to 4, "D" to 4, "E" to 3,
                    "F" to 3, "G" to 2, "H" to 2, "I" to 2, "J" to 2,
                    "K" to 2, "L" to 3, "M" to 4, "N" to 4, "O" to 4
                )
                blocks.forEachIndexed { index, (block, slotsCount) ->
                    ParkingRowAlignedToMiddle(
                        blockLabel = block,
                        slotsCount = slotsCount,
                        showPillarsBelow = index != blocks.lastIndex,
                        slotsMap = slotsMap,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Enhanced Middle Path with dashed lines effect
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(890.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Path",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "DRIVE\nWAY",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Enhanced Right Side
            Column(
                modifier = Modifier
                    .weight(0.33f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reserved Area
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(370.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF6B6B).copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "Reserved",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "RESERVED AREA",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "No Parking",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Pillar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outline)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⬛ PILLAR ⬛",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Extra Parking Space
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFFA5D6A7) else Color(0xFFC8E6C9)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalParking,
                                contentDescription = "Parking",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "EXTRA PARKING",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Space Available",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ParkingRowAlignedToMiddle(
    blockLabel: String,
    slotsCount: Int,
    showPillarsBelow: Boolean = true,
    slotsMap: MutableMap<String, ParkingSlotData>,
    navController: NavHostController,
    viewModel: BookingViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in slotsCount downTo 1) {
                val slotId = "$blockLabel$i"
                ParkingSlot(
                    slotData = slotsMap[slotId]!!,
                    onStatusChange = { newStatus ->
                        slotsMap[slotId]?.status?.value = newStatus
                    },
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        if (showPillarsBelow) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End)
            ) {
                repeat(slotsCount) {
                    HorizontalPillar()
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
fun ParkingSlot(
    slotData: ParkingSlotData,
    onStatusChange: (SlotStatus) -> Unit,
    navController: NavHostController,
    viewModel: BookingViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    Card(
        modifier = Modifier
            .size(width = 34.dp, height = 36.dp)
            .clickable { showDialog = true },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (slotData.status.value) {
                SlotStatus.RESERVED -> Color(0xFFFF6B6B)
                SlotStatus.AVAILABLE -> if (isDarkTheme) Color(0xFFA5D6A7) else Color(0xFFC8E6C9)
                SlotStatus.BOOKED -> if (isDarkTheme) Color(0xFFFFCC80) else Color(0xFFFFEB3B)
                SlotStatus.MAINTENANCE -> Color(0xFFBDBDBD)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = when (slotData.status.value) {
                        SlotStatus.RESERVED -> Color(0xFFD32F2F)
                        SlotStatus.AVAILABLE -> if (isDarkTheme) Color(0xFF81C784) else Color(0xFF66BB6A)
                        SlotStatus.BOOKED -> Color(0xFFFFA726)
                        SlotStatus.MAINTENANCE -> Color(0xFF757575)
                    },
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = slotData.id,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = when (slotData.status.value) {
                    SlotStatus.RESERVED -> Color.White
                    SlotStatus.AVAILABLE -> Color(0xFF2E7D32)
                    SlotStatus.BOOKED -> Color(0xFFE65100)
                    SlotStatus.MAINTENANCE -> Color.White
                }
            )
        }
    }

    if (showDialog) {
        when (slotData.status.value) {
            SlotStatus.AVAILABLE -> AvailableSlotDialog(
                slotData, onStatusChange, navController, viewModel
            ) { showDialog = false }
            SlotStatus.BOOKED -> BookedSlotDialog(
                slotData, onStatusChange, navController, viewModel
            ) { showDialog = false }
            SlotStatus.RESERVED -> ReservedSlotDialog(
                slotData, onStatusChange, navController, viewModel
            ) { showDialog = false }
            SlotStatus.MAINTENANCE -> MaintenanceSlotDialog(
                slotData, onStatusChange, navController, viewModel
            ) { showDialog = false }
        }
    }
}

@Composable
fun HorizontalPillar() {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .size(width = 8.dp, height = 8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), CircleShape)
            )
        }
    }
}

@Composable
fun AvailableSlotDialog(
    slotData: ParkingSlotData,
    onStatusChange: (SlotStatus) -> Unit,
    navController: NavHostController,
    viewModel: BookingViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Available Slot", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = {
                    onStatusChange(SlotStatus.BOOKED)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.BOOKED)
                    navController.navigate(
                        NavRoutes.BookingPage.createRoute(slotData.id, slotData.zoneName, SlotStatus.BOOKED)
                    )
                    onDismiss()
                }) { Text("📝 Make Booking") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.MAINTENANCE)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.MAINTENANCE)
                    Toast.makeText(context, "Marked as maintenance", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) { Text("🔧 Mark under maintenance") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.RESERVED)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.RESERVED)
                    navController.navigate(
                        NavRoutes.ReserveBookingPage.createRoute(slotData.id, slotData.zoneName, SlotStatus.RESERVED)
                    )
                    onDismiss()
                }) { Text("🔒 Reserve Slot") }
            }
        },
        confirmButton = {}, dismissButton = {}
    )
}

@Composable
fun BookedSlotDialog(
    slotData: ParkingSlotData,
    onStatusChange: (SlotStatus) -> Unit,
    navController: NavHostController,
    viewModel: BookingViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Booked Slot", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = {
                    navController.navigate(
                        NavRoutes.UpdateBookings.createRoute(slotData.id, slotData.zoneName, SlotStatus.BOOKED)
                    )
                    onDismiss()
                }) { Text("✏️ Update Booking") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.AVAILABLE)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.AVAILABLE)
                    viewModel.deleteBookings(slotData.id)
                    Toast.makeText(context, "Booking cancelled", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) { Text("❌ Cancel Booking") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.RESERVED)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.RESERVED)
                    navController.navigate(
                        NavRoutes.ReserveBookingPage.createRoute(slotData.id, slotData.zoneName, SlotStatus.RESERVED)
                    )
                    onDismiss()
                }) { Text("🔒 Mark as Reserved") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.MAINTENANCE)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.MAINTENANCE)
                    Toast.makeText(context, "Marked as maintenance", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) { Text("🔧 Mark under maintenance") }
            }
        },
        confirmButton = {}, dismissButton = {}
    )
}

@Composable
fun ReservedSlotDialog(
    slotData: ParkingSlotData,
    onStatusChange: (SlotStatus) -> Unit,
    navController: NavHostController,
    viewModel: BookingViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reserved Slot", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = {
                    navController.navigate(
                        NavRoutes.UpdateReserveBookings.createRoute(slotData.id, slotData.zoneName, SlotStatus.RESERVED)
                    )
                    onDismiss()
                }) { Text("✏️ Update Booking") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.AVAILABLE)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.AVAILABLE)
                    viewModel.deleteBookings(slotData.id)
                    Toast.makeText(context, "Reservation removed", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) { Text("🔓 UnMark Reserved") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.MAINTENANCE)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.MAINTENANCE)
                    Toast.makeText(context, "Marked as maintenance", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) { Text("🔧 Mark under maintenance") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.BOOKED)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.BOOKED)
                    navController.navigate(
                        NavRoutes.BookingPage.createRoute(slotData.id, slotData.zoneName, SlotStatus.BOOKED)
                    )
                    onDismiss()
                }) { Text("📝 Make Booking") }
            }
        },
        confirmButton = {}, dismissButton = {}
    )
}

@Composable
fun MaintenanceSlotDialog(
    slotData: ParkingSlotData,
    onStatusChange: (SlotStatus) -> Unit,
    navController: NavHostController,
    viewModel: BookingViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Maintenance Slot", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = {
                    onStatusChange(SlotStatus.AVAILABLE)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.AVAILABLE)
                    Toast.makeText(context, "Removed from maintenance", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) { Text("✅ UnMark Maintenance") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.RESERVED)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.RESERVED)
                    navController.navigate(
                        NavRoutes.ReserveBookingPage.createRoute(slotData.id, slotData.zoneName, SlotStatus.RESERVED)
                    )
                    onDismiss()
                }) { Text("🔒 Mark Reserved") }

                TextButton(onClick = {
                    onStatusChange(SlotStatus.BOOKED)
                    viewModel.updateSlotStatus(slotData.id, SlotStatus.BOOKED)
                    navController.navigate(
                        NavRoutes.BookingPage.createRoute(slotData.id, slotData.zoneName, SlotStatus.BOOKED)
                    )
                    onDismiss()
                }) { Text("📝 Make Booking") }
            }
        },
        confirmButton = {}, dismissButton = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewParkingLotLayout() {
    ParkingLotLayout(navController = rememberNavController())
}