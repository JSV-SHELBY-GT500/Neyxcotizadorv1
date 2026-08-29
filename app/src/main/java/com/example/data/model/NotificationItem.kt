package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val category: String = "SISTEMA", // "PEDIDO", "GARANTIA", "STOCK", "SEGURIDAD", "SISTEMA", "OCR", "WORKFLOW"
    val timestamp: String = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault()).format(Date()),
    val isRead: Boolean = false,
    val priority: String = "NORMAL",  // "BAJA", "NORMAL", "ALTA", "CRITICA"
    val actionRoute: String = "",     // e.g. "EDITOR", "SEGUIMIENTO", "GARANTIA", "SCANNER_OCR", "AUDITORIA"
    val metadataJson: String = "{}"
)
