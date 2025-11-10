package com.vanshika.parkit.user.navigation

import android.net.Uri

sealed class UserNavRoutes(val route: String){
    object MakeReservationScreen: UserNavRoutes("make_reservation_screen/{slotId}/{zoneName}"){
        fun createRoute(slotId: String, zoneName: String): String{
            return "make_reservation_screen/$slotId/${Uri.encode(zoneName)}"
        }
    }
    object ReportScreen: UserNavRoutes("report_screen/{slotId}/{zoneName}"){
        fun createRoute(slotId: String, zoneName: String): String{
            return "report_screen/$slotId/${Uri.encode(zoneName)}"
        }
    }
}