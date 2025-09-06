package com.vanshika.parkit.user.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vanshika.parkit.MainNavRoutes
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import com.vanshika.parkit.ui.theme.ThemePreference
import com.vanshika.parkit.user.screen.bookings.UserBookingScreen
import com.vanshika.parkit.user.screen.home.DirectionScreen
import com.vanshika.parkit.user.screen.home.ReportScreen
import com.vanshika.parkit.user.screen.home.UserHomeScreen
import com.vanshika.parkit.user.screen.notifications.UserNotificationScreen
import com.vanshika.parkit.user.screen.profile.UserProfileScreen
import kotlinx.coroutines.launch

@Composable
fun UserNavigationGraph(
    navHostController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier,
    authenticationViewModel: AuthenticationViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = UserBottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(UserBottomNavItem.Home.route) {
            UserHomeScreen(navHostController = navHostController)
        }
        composable(UserBottomNavItem.MyBookings.route) {
            UserBookingScreen(authViewModel = authenticationViewModel)
        }
        composable(UserBottomNavItem.Notification.route) {
            UserNotificationScreen()
        }
        composable(UserBottomNavItem.Profile.route) {
            val context = LocalContext.current
            val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            UserProfileScreen(
                onLogout = {
                    authenticationViewModel.logout()
                    rootNavController.navigate(MainNavRoutes.SplashScreen.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                isDarkTheme = isDarkTheme,
                onThemeChange = {theme ->
                    scope.launch{
                        ThemePreference.saveTheme(context, theme)
                    }
                }
            )
        }

        //navigation

        composable(UserNavRoutes.ReportScreen.route) { navBackStackEntry ->
            val slotId = navBackStackEntry.arguments?.getString("slotId") ?: ""
            val zoneName = navBackStackEntry.arguments?.getString("zoneName") ?: ""

            ReportScreen(
                slotId = slotId,
                zoneName = zoneName,
                onNavigationUp = { navHostController.navigateUp() }
            )
        }

        composable(UserNavRoutes.DirectionScreen.route) { navBackStackEntry ->
            val slotId = navBackStackEntry.arguments?.getString("slotId") ?: ""
            val zoneName = navBackStackEntry.arguments?.getString("zoneName") ?: ""

            DirectionScreen(
                slotId = slotId,
                zoneName = zoneName,
                onNavigateUp = { navHostController.navigateUp() }
            )
        }
    }
}