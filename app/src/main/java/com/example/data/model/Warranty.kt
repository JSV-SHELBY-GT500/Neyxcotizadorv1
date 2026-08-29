package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class WarrantyPoint(
    val id: String = UUID.randomUUID().toString(),
    val concepto: String,
    val descripcion: String,
    val foto: String = "",
    val verificado: Boolean = true
)

data class WarrantySection(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val contenido: String
)

@Entity(tableName = "warranties")
data class Warranty(
    @PrimaryKey val id: String = "GAR-" + SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault()).format(Date()) + "-" + UUID.randomUUID().toString().take(4).uppercase(),
    val orderId: String = "",
    val fecha: String = defaultFormattedDate(),
    val presetActivo: String = "general", // "general", "compresor", "transmision", "personalizado"
    val tiempoGarantia: String = "30 días naturales",
    val terminos: String = "La garantía cubre defectos internos de funcionamiento y ensamble. No aplica en caso de alteración de sellos, negligencia en la instalación, falta de lubricante o daño eléctrico.",
    val marcaAgua: String = "JSV-AUTOPARTES",
    val marcaAguaOpacidad: Float = 0.15f,
    val mostrarTiempo: Boolean = true,
    val mostrarTerminos: Boolean = true,
    val mostrarMarcaAgua: Boolean = true,

    // Cliente
    val clienteNombre: String = "",
    val clienteTelefono: String = "",
    val clienteDireccion: String = "",
    val mostrarCliente: Boolean = true,

    // Pieza
    val piezaNombre: String = "",
    val piezaMarca: String = "",
    val piezaModelo: String = "",
    val piezaNumeroParte: String = "",
    val piezaIdSerial: String = "",
    val piezaFoto: String = "",
    val mostrarPieza: Boolean = true,

    // Pieza a cambio
    val cambioNombre: String = "",
    val cambioDescripcion: String = "",
    val cambioFoto: String = "",
    val mostrarCambio: Boolean = false,

    // Puntos y secciones
    val puntosJson: String = "",
    val seccionesJson: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
