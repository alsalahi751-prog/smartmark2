package com.yourpackage.smartmark.di

import com.yourpackage.smartmark.data.SavedItemDao
import com.yourpackage.smartmark.repository.SavedItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSavedItemRepository(dao: SavedItemDao): SavedItemRepository {
        return SavedItemRepository(dao)
    }
}
