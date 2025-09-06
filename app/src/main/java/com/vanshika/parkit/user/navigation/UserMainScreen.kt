package com.vanshika.parkit.user.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel

@Composable
fun UserMainScreen(
    mainNavController: NavHostController,
    authenticationViewModel: AuthenticationViewModel
) {
    val navHostController = rememberNavController()
    val items = listOf(
        UserBottomNavItem.Home,
        UserBottomNavItem.MyBookings,
        UserBottomNavItem.Notification,
        UserBottomNavItem.Profile
    )
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Black,
            darkIcons = false
        )
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
                tonalElevation = 8.dp
            ) {

                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { items ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                items.icon,
                                contentDescription = "",
                                tint = if (currentRoute == items.route)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.6f)
                            )
                        },
                        selected = currentRoute == items.route,
                        onClick = {
                            if (currentRoute != items.route) {
                                navHostController.navigate(items.route) {
                                    popUpTo(navHostController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White,
                            indicatorColor = Color.DarkGray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        UserNavigationGraph(
            navHostController = navHostController,
            rootNavController = mainNavController,
            modifier = Modifier.padding(paddingValues),
            authenticationViewModel = authenticationViewModel
        )
    }
}