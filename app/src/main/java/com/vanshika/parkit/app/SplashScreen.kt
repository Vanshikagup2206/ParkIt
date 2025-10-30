package com.vanshika.parkit.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vanshika.parkit.MainNavRoutes
import com.vanshika.parkit.R
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import com.vanshika.parkit.onboarding.OnBoardingPreference
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    authenticationViewModel: AuthenticationViewModel
) {
    val user by authenticationViewModel.user.collectAsState()
    val customId by authenticationViewModel.customUserId.collectAsState()

    // Animation state
    var startAnimation by remember { mutableStateOf(false) }

    // Animate scale and alpha
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000)
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(true) {
        startAnimation = true
        delay(1500)

        val hasSeenOnBoarding = OnBoardingPreference.isOnBoardingShown(navHostController.context)

        if (!hasSeenOnBoarding) {
            navHostController.navigate(MainNavRoutes.OnBoarding.route) {
                popUpTo(MainNavRoutes.SplashScreen.route) { inclusive = true }
            }
        } else {
            when {
                user == null -> {
                    navHostController.navigate(MainNavRoutes.Login.route) {
                        popUpTo(MainNavRoutes.SplashScreen.route) { inclusive = true }
                    }
                }

                user != null && customId != null -> {
                    if (customId == "12200814") {
                        navHostController.navigate(MainNavRoutes.AdminMain.route) {
                            popUpTo(MainNavRoutes.SplashScreen.route) { inclusive = true }
                        }
                    } else {
                        navHostController.navigate(MainNavRoutes.UserMain.route) {
                            popUpTo(MainNavRoutes.SplashScreen.route) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .alpha(alpha)
        )
    }
}