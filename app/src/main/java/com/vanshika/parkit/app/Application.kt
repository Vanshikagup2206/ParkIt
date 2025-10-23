package com.vanshika.parkit.app

import android.app.Application
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ParkItApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Init OneSignal
        OneSignal.initWithContext(this, "531eda43-b91a-4e09-b931-0bd569b034e9")
    }
}