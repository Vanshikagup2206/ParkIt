package com.vanshika.parkit.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.vanshika.parkit.MainNavRoutes
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    authenticationViewModel: AuthenticationViewModel
) {
    // 🔹 Observe user state from ViewModel
    val user by authenticationViewModel.user.collectAsState()
    val customId by authenticationViewModel.customUserId.collectAsState()

    LaunchedEffect(user, customId) {
        authenticationViewModel.checkLoggedInUser()
        delay(1200)

        val currentUser = user

        if (currentUser != null && customId != null) {
            if (customId == "12200814") {
                navHostController.navigate(MainNavRoutes.AdminMain.route) {
                    popUpTo(MainNavRoutes.SplashScreen.route) { inclusive = true }
                }
            } else {
                navHostController.navigate(MainNavRoutes.UserMain.route) {
                    popUpTo(MainNavRoutes.SplashScreen.route) { inclusive = true }
                }
            }
        } else {
            navHostController.navigate(MainNavRoutes.Login.route) {
                popUpTo(MainNavRoutes.SplashScreen.route) { inclusive = true }
            }
        }
    }

    // 🔹 Loader UI
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}