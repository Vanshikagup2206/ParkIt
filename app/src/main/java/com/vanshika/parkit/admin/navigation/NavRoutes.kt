package com.vanshika.parkit.admin.navigation

import android.net.Uri
import com.vanshika.parkit.admin.screen.home.SlotStatus

sealed class NavRoutes(val route: String){
    object BookingPage : NavRoutes("booking_page/{slotId}/{zoneName}/{status}") {
        fun createRoute(slotId: String, zoneName: String, status: SlotStatus): String {
            return "booking_page/$slotId/${Uri.encode(zoneName)}/${status.name}"
        }
    }
    object ReserveBookingPage : NavRoutes("reserve_booking_page/{slotId}/{zoneName}/{status}"){
        fun createRoute(slotId: String, zoneName: String, status: SlotStatus): String{
            return "reserve_booking_page/$slotId/${Uri.encode(zoneName)}/${status.name}"
        }
    }

    object IssueDetail : NavRoutes("issue_detail/{issueId}") {
        fun createRoute(issueId: String): String {
            return "issue_detail/$issueId"
        }
    }
}
