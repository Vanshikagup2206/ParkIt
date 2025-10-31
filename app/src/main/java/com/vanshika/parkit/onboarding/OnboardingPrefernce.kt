package com.vanshika.parkit.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "onBoardingPrefs")

object OnBoardingPreference {
    private val ON_BOARDING_SHOWN = booleanPreferencesKey("onBoardingShown")

    fun setOnBoardingShown(context: Context) {
        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[ON_BOARDING_SHOWN] = true
            }
        }
    }

    fun isOnBoardingShown(context: Context): Boolean {
        return runBlocking {
            val prefs = context.dataStore.data.first()
            prefs[ON_BOARDING_SHOWN] ?: false
        }
    }
}