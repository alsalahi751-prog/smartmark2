package com.yourpackage.smartmark.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "saved_items",
    indices = [
        Index(value = ["folder"]),
        Index(value = ["type"]),
        Index(value = ["timestamp"]),
        Index(value = ["title"]),
        Index(value = ["url"])
    ]
)
data class SavedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val url: String,
    val type: String, // "image", "video", "other"
    val localPath: String,
    val folder: String = "General",
    val comment: String = "",
    val quality: String = "SD",
    val timestamp: Long = System.currentTimeMillis()
)
