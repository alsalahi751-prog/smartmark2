package com.yourpackage.smartmark.services

import android.content.Context
import android.util.Log
import com.yourpackage.smartmark.data.SavedDatabase
import com.yourpackage.smartmark.data.SavedItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object BackupManager {

    private const val BACKUP_FILE_NAME = "smartmark_backup.json"

    suspend fun createBackup(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val db = SavedDatabase.getDatabase(context)
                val items = db.savedItemDao().getAllItems().first()

                val jsonArray = JSONArray()
                for (item in items) {
                    val jsonObject = JSONObject().apply {
                        put("title", item.title)
                        put("url", item.url)
                        put("type", item.type)
                        put("localPath", item.localPath)
                        put("folder", item.folder)
                        put("comment", item.comment)
                        put("quality", item.quality)
                        put("timestamp", item.timestamp)
                    }
                    jsonArray.put(jsonObject)
                }

                val jsonString = jsonArray.toString(4)

                val file = File(context.getExternalFilesDir(null), BACKUP_FILE_NAME)
                FileOutputStream(file).use { it.write(jsonString.toByteArray()) }

                Log.d("BackupManager", "Backup created: ${file.absolutePath}")
                true
            } catch (e: Exception) {
                Log.e("BackupManager", "Backup failed: ${e.message}")
                false
            }
        }
    }

    suspend fun restoreBackup(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.getExternalFilesDir(null), BACKUP_FILE_NAME)
                if (!file.exists()) {
                    Log.e("BackupManager", "Backup file not found")
                    return@withContext false
                }

                val jsonString = file.readText()
                val jsonArray = JSONArray(jsonString)

                val db = SavedDatabase.getDatabase(context)
                val dao = db.savedItemDao()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val item = SavedItem(
                        title = jsonObject.getString("title"),
                        url = jsonObject.getString("url"),
                        type = jsonObject.getString("type"),
                        localPath = jsonObject.getString("localPath"),
                        folder = jsonObject.getString("folder"),
                        comment = jsonObject.getString("comment"),
                        quality = jsonObject.getString("quality"),
                        timestamp = jsonObject.getLong("timestamp")
                    )
                    dao.insert(item)
                }

                Log.d("BackupManager", "Backup restored successfully")
                true
            } catch (e: Exception) {
                Log.e("BackupManager", "Restore failed: ${e.message}")
                false
            }
        }
    }
}
