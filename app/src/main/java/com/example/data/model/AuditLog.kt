package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val fecha: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    val accion: String,               // e.g. "COTIZACION_CREADA", "ACCESO_MEMORIA_ROOM", "MODULO_ACTIVADO", "OCR_ESCANEADO"
    val entidad: String,              // "Cotización", "Pedido", "Garantía", "Vehículo", "MemoriaLocal", "Módulo", "Notificación", "OCR"
    val entidadId: String,
    val detalles: String,
    val dispositivo: String = "Android Local (JSV Core)",
    val severidad: String = "INFO",    // "INFO", "WARNING", "SECURITY", "MEMORY_ACCESS", "CRITICAL"
    val categoria: String = "SISTEMA", // "MEMORIA_LOCAL", "USUARIO", "SISTEMA", "MODULOS", "NOTIFICACIONES", "OCR"
    val memoriaTipo: String = "ROOM_SQLITE", // "ROOM_SQLITE", "CACHE_RAM", "SHARED_PREFS", "DRAFT_STORE", "N/A"
    val tiempoEjecucionMs: Long = 4L,
    val hashIntegridad: String = UUID.randomUUID().toString().take(12).uppercase()
)
