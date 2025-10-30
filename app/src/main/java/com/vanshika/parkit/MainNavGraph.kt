package com.vanshika.parkit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vanshika.parkit.admin.navigation.AdminMainScreen
import com.vanshika.parkit.app.SplashScreen
import com.vanshika.parkit.authentication.ui.ForgotPasswordScreen
import com.vanshika.parkit.authentication.ui.LoginScreen
import com.vanshika.parkit.authentication.ui.SignupScreen
import com.vanshika.parkit.authentication.viewmodel.AuthenticationViewModel
import com.vanshika.parkit.onboarding.OnBoardingPager
import com.vanshika.parkit.user.navigation.UserMainScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    authViewModel: AuthenticationViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavRoutes.SplashScreen.route, // start with splash
        modifier = modifier
    ) {
        // Splash
        composable(MainNavRoutes.SplashScreen.route) {
            SplashScreen(navHostController = navController, authenticationViewModel = authViewModel)
        }

        // Login
        composable(MainNavRoutes.Login.route) {
            LoginScreen(viewModel = authViewModel, navHostController = navController)
        }

        // Signup
        composable(MainNavRoutes.Signup.route) {
            SignupScreen(viewModel = authViewModel, navHostController = navController)
        }

        // Forgot Password
        composable(MainNavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(viewModel = authViewModel, navHostController = navController)
        }

        // Admin Main (Bottom Nav)
        composable(MainNavRoutes.AdminMain.route) {
            AdminMainScreen(mainNavController = navController)
        }

        // User Main (Bottom Nav)
        composable(MainNavRoutes.UserMain.route) {
            UserMainScreen(
                mainNavController = navController,
                authenticationViewModel = authViewModel
            )
        }

        // OnBoarding
        composable(MainNavRoutes.OnBoarding.route) {
            OnBoardingPager(navController)
        }
    }
}