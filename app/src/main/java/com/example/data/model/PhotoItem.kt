package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "photos")
data class PhotoItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val filename: String,
    val title: String,
    val category: String = "General", // "Motor", "Transmisión", "Compresor", "Inspección", "General"
    val uriOrPath: String = "",
    val rotationDegrees: Int = 0,
    val widthPx: Int = 300,
    val heightPx: Int = 200,
    val timestamp: Long = System.currentTimeMillis()
)
