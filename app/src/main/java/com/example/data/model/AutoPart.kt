package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autoparts")
data class AutoPart(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val numeroParte: String, // OEM or reference part number
    val precio: Double,
    val precioCambio: Double = 0.0,
    val stockDisponible: Int = 1,
    val categoria: String, // Transmisión, Motor, Suspensión, Frenos, Electrónica, A/C, etc.
    val marcaVehiculo: String = "",
    val modeloVehiculo: String = "",
    val aniosVehiculo: String = "",
    val motorVehiculo: String = "",
    val condicion: String = "OEM Original Seminuevo",
    val ubicacionAlmacen: String = "Torreón / Gómez Palacio / Lerdo",
    val proveedor: String = "Desarme Nacional Certificado",
    val notas: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayTitle: String
        get() = "$nombre ($numeroParte)"

    val displayVehicle: String
        get() = "$marcaVehiculo $modeloVehiculo ${if (aniosVehiculo.isNotBlank()) "[$aniosVehiculo]" else ""}".trim()

    val formattedPrice: String
        get() = "$${String.format(java.util.Locale.getDefault(), "%,.2f", precio)} MXN"

    val isAvailable: Boolean
        get() = stockDisponible > 0
}
