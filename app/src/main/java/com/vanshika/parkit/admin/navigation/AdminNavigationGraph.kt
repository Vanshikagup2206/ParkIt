package com.vanshika.parkit.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vanshika.parkit.MainNavRoutes
import com.vanshika.parkit.admin.data.repository.BookingRepository
import com.vanshika.parkit.admin.screen.analytics.AdminAnalyticsScreen
import com.vanshika.parkit.admin.screen.home.AdminHomeScreen
import com.vanshika.parkit.admin.screen.home.BookingPage
import com.vanshika.parkit.admin.screen.home.ReserveBookingPage
import com.vanshika.parkit.admin.screen.home.SlotStatus
import com.vanshika.parkit.admin.screen.issues.AdminIssuesScreen
import com.vanshika.parkit.admin.screen.issues.IssueDetail
import com.vanshika.parkit.admin.screen.profile.AdminProfileScreen
import com.vanshika.parkit.admin.viewmodel.BookingViewModel
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import com.vanshika.parkit.ui.theme.ThemePreference
import kotlinx.coroutines.launch

@Composable
fun AdminNavigationGraph(
    navHostController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier,
    authenticationViewModel: AuthenticationViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = AdminBottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(AdminBottomNavItem.Home.route) {
            AdminHomeScreen(navHostController = navHostController)
        }
        composable(AdminBottomNavItem.Issues.route) {
            AdminIssuesScreen(
                onIssueClick = { issueId ->
                    navHostController.navigate(NavRoutes.IssueDetail.createRoute(issueId))
                }
            )
        }
        composable(AdminBottomNavItem.Analytics.route) {
            AdminAnalyticsScreen()
        }
        composable(AdminBottomNavItem.Profile.route) {
            val context = LocalContext.current
            val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            AdminProfileScreen(
                onLogout = {
                    authenticationViewModel.logout()
                    rootNavController.navigate(MainNavRoutes.SplashScreen.route){
                        popUpTo(0){
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
        composable(NavRoutes.BookingPage.route) { backStackEntry ->
            val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
            val zoneName = backStackEntry.arguments?.getString("zoneName") ?: ""
            val statusName =
                backStackEntry.arguments?.getString("status") ?: SlotStatus.AVAILABLE.name
            val originalStatus = SlotStatus.valueOf(statusName)

            BookingPage(
                slotId = slotId,
                zoneName = zoneName,
                originalStatus = originalStatus,
                viewModel = BookingViewModel(BookingRepository()),
                onNavigateUp = { navHostController.navigateUp() }
            )
        }
        composable(NavRoutes.ReserveBookingPage.route) { backStackEntry ->
            val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
            val zoneName = backStackEntry.arguments?.getString("zoneName") ?: ""
            val statusName =
                backStackEntry.arguments?.getString("status") ?: SlotStatus.AVAILABLE.name
            val originalStatus = SlotStatus.valueOf(statusName)

            ReserveBookingPage(
                slotId = slotId,
                zoneName = zoneName,
                originalStatus = originalStatus,
                viewModel = BookingViewModel(BookingRepository()),
                onNavigateUp = { navHostController.navigateUp() }
            )
        }
        composable(
            route = NavRoutes.IssueDetail.route,
            arguments = listOf(navArgument("issueId") { defaultValue = "" })
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
            IssueDetail(
                issueId = issueId,
                onBack = { navHostController.navigateUp() }
            )
        }
    }
}