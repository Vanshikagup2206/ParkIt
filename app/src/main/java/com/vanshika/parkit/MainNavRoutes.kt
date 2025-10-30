package com.vanshika.parkit

sealed class MainNavRoutes (val route: String){
    object SplashScreen: MainNavRoutes("splash_screen")
    object Login: MainNavRoutes("login")
    object Signup: MainNavRoutes("signup")
    object ForgotPassword: MainNavRoutes("forgot_password_screen")
    object OnBoarding: MainNavRoutes("on_boarding")

    object AdminMain: MainNavRoutes("admin_main")
    object UserMain: MainNavRoutes("user_main")
}