package com.vanshika.parkit.admin.navigation

import androidx.annotation.DrawableRes
import com.vanshika.parkit.R

sealed class AdminBottomNavItem(
    val route: String,
    @DrawableRes val iconRes: Int
){
    object Home: AdminBottomNavItem("admin_home_screen", R.drawable.baseline_home_24)
    object Issues: AdminBottomNavItem("issues_screen", R.drawable.baseline_warning_24)
    object Analytics: AdminBottomNavItem("analytics_screen", R.drawable.baseline_auto_graph_24)
    object Profile: AdminBottomNavItem("admin_profile_screen", R.drawable.baseline_person_24)
}