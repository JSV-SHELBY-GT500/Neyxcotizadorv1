package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActiveQuoteDraft
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AutoPart
import com.example.data.model.AuditLog
import com.example.data.model.DynamicModule
import com.example.data.model.NotificationItem
import com.example.data.model.OcrPreset
import com.example.data.model.Order
import com.example.data.model.PhotoItem
import com.example.data.model.Vehicle
import com.example.data.model.Warranty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Vehicle::class,
        Order::class,
        Warranty::class,
        PhotoItem::class,
        AuditLog::class,
        ActiveQuoteDraft::class,
        NotificationItem::class,
        DynamicModule::class,
        OcrPreset::class,
        AppSettingsEntity::class,
        AutoPart::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun orderDao(): OrderDao
    abstract fun warrantyDao(): WarrantyDao
    abstract fun photoDao(): PhotoDao
    abstract fun auditDao(): AuditDao
    abstract fun quoteDraftDao(): QuoteDraftDao
    abstract fun notificationDao(): NotificationDao
    abstract fun dynamicModuleDao(): DynamicModuleDao
    abstract fun ocrPresetDao(): OcrPresetDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun autoPartDao(): AutoPartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jsv_studio_database.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            prepopulateData(database)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateData(database: AppDatabase) {
            // 1. Seed default Photo Bank inspection records
            val defaultPhotos = listOf(
                PhotoItem(filename = "transmision_cvt_versa.jpg", title = "Transmisión CVT Nissan Versa", category = "Transmisión"),
                PhotoItem(filename = "motor_completo_16.jpg", title = "Motor 1.6L HR16DE Completo", category = "Motor"),
                PhotoItem(filename = "compresor_ac_original.jpg", title = "Compresor A/C Calsonic OEM", category = "A/C"),
                PhotoItem(filename = "modulo_abs_bosch.jpg", title = "Módulo de Frenos ABS Bosch", category = "Frenos"),
                PhotoItem(filename = "cremallera_electro.jpg", title = "Cremallera Dirección Asistida", category = "Suspensión"),
                PhotoItem(filename = "computadora_ecu_oem.jpg", title = "Computadora Motor ECU", category = "Electrónica")
            )
            database.photoDao().insertAll(defaultPhotos)

            // 3. Seed Dynamic Modules
            val defaultModules = listOf(
                DynamicModule(
                    id = "AUTOPARTES",
                    name = "Inventario de Autopartes",
                    description = "Catálogo robusto de autopartes, precios, stock y números de parte OEM",
                    version = "2.6.0",
                    iconName = "build",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = true,
                    sizeKb = 1200,
                    category = "Ventas",
                    permissions = "LOCAL_STORAGE, SEARCH"
                ),
                DynamicModule(
                    id = "EDITOR",
                    name = "Cotizador Inteligente",
                    description = "Motor de cotización dinámica de autopartes, desglose y snapshot JSON",
                    version = "2.4.0",
                    iconName = "edit",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = true,
                    sizeKb = 1450,
                    category = "Ventas",
                    permissions = "LOCAL_STORAGE, CLIPBOARD"
                ),
                DynamicModule(
                    id = "SEGUIMIENTO",
                    name = "Seguimiento y Pedidos",
                    description = "Trazabilidad de órdenes, guías de paquetería y estados de venta",
                    version = "2.2.1",
                    iconName = "local_shipping",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = true,
                    sizeKb = 980,
                    category = "Logística",
                    permissions = "LOCAL_STORAGE"
                ),
                DynamicModule(
                    id = "GARANTIA",
                    name = "Pólizas de Garantía",
                    description = "Generación de certificados de garantía oficiales JSV con QR y sellos",
                    version = "2.1.0",
                    iconName = "verified_user",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = false,
                    sizeKb = 820,
                    category = "Legal",
                    permissions = "LOCAL_STORAGE, PRINTER"
                ),
                DynamicModule(
                    id = "VEHICULOS",
                    name = "Catálogo Donantes",
                    description = "Base de datos de vehículos en despiece y compatibilidad de motores",
                    version = "1.9.5",
                    iconName = "directions_car",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = false,
                    sizeKb = 640,
                    category = "Ventas",
                    permissions = "LOCAL_STORAGE"
                ),
                DynamicModule(
                    id = "FOTOS",
                    name = "Banco de Inspección",
                    description = "Evidencias visuales de autopartes, números de serie y sellos OEM",
                    version = "2.0.0",
                    iconName = "photo_camera",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = false,
                    sizeKb = 1120,
                    category = "Ventas",
                    permissions = "CAMERA, STORAGE"
                ),
                DynamicModule(
                    id = "LINKS",
                    name = "Smart Links & Precios",
                    description = "Buscador multicanal automático en MercadoLibre, Amazon y Google",
                    version = "1.8.0",
                    iconName = "public",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = false,
                    sizeKb = 420,
                    category = "Ventas",
                    permissions = "INTERNET"
                ),
                DynamicModule(
                    id = "SCANNER_OCR",
                    name = "Escáner OCR & Workflows",
                    description = "Extracción de texto por presets de posición y diagramas de flujo automáticos",
                    version = "3.0.0",
                    iconName = "document_scanner",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = false,
                    sizeKb = 2300,
                    category = "IA & Visión",
                    permissions = "SCREENSHOT_READ, OCR_VISION, FLOW_ENGINE"
                ),
                DynamicModule(
                    id = "AUDITORIA",
                    name = "Centro de Auditoría",
                    description = "Trazabilidad completa de operaciones y accesos a memoria local",
                    version = "2.5.0",
                    iconName = "security",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = true,
                    sizeKb = 780,
                    category = "Sistema",
                    permissions = "AUDIT_FORENSIC, MEMORY_INSPECT"
                ),
                DynamicModule(
                    id = "NOTIFICACIONES",
                    name = "Alertas & Push Hub",
                    description = "Gestor de notificaciones push, avisos de stock y pedidos en tiempo real",
                    version = "2.0.0",
                    iconName = "notifications",
                    isInstalled = true,
                    isEnabled = true,
                    isCore = false,
                    sizeKb = 550,
                    category = "Sistema",
                    permissions = "POST_NOTIFICATIONS, BACKGROUND_SYNC"
                ),
                DynamicModule(
                    id = "OBD_DIAGNOSTICS",
                    name = "Diagnóstico OBD-II",
                    description = "Lector de códigos DTC automotrices para detección de piezas dañadas",
                    version = "1.2.0",
                    iconName = "memory",
                    isInstalled = false,
                    isEnabled = false,
                    isCore = false,
                    sizeKb = 3400,
                    category = "Diagnóstico",
                    permissions = "BLUETOOTH_SCAN, BLUETOOTH_CONNECT"
                ),
                DynamicModule(
                    id = "SAT_INVOICE",
                    name = "Facturación CFDI 4.0",
                    description = "Timbrado fiscal automático de piezas y exportación XML/PDF",
                    version = "1.0.4",
                    iconName = "receipt_long",
                    isInstalled = false,
                    isEnabled = false,
                    isCore = false,
                    sizeKb = 1980,
                    category = "Legal",
                    permissions = "INTERNET, CRYPTO_SIGN"
                )
            )
            database.dynamicModuleDao().insertAll(defaultModules)

            // 4. Seed Default OCR Presets
            val defaultPresets = listOf(
                OcrPreset(
                    id = "preset_ficha_tecnica",
                    name = "Ficha Técnica OEM & Donante",
                    description = "Detecta marca, modelo, motor, número de parte OEM y precio sugerido",
                    documentType = "FICHA_TECNICA",
                    zonesJson = """[
                        {"id":"z1","name":"Encabezado Vehículo","label":"Vehículo Donante","xPercent":0.05,"yPercent":0.08,"widthPercent":0.90,"heightPercent":0.12,"fieldMapping":"donorVehicle","keywords":["NISSAN","CHEVROLET","FORD","TOYOTA","VOLKSWAGEN","MOTOR","CIL"]},
                        {"id":"z2","name":"Pieza & OEM","label":"Autoparte / OEM","xPercent":0.05,"yPercent":0.22,"widthPercent":0.90,"heightPercent":0.15,"fieldMapping":"partName","keywords":["TRANSMISION","MOTOR","COMPRESOR","ECU","MODULO","BOMBA","OEM"]},
                        {"id":"z3","name":"Precios y Desglose","label":"Precio de Venta","xPercent":0.05,"yPercent":0.40,"widthPercent":0.45,"heightPercent":0.15,"fieldMapping":"priceVenta","keywords":["$","PRECIO","VENTA","COSTO","MXN"]},
                        {"id":"z4","name":"Precio a Cambio","label":"Precio Cambio","xPercent":0.52,"yPercent":0.40,"widthPercent":0.43,"heightPercent":0.15,"fieldMapping":"priceCambio","keywords":["CAMBIO","CASCO","RETORNO","DEPOSITO"]},
                        {"id":"z5","name":"Garantía y Notas","label":"Póliza y Términos","xPercent":0.05,"yPercent":0.60,"widthPercent":0.90,"heightPercent":0.20,"fieldMapping":"warranty","keywords":["GARANTIA","DIAS","MESES","ORIGINAL","PROBADO"]}
                    ]""".trimIndent(),
                    triggersJson = "[\"OEM\", \"VENTA\", \"CAMBIO\", \"MOTOR\", \"GARANTIA\", \"DONANTE\"]",
                    outputTemplate = "=== FICHA TECNICA JSV ===\nVEHICULO: {donorVehicle}\nPIEZA: {partName}\nOEM: {partOem}\nVENTA: \${priceVenta}\nCAMBIO: \${priceCambio}\nGARANTIA: {warranty}",
                    isDefault = true
                ),
                OcrPreset(
                    id = "preset_cotizacion_chat",
                    name = "Chat WhatsApp / Cotización",
                    description = "Ideal para capturas de conversaciones con clientes o presupuestos de refaccionarias",
                    documentType = "COTIZACION_CHAT",
                    zonesJson = """[
                        {"id":"zc1","name":"Nombre Cliente","label":"Cliente","xPercent":0.05,"yPercent":0.04,"widthPercent":0.60,"heightPercent":0.08,"fieldMapping":"clientName","keywords":["CLIENTE","ING","SR","LIC","DON"]},
                        {"id":"zc2","name":"Pieza Solicitada","label":"Pieza y Compatibilidad","xPercent":0.05,"yPercent":0.15,"widthPercent":0.90,"heightPercent":0.20,"fieldMapping":"partName","keywords":["BUSCO","COTIZAME","TIENEN","PRECIO","DISPONIBLE"]},
                        {"id":"zc3","name":"Precio Venta","label":"Total / Presupuesto","xPercent":0.10,"yPercent":0.45,"widthPercent":0.80,"heightPercent":0.15,"fieldMapping":"priceVenta","keywords":["$","NETO","TOTAL","DEPOSITO","ANTICIPO"]}
                    ]""".trimIndent(),
                    triggersJson = "[\"COTIZAR\", \"PRECIO\", \"ENVIO\", \"ANTICIPO\", \"DISPONIBLE\"]",
                    outputTemplate = "=== SOLICITUD CLIENTE ===\nCLIENTE: {clientName}\nPIEZA REQUERIDA: {partName}\nPRESUPUESTO ESTIMADO: \${priceVenta}",
                    isDefault = false
                ),
                OcrPreset(
                    id = "preset_ticket_factura",
                    name = "Ticket / Factura de Proveedor",
                    description = "Capturas de notas de mostrador, remisiones y tickets de yonke con desglose",
                    documentType = "TICKET_FACTURA",
                    zonesJson = """[
                        {"id":"zt1","name":"Proveedor Emisor","label":"Emisor / Yonke","xPercent":0.05,"yPercent":0.02,"widthPercent":0.90,"heightPercent":0.10,"fieldMapping":"donorVehicle","keywords":["AUTOPARTES","YONKE","DESHUESADERO","REFACCIONES"]},
                        {"id":"zt2","name":"Concepto","label":"Pieza Detallada","xPercent":0.05,"yPercent":0.20,"widthPercent":0.90,"heightPercent":0.30,"fieldMapping":"partName","keywords":["PZA","UNIDAD","COMPONENTE","NUMERO"]},
                        {"id":"zt3","name":"Total Facturado","label":"Monto Total","xPercent":0.40,"yPercent":0.70,"widthPercent":0.55,"heightPercent":0.15,"fieldMapping":"priceVenta","keywords":["TOTAL","IMPORTE","PAGADO","EFECTIVO"]}
                    ]""".trimIndent(),
                    triggersJson = "[\"TOTAL\", \"FOLIO\", \"IVA\", \"PAGADO\", \"ORIGINAL\"]",
                    outputTemplate = "=== NOTA COMPRA / PROVEEDOR ===\nYONKE: {donorVehicle}\nPIEZA: {partName}\nCOSTO PROVEEDOR: \${priceVenta}",
                    isDefault = false
                )
            )
            database.ocrPresetDao().insertAll(defaultPresets)

            // 5. Seed Initial Notifications
            val defaultNotifications = listOf(
                NotificationItem(
                    title = "📦 Pedido Listo para Envío",
                    message = "El pedido #ORD-1048 (Transmisión Versa) fue marcado como Pagado. Genera la guía de envío.",
                    category = "PEDIDO",
                    priority = "ALTA",
                    actionRoute = "SEGUIMIENTO"
                ),
                NotificationItem(
                    title = "🛡️ Póliza de Garantía por Vencer",
                    message = "La garantía de Motor 1.6L para Beat 2019 vence en 5 días naturales.",
                    category = "GARANTIA",
                    priority = "NORMAL",
                    actionRoute = "GARANTIA"
                ),
                NotificationItem(
                    title = "🔍 Nuevo Preset OCR Disponible",
                    message = "Se ha configurado el Preset 'Ficha Técnica OEM & Donante' para mapeo automatizado de workflows.",
                    category = "OCR",
                    priority = "NORMAL",
                    actionRoute = "SCANNER_OCR"
                ),
                NotificationItem(
                    title = "💾 Memoria Local & Room Sincronizados",
                    message = "Los registros de auditoría forense y caché han sido verificados con hash de integridad SHA-256.",
                    category = "SEGURIDAD",
                    priority = "BAJA",
                    actionRoute = "AUDITORIA"
                )
            )
            database.notificationDao().insertAll(defaultNotifications)

            // 5b. Seed Default Auto Parts (Inventario Robusto)
            val defaultAutoParts = listOf(
                AutoPart(
                    nombre = "Transmisión Automática CVT",
                    numeroParte = "31020-3JX0A",
                    precio = 11500.0,
                    precioCambio = 9500.0,
                    stockDisponible = 2,
                    categoria = "Transmisión",
                    marcaVehiculo = "Nissan",
                    modeloVehiculo = "Versa",
                    aniosVehiculo = "2015-2019",
                    motorVehiculo = "1.6L 4 CIL",
                    condicion = "OEM Original Seminuevo",
                    ubicacionAlmacen = "Estante A-1, Bodega Central",
                    notas = "Probado en banco, incluye turbina."
                ),
                AutoPart(
                    nombre = "Motor 1.6L HR16DE Completo",
                    numeroParte = "HR16-NISSAN-01",
                    precio = 24500.0,
                    precioCambio = 21000.0,
                    stockDisponible = 1,
                    categoria = "Motor",
                    marcaVehiculo = "Nissan",
                    modeloVehiculo = "Sentra / Versa",
                    aniosVehiculo = "2013-2019",
                    motorVehiculo = "1.6L / 1.8L",
                    condicion = "Baja Definitiva Nacional",
                    ubicacionAlmacen = "Estante B-2, Bodega Central",
                    notas = "Con compresor y alternador incluidos."
                ),
                AutoPart(
                    nombre = "Compresor de A/C Calsonic",
                    numeroParte = "92600-3AN0A",
                    precio = 3200.0,
                    precioCambio = 2700.0,
                    stockDisponible = 4,
                    categoria = "A/C",
                    marcaVehiculo = "Nissan",
                    modeloVehiculo = "Versa / March",
                    aniosVehiculo = "2012-2020",
                    motorVehiculo = "1.6L",
                    condicion = "OEM Original Probado",
                    ubicacionAlmacen = "Estante C-1",
                    notas = "Excelente compresión, sin fugas."
                ),
                AutoPart(
                    nombre = "Módulo de Frenos ABS Bosch",
                    numeroParte = "47660-3SH0A",
                    precio = 4100.0,
                    precioCambio = 3500.0,
                    stockDisponible = 3,
                    categoria = "Frenos",
                    marcaVehiculo = "Nissan",
                    modeloVehiculo = "Sentra",
                    aniosVehiculo = "2013-2017",
                    motorVehiculo = "1.8L",
                    condicion = "Garantizado 100%",
                    ubicacionAlmacen = "Estante D-4",
                    notas = "Escaneado sin códigos de falla DTC."
                ),
                AutoPart(
                    nombre = "Cremallera de Dirección Electroasistida",
                    numeroParte = "48001-4BA0A",
                    precio = 5500.0,
                    precioCambio = 4800.0,
                    stockDisponible = 2,
                    categoria = "Suspensión",
                    marcaVehiculo = "Nissan",
                    modeloVehiculo = "X-Trail / Rogue",
                    aniosVehiculo = "2014-2018",
                    motorVehiculo = "2.5L",
                    condicion = "OEM Original",
                    ubicacionAlmacen = "Estante E-2",
                    notas = "Cople y piñón sin juego excesivo."
                ),
                AutoPart(
                    nombre = "Computadora Motor ECU",
                    numeroParte = "23710-1KT7B",
                    precio = 6800.0,
                    precioCambio = 5900.0,
                    stockDisponible = 1,
                    categoria = "Electrónica",
                    marcaVehiculo = "Volkswagen",
                    modeloVehiculo = "Jetta MK6",
                    aniosVehiculo = "2011-2015",
                    motorVehiculo = "2.5L 5 CIL",
                    condicion = "Liberada y Probada",
                    ubicacionAlmacen = "Estante Electrónica 1",
                    notas = "Lista para programar immo."
                )
            )
            database.autoPartDao().insertAll(defaultAutoParts)

            // 6. Seed Initial Audit Record with enhanced fields
            database.auditDao().insertLog(
                AuditLog(
                    accion = "INICIALIZACION_SISTEMA",
                    entidad = "Sistema",
                    entidadId = "JSV-CORE-V2.5",
                    detalles = "Base de datos Room v2 inicializada. Tablas de Auditoría, Módulos Dinámicos, Notificaciones y OCR Presets activas.",
                    severidad = "INFO",
                    categoria = "SISTEMA",
                    memoriaTipo = "ROOM_SQLITE",
                    tiempoEjecucionMs = 8L
                )
            )
        }
    }
}
