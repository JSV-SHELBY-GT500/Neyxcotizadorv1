package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dynamic_modules")
data class DynamicModule(
    @PrimaryKey val id: String,          // e.g. "EDITOR", "SEGUIMIENTO", "GARANTIA", "VEHICULOS", "FOTOS", "LINKS", "AUDITORIA", "SCANNER_OCR", "NOTIFICACIONES", "OBD_DIAGNOSTICS", "SAT_INVOICE"
    val name: String,
    val description: String,
    val version: String = "2.4.0",
    val iconName: String = "edit",
    val isInstalled: Boolean = true,
    val isEnabled: Boolean = true,
    val isCore: Boolean = false,
    val sizeKb: Int = 1024,
    val category: String = "Ventas",    // "Ventas", "Logística", "Legal", "IA & Visión", "Sistema", "Diagnóstico"
    val remoteEndpoint: String = "https://api.jsv-autopartes.mx/modules/",
    val lastSync: String = "2026-08-28 07:00",
    val permissions: String = "INTERNET, STORAGE_LOCAL",
    val dependencies: String = "com.example.core"
)
