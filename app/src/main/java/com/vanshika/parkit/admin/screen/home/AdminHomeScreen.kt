package com.vanshika.parkit.admin.screen.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AdminHomeScreen(navHostController: NavHostController) {
    ParkingLotLayout(navController = navHostController)
}