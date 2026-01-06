package com.yourpackage.smartmark.repository

import com.yourpackage.smartmark.data.SavedItem
import com.yourpackage.smartmark.data.SavedItemDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedItemRepository @Inject constructor(
    private val dao: SavedItemDao
) {

    fun getAllItems(): Flow<List<SavedItem>> = dao.getAllItems()

    fun getItemsByFolder(folderName: String): Flow<List<SavedItem>> = dao.getItemsByFolder(folderName)

    fun getItemsByType(type: String): Flow<List<SavedItem>> = dao.getItemsByType(type)

    fun searchItems(query: String): Flow<List<SavedItem>> = dao.searchItems(query)

    suspend fun insert(item: SavedItem) = dao.insert(item)

    suspend fun delete(item: SavedItem) = dao.delete(item)

    suspend fun getFolderCounts(): List<com.yourpackage.smartmark.data.FolderCount> = dao.getFolderCounts()
}
