package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val marca: String,
    val modelo: String,
    val anios: String,
    val motor: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = "$marca $modelo ${if (anios.isNotBlank()) "($anios)" else ""} ${if (motor.isNotBlank()) "- $motor" else ""}".trim()
}
