package com.yourpackage.smartmark.di

import android.content.Context
import com.yourpackage.smartmark.data.SavedDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): SavedDatabase {
        return SavedDatabase.getDatabase(context)
    }

    @Provides
    fun provideSavedItemDao(database: SavedDatabase) = database.savedItemDao()
}
