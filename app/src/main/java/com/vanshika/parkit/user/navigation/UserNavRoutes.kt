package com.vanshika.parkit.user.navigation

import android.net.Uri

sealed class UserNavRoutes(val route: String){
    object DirectionScreen: UserNavRoutes("direction_screen/{slotId}/{zoneName}"){
        fun createRoute(slotId: String, zoneName: String): String{
            return "direction_screen/$slotId/${Uri.encode(zoneName)}"
        }
    }
    object ReportScreen: UserNavRoutes("report_screen/{slotId}/{zoneName}"){
        fun createRoute(slotId: String, zoneName: String): String{
            return "report_screen/$slotId/${Uri.encode(zoneName)}"
        }
    }
}