package com.vanshika.parkit.user.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.vanshika.parkit.admin.screen.home.ParkingSlotData
import com.vanshika.parkit.admin.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectionScreen(
    slotId: String = "",
    zoneName: String = "",
    onNavigateUp: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val rawSlotCoordinates = viewModel.slotPositions[slotId] ?: Offset.Zero

    // Middle lane fixed X
    val middleX = 200f
    val entrance = Offset(middleX, 0f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Directions") },
            navigationIcon = {
                IconButton(onClick = { onNavigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            UserParkingLotSection(
                zoneName = zoneName,
                highlightSlotId = slotId,
                slotsMap = viewModel.slotsMap
            )

            Canvas(modifier = Modifier.fillMaxSize()) {

                // Generate path
                val pathPoints = generatePath(entrance, rawSlotCoordinates, size.width / 2f)

                // Dotted Line
                for (i in 0 until pathPoints.size - 1) {
                    drawLine(
                        color = Color.Blue,
                        start = pathPoints[i],
                        end = pathPoints[i + 1],
                        strokeWidth = 6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                    )
                }

                // Arrow at destination
                if (rawSlotCoordinates != Offset.Zero) {
                    val arrowSize = 20f
                    val p1 = Offset(rawSlotCoordinates.x, rawSlotCoordinates.y - arrowSize)
                    val p2 =
                        Offset(
                            rawSlotCoordinates.x - arrowSize / 2,
                            rawSlotCoordinates.y + arrowSize / 2)
                    val p3 =
                        Offset(
                            rawSlotCoordinates.x + arrowSize / 2,
                            rawSlotCoordinates.y + arrowSize / 2)

                    drawLine(Color.Blue, p1, p2, strokeWidth = 6f)
                    drawLine(Color.Blue, p1, p3, strokeWidth = 6f)
                    drawLine(Color.Blue, p2, p3, strokeWidth = 6f)
                }
            }
        }

        // Instructions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Slot $slotId", style = MaterialTheme.typography.headlineSmall)
            Text("Zone $zoneName", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Generate text instructions
            val scaledSlotCoordinates = Offset(
                rawSlotCoordinates.x, rawSlotCoordinates.y
            )
            val instructions = generateInstructions(
                generatePath(entrance, scaledSlotCoordinates, middleX)
            )

            Text(
                text = "Instruction: $instructions",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun UserParkingLotSection(
    zoneName: String,
    highlightSlotId: String,
    slotsMap: MutableMap<String, ParkingSlotData>
) {
    val blocks = listOf(
        "A" to 4, "B" to 4, "C" to 4, "D" to 4, "E" to 3,
        "F" to 3, "G" to 2, "H" to 2, "I" to 2, "J" to 2,
        "K" to 2, "L" to 3, "M" to 4, "N" to 4, "O" to 4
    )

    // find current zone index
    val currentIndex = blocks.indexOfFirst { it.first == zoneName }

    // take 2 above, current, and 2 below (safe range)
    val visibleBlocks = blocks.subList(
        maxOf(0, currentIndex - 2),
        minOf(blocks.size, currentIndex + 3)
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Left Blocks
        Column(modifier = Modifier.weight(0.55f)) {
            visibleBlocks.forEachIndexed { index, (block, slotsCount) ->
                UserParkingRow(
                    blockLabel = block,
                    slotsCount = slotsCount,
                    showPillarsBelow = index != visibleBlocks.lastIndex,
                    slotsMap = slotsMap,
                    navController = rememberNavController(), // dummy
                    viewModel = hiltViewModel()
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Middle Path
        Box(
            modifier = Modifier
                .width(50.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text("Path", color = MaterialTheme.colorScheme.onSecondary, fontSize = 14.sp)
        }
    }
}

/** Generate path: entrance → middle lane → zone row → slot */
fun generatePath(entry: Offset, slot: Offset, middleX: Float): List<Offset> {
    val path = mutableListOf<Offset>()
    path.add(entry)

    if (slot != Offset.Zero) {
        // Step 1: go straight down middle lane until aligned with slot’s row (y position)
        val middleAtSlotY = Offset(middleX, slot.y)
        path.add(middleAtSlotY)

        // Step 2: turn left/right from middle lane into slot
        path.add(slot)
    }

    return path
}

/** Generate textual driving instructions */
fun generateInstructions(path: List<Offset>): String {
    val instructions = mutableListOf<String>()

    if (path.size > 1) {
        instructions.add("Go straight down the middle lane until Zone area.")
    }
    if (path.size > 2) {
        val dx = path[2].x - path[1].x
        when {
            dx > 0 -> instructions.add("Turn right into your zone.")
            dx < 0 -> instructions.add("Turn left into your zone.")
        }
        instructions.add("Proceed forward to your slot.")
    }

    return instructions.joinToString(" ")
}