package com.vanshika.parkit.admin.di

import android.content.Context
import com.vanshika.parkit.admin.data.repository.BookingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideBookingRepository(
        @ApplicationContext context: Context
    ): BookingRepository {
        return BookingRepository(context)
    }
}