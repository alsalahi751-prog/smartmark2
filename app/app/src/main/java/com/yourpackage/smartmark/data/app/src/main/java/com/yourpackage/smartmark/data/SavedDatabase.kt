package com.yourpackage.smartmark.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [SavedItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun savedItemDao(): SavedItemDao

    companion object {
        @Volatile
        private var INSTANCE: SavedDatabase? = null

        fun getDatabase(context: Context): SavedDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedDatabase::class.java,
                    "saved_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
