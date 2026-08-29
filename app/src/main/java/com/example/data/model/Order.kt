package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String = generateOrderId(),
    val fechaCreacion: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
    val fechaActualizacion: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
    val estado: String = "cotizado", // "cotizado", "pagado", "envio", "entregado", "cancelado"
    val titulo: String,
    val vehiculo: String,
    val pieza: String,
    val clienteNombre: String = "",
    val clienteTelefono: String = "",
    val clienteDireccion: String = "",
    val guiaRastreo: String = "",
    val fechaEnvio: String = "",
    val fechaEntrega: String = "",
    val observaciones: String = "",
    val montoTotal: Double = 0.0,
    val cotizacionJson: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

fun generateOrderId(): String {
    val datePart = SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault()).format(Date())
    val rnd = UUID.randomUUID().toString().substring(0, 4).uppercase()
    return "PED-$datePart-$rnd"
}
