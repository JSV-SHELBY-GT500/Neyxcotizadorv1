package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ActiveQuoteDraft
import com.example.data.model.AuditLog
import com.example.data.model.AutoPart
import com.example.data.model.DynamicModule
import com.example.data.model.NotificationItem
import com.example.data.model.OcrPreset
import com.example.data.model.Order
import com.example.data.model.PhotoItem
import com.example.data.model.QuoteState
import com.example.data.model.Vehicle
import com.example.data.model.Warranty
import com.example.data.util.JsvJsonHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JsvRepository(private val db: AppDatabase) {

    // --- Dynamic Modules ---
    val allDynamicModules: Flow<List<DynamicModule>> = db.dynamicModuleDao().getAllModules()
    val enabledDynamicModules: Flow<List<DynamicModule>> = db.dynamicModuleDao().getEnabledModules()

    suspend fun setModuleEnabled(moduleId: String, isEnabled: Boolean) {
        val start = System.currentTimeMillis()
        db.dynamicModuleDao().setModuleEnabled(moduleId, isEnabled)
        logAudit(
            accion = if (isEnabled) "MODULO_HABILITADO" else "MODULO_DESHABILITADO",
            entidad = "ModuloDinamico",
            entidadId = moduleId,
            detalles = "Módulo [$moduleId] fue ${if (isEnabled) "activado" else "desactivado"} en la interfaz dinámica",
            severidad = "INFO",
            categoria = "MODULOS",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun setModuleInstalled(moduleId: String, isInstalled: Boolean) {
        val start = System.currentTimeMillis()
        db.dynamicModuleDao().setModuleInstalled(moduleId, isInstalled)
        logAudit(
            accion = if (isInstalled) "MODULO_DESCARGADO" else "MODULO_DESINSTALADO",
            entidad = "ModuloDinamico",
            entidadId = moduleId,
            detalles = "Módulo [$moduleId] ${if (isInstalled) "instalado/sincronizado remotamente" else "removido del dispositivo"}",
            severidad = "SECURITY",
            categoria = "MODULOS",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    // --- Notifications & Alerts ---
    val allNotifications: Flow<List<NotificationItem>> = db.notificationDao().getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = db.notificationDao().getUnreadCount()

    suspend fun insertNotification(notification: NotificationItem) {
        val start = System.currentTimeMillis()
        db.notificationDao().insertNotification(notification)
        logAudit(
            accion = "NOTIFICACION_EMITIDA",
            entidad = "Notificacion",
            entidadId = notification.id,
            detalles = "Push Alerta generada: ${notification.title} [${notification.category}]",
            severidad = if (notification.priority == "CRITICA" || notification.priority == "ALTA") "WARNING" else "INFO",
            categoria = "NOTIFICACIONES",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun markNotificationAsRead(id: String) {
        db.notificationDao().markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        db.notificationDao().markAllAsRead()
    }

    suspend fun deleteNotification(id: String) {
        db.notificationDao().deleteNotification(id)
    }

    suspend fun clearAllNotifications() {
        db.notificationDao().clearAll()
    }

    // --- OCR Presets ---
    val allOcrPresets: Flow<List<OcrPreset>> = db.ocrPresetDao().getAllPresets()

    suspend fun insertOcrPreset(preset: OcrPreset) {
        val start = System.currentTimeMillis()
        db.ocrPresetDao().insertPreset(preset)
        logAudit(
            accion = "PRESET_OCR_GUARDADO",
            entidad = "PresetOCR",
            entidadId = preset.id,
            detalles = "Preset OCR registrado: ${preset.name} (${preset.documentType})",
            severidad = "INFO",
            categoria = "OCR",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun deleteOcrPreset(id: String) {
        db.ocrPresetDao().deletePreset(id)
    }

    // --- Vehicles ---
    val allVehicles: Flow<List<Vehicle>> = db.vehicleDao().getAllVehicles()

    fun searchVehicles(query: String): Flow<List<Vehicle>> = db.vehicleDao().searchVehicles(query)

    suspend fun insertVehicle(vehicle: Vehicle): Long {
        val start = System.currentTimeMillis()
        val id = db.vehicleDao().insertVehicle(vehicle)
        logAudit(
            accion = "VEHICULO_AGREGADO",
            entidad = "Vehículo",
            entidadId = "${vehicle.marca} ${vehicle.modelo}",
            detalles = "Vehículo agregado al catálogo local: ${vehicle.displayName}",
            severidad = "INFO",
            categoria = "MEMORIA_LOCAL",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
        return id
    }

    suspend fun deleteVehicle(id: Long, name: String) {
        val start = System.currentTimeMillis()
        db.vehicleDao().deleteVehicle(id)
        logAudit(
            accion = "VEHICULO_ELIMINADO",
            entidad = "Vehículo",
            entidadId = id.toString(),
            detalles = "Vehículo eliminado: $name",
            severidad = "WARNING",
            categoria = "MEMORIA_LOCAL",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    // --- Orders ---
    val allOrders: Flow<List<Order>> = db.orderDao().getAllOrders()

    fun getOrdersByStatus(estado: String): Flow<List<Order>> = db.orderDao().getOrdersByStatus(estado)

    fun searchOrders(query: String): Flow<List<Order>> = db.orderDao().searchOrders(query)

    suspend fun insertOrder(order: Order) {
        val start = System.currentTimeMillis()
        db.orderDao().insertOrder(order)
        logAudit(
            accion = "PEDIDO_GUARDADO",
            entidad = "Pedido",
            entidadId = order.id,
            detalles = "Cotización guardada como pedido. Cliente: ${order.clienteNombre.ifBlank { "Sin asignar" }} | Monto: $${order.montoTotal} MXN",
            severidad = "INFO",
            categoria = "USUARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun updateOrderStatus(id: String, newStatus: String, tracking: String, notes: String) {
        val start = System.currentTimeMillis()
        val now = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        db.orderDao().updateOrderStatus(id, newStatus, now, tracking, notes)
        logAudit(
            accion = "ESTADO_ACTUALIZADO",
            entidad = "Pedido",
            entidadId = id,
            detalles = "Estado actualizado a [${newStatus.uppercase()}]. Guía: ${tracking.ifBlank { "N/A" }}",
            severidad = "INFO",
            categoria = "USUARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun deleteOrder(id: String) {
        val start = System.currentTimeMillis()
        db.orderDao().deleteOrder(id)
        logAudit(
            accion = "PEDIDO_ELIMINADO",
            entidad = "Pedido",
            entidadId = id,
            detalles = "Pedido eliminado permanentemente",
            severidad = "WARNING",
            categoria = "USUARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    // --- Warranties ---
    val allWarranties: Flow<List<Warranty>> = db.warrantyDao().getAllWarranties()

    suspend fun insertWarranty(warranty: Warranty) {
        val start = System.currentTimeMillis()
        db.warrantyDao().insertWarranty(warranty)
        logAudit(
            accion = "GARANTIA_EMITIDA",
            entidad = "Garantía",
            entidadId = warranty.id,
            detalles = "Garantía emitida para ${warranty.piezaNombre} (${warranty.tiempoGarantia}). Cliente: ${warranty.clienteNombre}",
            severidad = "INFO",
            categoria = "USUARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun deleteWarranty(id: String) {
        val start = System.currentTimeMillis()
        db.warrantyDao().deleteWarranty(id)
        logAudit(
            accion = "GARANTIA_ELIMINADA",
            entidad = "Garantía",
            entidadId = id,
            detalles = "Póliza de garantía eliminada",
            severidad = "WARNING",
            categoria = "USUARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    // --- Photos ---
    val allPhotos: Flow<List<PhotoItem>> = db.photoDao().getAllPhotos()

    suspend fun insertPhoto(photo: PhotoItem) {
        val start = System.currentTimeMillis()
        db.photoDao().insertPhoto(photo)
        logAudit(
            accion = "FOTO_REGISTRADA",
            entidad = "BancoFotos",
            entidadId = photo.id,
            detalles = "Foto agregada: ${photo.title} (${photo.category})",
            severidad = "INFO",
            categoria = "MEMORIA_LOCAL",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun rotatePhoto(id: String, currentDegrees: Int) {
        val next = (currentDegrees + 90) % 360
        db.photoDao().updateRotation(id, next)
    }

    suspend fun deletePhoto(id: String, title: String) {
        val start = System.currentTimeMillis()
        db.photoDao().deletePhoto(id)
        logAudit(
            accion = "FOTO_ELIMINADA",
            entidad = "BancoFotos",
            entidadId = id,
            detalles = "Foto eliminada: $title",
            severidad = "WARNING",
            categoria = "MEMORIA_LOCAL",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    // --- Full Audit Logs ---
    val allAuditLogs: Flow<List<AuditLog>> = db.auditDao().getRecentLogs(150)
    val totalAuditLogsCount: Flow<Int> = db.auditDao().getTotalLogsCount()
    val memoryAccessCount: Flow<Int> = db.auditDao().getMemoryAccessCount()

    fun searchAuditLogs(query: String): Flow<List<AuditLog>> = db.auditDao().searchLogs(query)
    fun getAuditLogsByCategory(category: String): Flow<List<AuditLog>> = db.auditDao().getLogsByCategory(category)
    fun getAuditLogsBySeverity(severity: String): Flow<List<AuditLog>> = db.auditDao().getLogsBySeverity(severity)

    suspend fun logAudit(
        accion: String,
        entidad: String,
        entidadId: String,
        detalles: String,
        severidad: String = "INFO",
        categoria: String = "SISTEMA",
        memoriaTipo: String = "ROOM_SQLITE",
        tiempoEjecucionMs: Long = 4L
    ) {
        db.auditDao().insertLog(
            AuditLog(
                accion = accion,
                entidad = entidad,
                entidadId = entidadId,
                detalles = detalles,
                severidad = severidad,
                categoria = categoria,
                memoriaTipo = memoriaTipo,
                tiempoEjecucionMs = tiempoEjecucionMs
            )
        )
        db.auditDao().trimLogs()
    }

    suspend fun clearAuditLogs() {
        db.auditDao().clearAllLogs()
    }

    // --- General App Settings & Customization ---
    val appSettingsEntity: Flow<com.example.data.model.AppSettingsEntity?> = db.appSettingsDao().getSettingsFlow()

    suspend fun getAppSettings(): com.example.data.model.AppSettingsEntity? = db.appSettingsDao().getSettings()

    suspend fun saveAppSettings(settings: com.example.data.model.AppSettingsEntity) {
        val start = System.currentTimeMillis()
        db.appSettingsDao().insertOrUpdate(settings)
        logAudit(
            accion = "AJUSTES_ACTUALIZADOS",
            entidad = "ConfiguracionGeneral",
            entidadId = "Settings#1",
            detalles = "Ajustes de interfaz, formatos entregables y preset [${settings.activePresetId}] guardados",
            severidad = "INFO",
            categoria = "SISTEMA",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    // --- Active Draft Quote & Memory Access ---
    val activeDraftState: Flow<QuoteState?> = db.quoteDraftDao().getDraft().map { draft ->
        draft?.let { JsvJsonHelper.jsonToQuoteState(it.jsonState) }
    }

    suspend fun saveActiveDraft(state: QuoteState) {
        val start = System.currentTimeMillis()
        val json = JsvJsonHelper.quoteStateToJson(state)
        db.quoteDraftDao().saveDraft(ActiveQuoteDraft(id = 1, jsonState = json))
        // Log subtle local memory access
        logAudit(
            accion = "ACCESO_MEMORIA_DRAFT",
            entidad = "BorradorActivo",
            entidadId = "Draft#1",
            detalles = "Borrador de cotización auto-guardado en memoria local SQLite (${json.length} bytes)",
            severidad = "MEMORY_ACCESS",
            categoria = "MEMORIA_LOCAL",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    // --- Auto Parts Inventory ---
    val allAutoParts: Flow<List<AutoPart>> = db.autoPartDao().getAllAutoParts()

    fun searchAutoParts(query: String): Flow<List<AutoPart>> = db.autoPartDao().searchAutoParts(query)

    suspend fun insertAutoPart(autoPart: AutoPart): Long {
        val start = System.currentTimeMillis()
        val id = db.autoPartDao().insertAutoPart(autoPart)
        logAudit(
            accion = "AUTOPARTE_AGREGADA",
            entidad = "Autoparte",
            entidadId = "${autoPart.nombre} (${autoPart.numeroParte})",
            detalles = "Autoparte agregada al inventario: ${autoPart.nombre} - Precio: \$${autoPart.precio} - Stock: ${autoPart.stockDisponible}",
            severidad = "INFO",
            categoria = "INVENTARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
        return id
    }

    suspend fun updateAutoPart(autoPart: AutoPart) {
        val start = System.currentTimeMillis()
        db.autoPartDao().updateAutoPart(autoPart)
        logAudit(
            accion = "AUTOPARTE_ACTUALIZADA",
            entidad = "Autoparte",
            entidadId = autoPart.id.toString(),
            detalles = "Autoparte actualizada: ${autoPart.nombre} - Stock: ${autoPart.stockDisponible}",
            severidad = "INFO",
            categoria = "INVENTARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }

    suspend fun deleteAutoPart(id: Long, partName: String) {
        val start = System.currentTimeMillis()
        db.autoPartDao().deleteAutoPart(id)
        logAudit(
            accion = "AUTOPARTE_ELIMINADA",
            entidad = "Autoparte",
            entidadId = id.toString(),
            detalles = "Autoparte eliminada del inventario: $partName",
            severidad = "WARNING",
            categoria = "INVENTARIO",
            memoriaTipo = "ROOM_SQLITE",
            tiempoEjecucionMs = System.currentTimeMillis() - start
        )
    }
}
