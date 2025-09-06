package com.vanshika.parkit.admin.di

import com.vanshika.parkit.admin.data.repository.BookingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideBookingRepository(): BookingRepository {
        return BookingRepository()
    }
}