package com.yourpackage.smartmark.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedItemDao {

    @Query("SELECT * FROM saved_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<SavedItem>>

    @Query("SELECT * FROM saved_items WHERE folder = :folderName ORDER BY timestamp DESC")
    fun getItemsByFolder(folderName: String): Flow<List<SavedItem>>

    @Query("SELECT * FROM saved_items WHERE type = :type ORDER BY timestamp DESC")
    fun getItemsByType(type: String): Flow<List<SavedItem>>

    @Query("""
        SELECT * FROM saved_items 
        WHERE title LIKE '%' || :query || '%' 
           OR url LIKE '%' || :query || '%' 
           OR folder LIKE '%' || :query || '%' 
           OR comment LIKE '%' || :query || '%' 
        ORDER BY 
            CASE 
                WHEN title LIKE :query || '%' THEN 1
                WHEN title LIKE '%' || :query || '%' THEN 2
                WHEN folder LIKE :query || '%' THEN 3
                WHEN folder LIKE '%' || :query || '%' THEN 4
                ELSE 5
            END,
            timestamp DESC
    """)
    fun searchItems(query: String): Flow<List<SavedItem>>

    @Insert
    suspend fun insert(item: SavedItem)

    @Delete
    suspend fun delete(item: SavedItem)

    @Query("SELECT folder, COUNT(*) as count FROM saved_items GROUP BY folder")
    suspend fun getFolderCounts(): List<FolderCount>
}

data class FolderCount(
    val folder: String,
    val count: Int
)
