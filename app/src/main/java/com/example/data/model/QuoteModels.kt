package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class BreakdownItem(
    val id: String = UUID.randomUUID().toString(),
    val concept: String = "Autoparte OEM",
    val qty: String = "1",
    val condition: String = "OEM Original",
    val priceVenta: Double = 0.0,
    val priceCambio: Double = 0.0,
    val imgFilename: String = ""
)

data class QuoteOption(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Opción Original",
    val price: Double = 0.0,
    val description: String = "Excelente estado físico y operacional, verificado en banco de prueba.",
    val condition: String = "Seminuevo / OEM",
    val delivery: String = "3-5 días hábiles",
    val warranty: String = "30 días por escrito",
    val imgFilename: String = ""
)

data class OrderProcessStep(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val marked: Boolean = false
)

data class QuoteState(
    val quoteType: String = "stock_local", // "stock_local" | "sobre_pedido"
    val brandTitle: String = "JSV-AUTOPARTES",
    val brandSub: String = "COMPRA VENTA DE AUTOPARTES ORIGINALES",
    val secTitle1: String = "1. FICHA DEL VEHÍCULO DONANTE Y COMPATIBILIDAD",
    val secTitle2: String = "2. INSPECCIÓN VISUAL Y ESPECIFICACIONES",
    val secTitle3: String = "3. OPCIONES DE CONFIGURACIÓN Y PRECIOS",
    val secTitle4: String = "4. LOGÍSTICA Y GARANTÍA",
    val docTitle: String = "COTIZACIÓN OFICIAL",
    val docSubtitle: String = "",
    val folioRef: String = "COT-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).substring(2) + "-01",
    val dateStr: String = defaultFormattedDate(),
    val primaryColor: String = "#FCB813",
    val darkColor: String = "#1E2229",
    val pageMode: String = "continuous",
    val porcentajeComision: Double = 15.0,
    val isNewPart: Boolean = true,
    val purchaseMode: String = "gestion_jsv", // "gestion_jsv" (Gestión Integral JSV) | "compra_en_linea" (Compra en Línea Directa)
    val recipientVehicle: String = "Toyota Hilux 2.7 2022",
    val liquidationPolicy: String = "Liquidación al 100% al confirmar el pedido especial",

    // Visibility toggles
    val showSecTitle1: Boolean = true,
    val showSecTitle2: Boolean = true,
    val showSecTitle3: Boolean = true,
    val showSecTitle4: Boolean = true,
    val showDonorVehicle: Boolean = true,
    val showYearRange: Boolean = true,
    val showEngineRange: Boolean = true,
    val showItemLocation: Boolean = true,
    val showPartName: Boolean = true,
    val showPartOem: Boolean = true,
    val showPartEng: Boolean = false,
    val showPartTrans: Boolean = true,
    val showPartCondition: Boolean = true,
    val showPartOrigin: Boolean = true,
    val showTechVerify: Boolean = true,
    val showImageSection: Boolean = true,
    val showBreakdownTable: Boolean = true,
    val showLogistics: Boolean = true,
    val showGuarantee: Boolean = true,
    val showItemPhotos: Boolean = true,
    val showPlaceholderText: Boolean = true,
    val showImgCaption: Boolean = true,

    // Donor & Specs
    val donorVehicle: String = "Nissan Versa Advance",
    val yearRange: String = "2015 - 2019",
    val engineRange: String = "1.6L 4 CIL Gasolina",
    val itemLocation: String = "Torreón / Gómez Palacio / Lerdo",
    val partName: String = "Transmisión Automática CVT",
    val partOem: String = "31020-3JX0A",
    val partEng: String = "RE0F11A",
    val partSystem: String = "Transmisión y Tren Motriz",
    val partCondition: String = "Original Seminueva 100% Funcional",
    val partOrigin: String = "Vehículo Donante Nacional (Baja Definitiva)",
    val techVerifyNote: String = "Se recomienda verificar compatibilidad con número de serie (VIN) y realizar cambio de fluido y cedazo al momento de la instalación.",
    val imgStockLocal: String = "",
    val imgCaptionText: String = "Soporte visual de estado físico, números de marcado y sellos de garantía.",

    // Items and Options
    val breakdownItems: List<BreakdownItem> = listOf(
        BreakdownItem(
            concept = "Transmisión Completa con Turbina OEM",
            qty = "1",
            condition = "OEM Original",
            priceVenta = 11500.0,
            priceCambio = 9500.0
        )
    ),
    val options: List<QuoteOption> = listOf(
        QuoteOption(
            title = "Opción Grado A - Importación Directa",
            price = 12500.0,
            description = "Bajo kilometraje certificado, probado en banco computarizado.",
            condition = "Grado A / Como Nuevo",
            delivery = "2 a 4 días hábiles",
            warranty = "30 días de garantía directa"
        ),
        QuoteOption(
            title = "Opción Grado B - Nacional Certificado",
            price = 10500.0,
            description = "Funcionamiento óptimo garantizado con sellos de inviolabilidad.",
            condition = "OEM Recuperado",
            delivery = "Inmediata / 24 hrs",
            warranty = "15 días de garantía directa"
        )
    ),

    // Logistics & Guarantee
    val logisticsTitle: String = "📍 SERVICIO A DOMICILIO GRATIS",
    val logisticsText: String = "Entrega personal y gratuita en área local (Torreón, Gómez Palacio y Lerdo).\nEnvíos foráneos disponibles por paqueterías líderes (Castores, Tres Guerras, DHL) con guía rastreable.",
    val guaranteeTitle: String = "GARANTÍA DIRECTA Y RESPALDO JSV",
    val guaranteeText: String = "30 días naturales de garantía por escrito contra cualquier defecto interno o de funcionamiento. Sellos de seguridad intactos.",

    // Order Process
    val procesoTituloSeccion: String = "📋 Proceso de Pedido",
    val procesoMostrar: Boolean = true,
    val procesoMostrarTitulo: Boolean = true,
    val procesoMostrarNumeros: Boolean = true,
    val procesoPasos: List<OrderProcessStep> = listOf(
        OrderProcessStep(title = "Verificación del Producto", description = "Se envían fotos a detalle, números de serie y especificaciones para tu validación previa.", marked = true),
        OrderProcessStep(title = "Pago del Pedido", description = "Se proporciona cuenta CLABE bancaria autorizada para la liquidación o anticipo pactado.", marked = true),
        OrderProcessStep(title = "Recepción e Inspección", description = "Revisamos minuciosamente la pieza en nuestro taller técnico antes de la salida.", marked = false),
        OrderProcessStep(title = "Entrega Personal a Domicilio", description = "Te entregamos la pieza directamente en tu taller o domicilio con póliza de garantía.", marked = false),
        OrderProcessStep(title = "Garantía y Acompañamiento", description = "30 días de cobertura total con reemplazo o reembolso en caso necesario.", marked = false)
    )
) {
    fun calculateTotalStockVenta(): Double = breakdownItems.sumOf { it.priceVenta * (it.qty.toDoubleOrNull() ?: 1.0) }
    fun calculateTotalStockCambio(): Double = breakdownItems.sumOf { it.priceCambio * (it.qty.toDoubleOrNull() ?: 1.0) }
    fun calculateTotalSobrePedidoBase(): Double = options.sumOf { it.price }
    fun calculateSobrePedidoComision(): Double = calculateTotalSobrePedidoBase() * (porcentajeComision / 100.0)
    fun calculateTotalSobrePedidoFinal(): Double = calculateTotalSobrePedidoBase() + calculateSobrePedidoComision()

    fun getEffectiveTotal(): Double = if (quoteType == "stock_local") calculateTotalStockVenta() else calculateTotalSobrePedidoFinal()
}

fun defaultFormattedDate(): String {
    val sdf = SimpleDateFormat("dd 'DE' MMMM 'DE' yyyy", Locale("es", "ES"))
    return sdf.format(Date()).uppercase()
}
