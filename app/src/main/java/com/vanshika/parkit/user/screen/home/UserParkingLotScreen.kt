package com.vanshika.parkit.user.screen.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.vanshika.parkit.admin.screen.home.HorizontalPillar
import com.vanshika.parkit.admin.screen.home.ParkingSlotData
import com.vanshika.parkit.admin.screen.home.SlotStatus
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.ui.theme.ThemePreference
import com.vanshika.parkit.user.navigation.UserNavRoutes

@Composable
fun UserParkingLotScreen(
    navHostController: NavHostController,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val slotsMap = viewModel.slotsMap

    val context = LocalContext.current
    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Entrance Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ENTRANCE",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        }

        // Parking Area
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            // Left Blocks
            Column(modifier = Modifier.weight(0.55f)) {
                val blocks = listOf(
                    "A" to 4, "B" to 4, "C" to 4, "D" to 4, "E" to 3,
                    "F" to 3, "G" to 2, "H" to 2, "I" to 2, "J" to 2,
                    "K" to 2, "L" to 3, "M" to 4, "N" to 4, "O" to 4
                )
                blocks.forEachIndexed { index, (block, slotsCount) ->
                    UserParkingRow(
                        blockLabel = block,
                        slotsCount = slotsCount,
                        showPillarsBelow = index != blocks.lastIndex,
                        slotsMap = slotsMap,
                        navController = navHostController,
                        viewModel = viewModel
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle Path
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(840.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Path",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Side
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Reserved Area",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(MaterialTheme.colorScheme.outline),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pillar",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .background(
                            color = if (isDarkTheme) Color(0xFFA5D6A7) else Color(0xFFC8E6C9)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Space for Parking",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun UserParkingRow(
    blockLabel: String,
    slotsCount: Int,
    showPillarsBelow: Boolean = true,
    slotsMap: MutableMap<String, ParkingSlotData>,
    navController: NavHostController,
    viewModel: BookingViewModel
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in slotsCount downTo 1) {
                val slotId = "$blockLabel$i"
                slotsMap[slotId]?.let { slot ->
                    UserParkingSlot(slot, navController, viewModel)
                }
                if (i != 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        if (showPillarsBelow) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                repeat(slotsCount) {
                    HorizontalPillar()
                    if (it != slotsCount - 1) Spacer(modifier = Modifier.width(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun UserParkingSlot(
    slotData: ParkingSlotData,
    navController: NavHostController,
    viewModel: BookingViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)

    Box(
        modifier = Modifier
            .size(width = 35.dp, height = 36.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface)
            .background(
                when (slotData.status.value) {
                    SlotStatus.RESERVED -> Color.Red        // fixed
                    SlotStatus.AVAILABLE -> if (isDarkTheme) Color(0xFFA5D6A7) else Color(0xFFC8E6C9)
                    SlotStatus.BOOKED -> if (isDarkTheme) Color(0xFFFFCC80) else Color.Yellow     // fixed
                    SlotStatus.MAINTENANCE -> Color.Gray    // fixed
                }
            )
            .onGloballyPositioned { cords ->
                val position = cords.positionInRoot()
                Log.d("ParkingSlot", "ID: ${slotData.id}, Position: $position")
                viewModel.updateSlotPosition(slotData.id, position)
            }
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = slotData.id,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Slot ${slotData.id}") },
            text = {
                Column {
                    TextButton(onClick = {
                        navController.navigate(
                            UserNavRoutes.DirectionScreen.createRoute(
                                slotData.id,
                                slotData.zoneName
                            )
                        )
                        showDialog = false
                    }) { Text("See Directions") }

                    TextButton(onClick = {
                        navController.navigate(
                            UserNavRoutes.ReportScreen.createRoute(
                                slotData.id,
                                slotData.zoneName
                            )
                        )
                        showDialog = false
                    }) { Text("Make Report / Issue") }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserParkingLot() {
    UserParkingLotScreen(navHostController = rememberNavController())
}