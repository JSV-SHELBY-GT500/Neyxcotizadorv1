package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AppThemeConfig
import com.example.data.model.AuditLog
import com.example.data.model.AutoPart
import com.example.data.model.BreakdownItem
import com.example.data.model.DeliverableFormatConfig
import com.example.data.model.DynamicModule
import com.example.data.model.EfficiencyConfig
import com.example.data.model.NodeStatus
import com.example.data.model.NotificationItem
import com.example.data.model.OcrExtractedData
import com.example.data.model.OcrKeyZone
import com.example.data.model.OcrPreset
import com.example.data.model.Order
import com.example.data.model.OrderProcessStep
import com.example.data.model.PhotoItem
import com.example.data.model.QuoteOption
import com.example.data.model.QuoteState
import com.example.data.model.UiPanelModule
import com.example.data.model.UiPresetType
import com.example.data.model.Vehicle
import com.example.data.model.Warranty
import com.example.data.model.WorkflowExecutionState
import com.example.data.model.WorkflowNode
import com.example.data.model.WorkflowNodeType
import com.example.data.repository.JsvRepository
import com.example.data.util.JsvJsonHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MetricsData(
    val totalQuotes: Int = 0,
    val deliveredCount: Int = 0,
    val deliveredPercent: Float = 0f,
    val totalRevenue: Double = 0.0,
    val topModel: String = "N/A",
    val topPiece: String = "N/A",
    val statusCounts: Map<String, Int> = emptyMap()
)

data class SmartSearchLinks(
    val googleImages: String,
    val googleWeb: String,
    val mercadoLibre: String,
    val amazon: String,
    val youtube: String,
    val facebook: String,
    val queryDisplay: String
)

enum class JsvTab(val moduleId: String, val label: String, val iconName: String) {
    AUTOPARTES("AUTOPARTES", "Autopartes", "build"),
    EDITOR("EDITOR", "Cotizador", "edit"),
    SEGUIMIENTO("SEGUIMIENTO", "Pedidos", "local_shipping"),
    GARANTIA("GARANTIA", "Garantías", "verified_user"),
    VEHICULOS("VEHICULOS", "Vehículos", "directions_car"),
    FOTOS("FOTOS", "Banco Fotos", "photo_camera"),
    LINKS("LINKS", "Smart Links", "public"),
    SCANNER_OCR("SCANNER_OCR", "Escáner OCR", "document_scanner"),
    AUDITORIA("AUDITORIA", "Auditoría", "security"),
    MODULOS("MODULOS", "Módulos", "extension"),
    NOTIFICACIONES("NOTIFICACIONES", "Alertas", "notifications"),
    AJUSTES("AJUSTES", "Ajustes & Canvas", "settings")
}

data class SampleScreenshot(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val presetId: String,
    val simulatedImageText: String,
    val detectedData: OcrExtractedData
)

class JsvViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JsvRepository(AppDatabase.getInstance(application))

    // Dynamic Modules
    val allDynamicModules = repository.allDynamicModules.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val enabledDynamicModules = repository.enabledDynamicModules.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(JsvTab.EDITOR)
    val currentTab: StateFlow<JsvTab> = _currentTab.asStateFlow()

    fun selectTab(tab: JsvTab) {
        _currentTab.value = tab
        viewModelScope.launch {
            repository.logAudit(
                accion = "NAVEGACION_TAB",
                entidad = "Interfaz",
                entidadId = tab.name,
                detalles = "Usuario navegó a la pantalla [${tab.label}]",
                severidad = "INFO",
                categoria = "USUARIO",
                memoriaTipo = "RAM_CACHE"
            )
        }
    }

    // Notifications & Alerts
    val allNotifications = repository.allNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Auto Parts Inventory ---
    val autoPartSearchQuery = MutableStateFlow("")
    val autoPartSelectedCategory = MutableStateFlow("Todas")

    val allAutoParts = repository.allAutoParts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredAutoParts = combine(allAutoParts, autoPartSearchQuery, autoPartSelectedCategory) { parts, query, category ->
        parts.filter { part ->
            val matchesCategory = category == "Todas" || part.categoria.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    part.nombre.contains(query, ignoreCase = true) ||
                    part.numeroParte.contains(query, ignoreCase = true) ||
                    part.categoria.contains(query, ignoreCase = true) ||
                    part.modeloVehiculo.contains(query, ignoreCase = true) ||
                    part.marcaVehiculo.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setAutoPartSearchQuery(q: String) {
        autoPartSearchQuery.value = q
    }

    fun setAutoPartCategory(cat: String) {
        autoPartSelectedCategory.value = cat
    }

    fun addAutoPart(part: AutoPart) {
        viewModelScope.launch {
            repository.insertAutoPart(part)
            _userMessage.value = "Autoparte '${part.nombre}' agregada al inventario."
        }
    }

    fun updateAutoPart(part: AutoPart) {
        viewModelScope.launch {
            repository.updateAutoPart(part)
            _userMessage.value = "Autoparte '${part.nombre}' actualizada."
        }
    }

    fun deleteAutoPart(id: Long, name: String) {
        viewModelScope.launch {
            repository.deleteAutoPart(id, name)
            _userMessage.value = "Autoparte '$name' eliminada."
        }
    }

    val unreadNotificationsCount = repository.unreadNotificationsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            _userMessage.value = "Todas las alertas marcadas como leídas."
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            _userMessage.value = "Historial de alertas limpiado."
        }
    }

    fun triggerTestPushNotification(category: String, title: String, message: String, priority: String = "NORMAL", route: String = "") {
        viewModelScope.launch {
            val item = NotificationItem(
                title = title,
                message = message,
                category = category,
                priority = priority,
                actionRoute = route
            )
            repository.insertNotification(item)
            _userMessage.value = "🔔 Alerta push recibida: $title"
        }
    }

    // Active Quote State
    private val _quoteState = MutableStateFlow(QuoteState())
    val quoteState: StateFlow<QuoteState> = _quoteState.asStateFlow()

    // Transient UI message / toast trigger
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- APP SETTINGS, MODULAR CANVAS & FORMAT PRESETS ---
    private val _activeUiPreset = MutableStateFlow(UiPresetType.DASHBOARD_PRO)
    val activeUiPreset: StateFlow<UiPresetType> = _activeUiPreset.asStateFlow()

    private val _customPresetName = MutableStateFlow("Mi Canvas Personalizado")
    val customPresetName: StateFlow<String> = _customPresetName.asStateFlow()

    private val _modularPanels = MutableStateFlow(defaultModularPanels())
    val modularPanels: StateFlow<List<UiPanelModule>> = _modularPanels.asStateFlow()

    private val _appThemeConfig = MutableStateFlow(AppThemeConfig())
    val appThemeConfig: StateFlow<AppThemeConfig> = _appThemeConfig.asStateFlow()

    private val _deliverableFormatConfig = MutableStateFlow(DeliverableFormatConfig())
    val deliverableFormatConfig: StateFlow<DeliverableFormatConfig> = _deliverableFormatConfig.asStateFlow()

    private val _efficiencyConfig = MutableStateFlow(EfficiencyConfig())
    val efficiencyConfig: StateFlow<EfficiencyConfig> = _efficiencyConfig.asStateFlow()

    init {
        // Load saved settings from database
        viewModelScope.launch {
            repository.appSettingsEntity.collect { entity ->
                if (entity != null) {
                    _activeUiPreset.value = UiPresetType.entries.find { it.id == entity.activePresetId } ?: UiPresetType.DASHBOARD_PRO
                    _customPresetName.value = entity.customPresetName
                    if (entity.panelsJson.isNotBlank()) {
                        val parsed = parsePanelsJson(entity.panelsJson)
                        if (parsed.isNotEmpty()) {
                            _modularPanels.value = parsed
                        }
                    }
                    _appThemeConfig.value = AppThemeConfig(
                        primaryAccentHex = entity.themeAccentHex,
                        backgroundMode = entity.themeBackgroundMode,
                        uiDensity = entity.themeUiDensity,
                        fontScale = entity.themeFontScale,
                        hapticFeedback = entity.themeHaptic
                    )
                    _deliverableFormatConfig.value = DeliverableFormatConfig(
                        brandTitle = entity.formatBrandTitle,
                        brandSub = entity.formatBrandSub,
                        brandFont = entity.formatBrandFont,
                        brandColorHex = entity.formatBrandColorHex,
                        headerBgHex = entity.formatHeaderBgHex,
                        watermarkText = entity.formatWatermarkText,
                        watermarkOpacity = entity.formatWatermarkOpacity,
                        watermarkAngle = entity.formatWatermarkAngle,
                        watermarkEnabled = entity.formatWatermarkEnabled,
                        watermarkStyle = entity.formatWatermarkStyle,
                        sealType = entity.formatSealType,
                        footerPhone = entity.formatFooterPhone,
                        footerAddress = entity.formatFooterAddress
                    )
                    _efficiencyConfig.value = EfficiencyConfig(
                        autoCopyWhatsAppOnGenerate = entity.effAutoCopyWhatsApp,
                        autoSaveDraftIntervalSec = entity.effAutoSaveSec
                    )
                }
            }
        }
    }

    private fun persistSettings() {
        viewModelScope.launch {
            val panelsJsonStr = serializePanelsJson(_modularPanels.value)
            val currentTheme = _appThemeConfig.value
            val currentFormat = _deliverableFormatConfig.value
            val currentEff = _efficiencyConfig.value

            val entity = AppSettingsEntity(
                id = 1,
                activePresetId = _activeUiPreset.value.id,
                customPresetName = _customPresetName.value,
                panelsJson = panelsJsonStr,
                themeAccentHex = currentTheme.primaryAccentHex,
                themeBackgroundMode = currentTheme.backgroundMode,
                themeUiDensity = currentTheme.uiDensity,
                themeFontScale = currentTheme.fontScale,
                themeHaptic = currentTheme.hapticFeedback,
                formatBrandTitle = currentFormat.brandTitle,
                formatBrandSub = currentFormat.brandSub,
                formatBrandFont = currentFormat.brandFont,
                formatBrandColorHex = currentFormat.brandColorHex,
                formatHeaderBgHex = currentFormat.headerBgHex,
                formatWatermarkText = currentFormat.watermarkText,
                formatWatermarkOpacity = currentFormat.watermarkOpacity,
                formatWatermarkAngle = currentFormat.watermarkAngle,
                formatWatermarkEnabled = currentFormat.watermarkEnabled,
                formatWatermarkStyle = currentFormat.watermarkStyle,
                formatSealType = currentFormat.sealType,
                formatFooterPhone = currentFormat.footerPhone,
                formatFooterAddress = currentFormat.footerAddress,
                effAutoCopyWhatsApp = currentEff.autoCopyWhatsAppOnGenerate,
                effAutoSaveSec = currentEff.autoSaveDraftIntervalSec
            )
            repository.saveAppSettings(entity)
        }
    }

    fun applyUiPreset(preset: UiPresetType) {
        _activeUiPreset.value = preset
        val newPanels = when (preset) {
            UiPresetType.DASHBOARD_PRO -> defaultModularPanels()
            UiPresetType.COTIZADOR_SPEED -> getFastQuotePanels()
            UiPresetType.OPERACIONES_TALLER -> getOperationsPanels()
            UiPresetType.FORENSE_AUDITORIA -> getForensicPanels()
            UiPresetType.CANVAS_CUSTOM -> _modularPanels.value
        }
        _modularPanels.value = newPanels
        persistSettings()
        _userMessage.value = "Preset aplicado: ${preset.title}"
    }

    fun togglePanelVisibility(panelId: String) {
        _modularPanels.update { list ->
            list.map { if (it.id == panelId) it.copy(isVisible = !it.isVisible) else it }
        }
        _activeUiPreset.value = UiPresetType.CANVAS_CUSTOM
        persistSettings()
    }

    fun setPanelShowFirst(panelId: String, showFirst: Boolean) {
        _modularPanels.update { list ->
            list.map { if (it.id == panelId) it.copy(isPriorityFirst = showFirst) else it }
        }
        _activeUiPreset.value = UiPresetType.CANVAS_CUSTOM
        persistSettings()
    }

    fun movePanel(panelId: String, moveUp: Boolean) {
        _modularPanels.update { current ->
            val list = current.toMutableList()
            val index = list.indexOfFirst { it.id == panelId }
            if (index != -1) {
                val targetIndex = if (moveUp) index - 1 else index + 1
                if (targetIndex in list.indices) {
                    val item = list.removeAt(index)
                    list.add(targetIndex, item)
                }
            }
            list.mapIndexed { idx, p -> p.copy(orderIndex = idx) }
        }
        _activeUiPreset.value = UiPresetType.CANVAS_CUSTOM
        persistSettings()
    }

    fun saveCustomCanvasPreset(name: String) {
        _customPresetName.value = name
        _activeUiPreset.value = UiPresetType.CANVAS_CUSTOM
        persistSettings()
        _userMessage.value = "Preset personalizado guardado: $name"
    }

    fun updateAppTheme(theme: AppThemeConfig) {
        _appThemeConfig.value = theme
        persistSettings()
        _userMessage.value = "Tema de interfaz actualizado"
    }

    fun updateDeliverableFormat(format: DeliverableFormatConfig) {
        _deliverableFormatConfig.value = format
        _quoteState.update { curr ->
            curr.copy(
                brandTitle = format.brandTitle,
                brandSub = format.brandSub,
                primaryColor = format.brandColorHex,
                darkColor = format.headerBgHex
            )
        }
        persistSettings()
        _userMessage.value = "Formato entregable actualizado"
    }

    fun updateEfficiency(efficiency: EfficiencyConfig) {
        _efficiencyConfig.value = efficiency
        persistSettings()
        _userMessage.value = "Ajustes de eficiencia guardados"
    }

    fun reintegrateColorsFromTxtSpec() {
        val authenticAppTheme = AppThemeConfig(
            primaryAccentHex = "#FFC700",
            backgroundMode = "DARK_ZINC",
            uiDensity = "STANDARD",
            fontScale = 1.0f,
            hapticFeedback = true
        )
        val authenticFormat = DeliverableFormatConfig(
            brandTitle = "JSV-AUTOPARTES",
            brandSub = "COMPRA VENTA DE AUTOPARTES ORIGINALES",
            brandFont = "IMPACT_HEAVY",
            brandColorHex = "#FFC700",
            headerBgHex = "#18181B",
            paperBgHex = "#FFFFFF",
            accentColorHex = "#FFC700",
            darkTextColorHex = "#0A0A0C",
            lightBorderColorHex = "#CBD5E1",
            watermarkText = "JSV-AUTOPARTES ORIGINAL",
            watermarkOpacity = 0.12f,
            watermarkAngle = -28f,
            watermarkEnabled = true,
            watermarkStyle = "DIAGONAL_CENTER",
            sealType = "SELLO_DIGITAL_DORADO",
            footerPhone = "WHATSAPP: 871 141 2774",
            footerAddress = "CARRETERA A BERMEJILLO KM 16 • TORREÓN / GÓMEZ PALACIO"
        )
        _appThemeConfig.value = authenticAppTheme
        _deliverableFormatConfig.value = authenticFormat
        _quoteState.update {
            it.copy(
                brandTitle = "JSV-AUTOPARTES",
                brandSub = "COMPRA VENTA DE AUTOPARTES ORIGINALES",
                primaryColor = "#FFC700",
                darkColor = "#18181B"
            )
        }
        persistSettings()
        _userMessage.value = "Paleta oficial JSV reintegrada: Amarillo (#FFC700), Gris Oscuro (#18181B), Negro (#000000), Gris Claro (#CBD5E1), Blanco (#FFFFFF)."
    }

    fun resetToFactoryDefaults() {
        _activeUiPreset.value = UiPresetType.DASHBOARD_PRO
        _modularPanels.value = defaultModularPanels()
        reintegrateColorsFromTxtSpec()
        _userMessage.value = "Configuración restaurada a valores de fábrica."
    }

    // All Orders
    val allOrders = repository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All Vehicles
    val allVehicles = repository.allVehicles.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All Warranties
    val allWarranties = repository.allWarranties.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All Photos
    val allPhotos = repository.allPhotos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Audit Logs
    val allAuditLogs = repository.allAuditLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalAuditLogsCount = repository.totalAuditLogsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val memoryAccessCount = repository.memoryAccessCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Audit filter query and category
    private val _auditSearchQuery = MutableStateFlow("")
    val auditSearchQuery: StateFlow<String> = _auditSearchQuery.asStateFlow()

    private val _auditCategoryFilter = MutableStateFlow("ALL")
    val auditCategoryFilter: StateFlow<String> = _auditCategoryFilter.asStateFlow()

    private val _auditSeverityFilter = MutableStateFlow("ALL")
    val auditSeverityFilter: StateFlow<String> = _auditSeverityFilter.asStateFlow()

    fun setAuditSearchQuery(query: String) {
        _auditSearchQuery.value = query
    }

    fun setAuditCategoryFilter(category: String) {
        _auditCategoryFilter.value = category
    }

    fun setAuditSeverityFilter(severity: String) {
        _auditSeverityFilter.value = severity
    }

    fun clearAuditLogs() {
        viewModelScope.launch {
            repository.clearAuditLogs()
            repository.logAudit(
                accion = "AUDITORIA_PURGADA",
                entidad = "Seguridad",
                entidadId = "ROOT_CLEAN",
                detalles = "Historial forense reiniciado por el administrador.",
                severidad = "SECURITY",
                categoria = "SISTEMA"
            )
            _userMessage.value = "Logs de auditoría purgados."
        }
    }

    // Dynamic Modules Operations
    fun toggleDynamicModule(moduleId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setModuleEnabled(moduleId, isEnabled)
            // If current active tab was disabled, switch to safe fallback
            val tab = JsvTab.entries.find { it.moduleId == moduleId }
            if (tab == _currentTab.value && !isEnabled) {
                _currentTab.value = JsvTab.EDITOR
            }
            _userMessage.value = "Módulo $moduleId ${if (isEnabled) "habilitado" else "deshabilitado"}."
        }
    }

    fun installOrUninstallModule(moduleId: String, isInstalled: Boolean) {
        viewModelScope.launch {
            repository.setModuleInstalled(moduleId, isInstalled)
            _userMessage.value = if (isInstalled) "Módulo $moduleId descargado y listo." else "Módulo $moduleId desinstalado."
        }
    }

    // OCR Screen Scanner & Workflow Engine
    val allOcrPresets = repository.allOcrPresets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Sample Screenshots Library
    val sampleScreenshots = listOf(
        SampleScreenshot(
            id = "ss_transmision_versa",
            title = "Ficha Despiece: Transmisión CVT Versa",
            description = "Ficha técnica de yonke con especificaciones OEM, motor 1.6L y precios",
            category = "FICHA_TECNICA",
            presetId = "preset_ficha_tecnica",
            simulatedImageText = """
                [YONKE CENTRAL MX - FICHA DONANTE]
                VEHICULO: NISSAN VERSA 2018
                MOTOR: 1.6L 4 CIL DOHC HR16DE
                AUTOPARTE: TRANSMISION AUTOMATICA CVT RE0F11A
                NUMERO DE PARTE OEM: 31020-3VX0A
                PRECIO VENTA: $14,500 MXN
                PRECIO A CAMBIO: $12,500 MXN
                GARANTIA: 90 DIAS SELLADA
                ESTADO: PROBADA EN BANCO / 48,000 KMS
            """.trimIndent(),
            detectedData = OcrExtractedData(
                donorVehicle = "Nissan Versa 2015-2019",
                partName = "Transmisión Automática CVT",
                partOem = "31020-3VX0A",
                priceVenta = 14500.0,
                priceCambio = 12500.0,
                warranty = "90 días naturales",
                detectedTriggers = listOf("OEM", "CVT", "VENTA", "CAMBIO", "GARANTIA", "HR16DE"),
                rawTranscription = "NISSAN VERSA 2018 | TRANSMISION AUTOMATICA CVT | 31020-3VX0A | $14,500",
                compiledFormattedText = "=== FICHA TECNICA JSV ===\nVEHICULO: Nissan Versa 2015-2019\nPIEZA: Transmisión Automática CVT\nOEM: 31020-3VX0A\nVENTA: $14,500.00 MXN\nCAMBIO: $12,500.00 MXN\nGARANTIA: 90 días naturales"
            )
        ),
        SampleScreenshot(
            id = "ss_motor_beat",
            title = "Chat WhatsApp: Cotización Motor Beat",
            description = "Conversación cliente preguntando disponibilidad y precio de 3/4 motor",
            category = "COTIZACION_CHAT",
            presetId = "preset_cotizacion_chat",
            simulatedImageText = """
                [WHATSAPP CLIENTE]
                Cliente: Lic. Roberto Méndez (+52 55 4920 1823)
                "Hola buenas tardes, me urge el motor 1.2L para Chevrolet Beat 2019 3/4 con factura."
                Respuesta: "Sí lo tenemos garantizado en $18,500 a cambio o $21,000 venta directa."
                Anticipo requerido: $3,000 MXN para apartado.
            """.trimIndent(),
            detectedData = OcrExtractedData(
                donorVehicle = "Chevrolet Beat 2018-2021",
                partName = "Motor 1.2L 4 CIL (3/4)",
                partOem = "12683921-OEM",
                priceVenta = 21000.0,
                priceCambio = 18500.0,
                warranty = "60 días naturales",
                clientName = "Lic. Roberto Méndez",
                clientPhone = "5549201823",
                detectedTriggers = listOf("CLIENTE", "BEAT", "3/4 MOTOR", "ANTICIPO", "COTIZACION"),
                rawTranscription = "Lic. Roberto Méndez | Chevrolet Beat 2019 | Motor 1.2L 3/4 | $21,000 Venta",
                compiledFormattedText = "=== SOLICITUD CLIENTE ===\nCLIENTE: Lic. Roberto Méndez (5549201823)\nPIEZA REQUERIDA: Motor 1.2L 4 CIL (3/4)\nPRESUPUESTO: $21,000.00 MXN (Cambio: $18,500.00)"
            )
        ),
        SampleScreenshot(
            id = "ss_compresor_jetta",
            title = "Ticket Remisión: Compresor A/C Jetta",
            description = "Nota de yonke con desglose de compresor Sanden original",
            category = "TICKET_FACTURA",
            presetId = "preset_ticket_factura",
            simulatedImageText = """
                [REMISION REFACCIONES DEL NORTE #4819]
                EMISOR: Autopartes y Yonke Los Álamos
                FECHA: 2026-08-28
                PIEZA: Compresor Aire Acondicionado Sanden 5Q0820803
                COMPATIBILIDAD: VW Jetta MK6 2.5L / Vento
                TOTAL PAGADO: $3,800.00 MXN EFECTIVO
                POLIZA: Sello amarillo intacto, 30 días prueba.
            """.trimIndent(),
            detectedData = OcrExtractedData(
                donorVehicle = "Volkswagen Jetta MK6 2011-2018",
                partName = "Compresor Aire Acondicionado OEM",
                partOem = "5Q0820803",
                priceVenta = 3800.0,
                priceCambio = 3200.0,
                warranty = "30 días naturales",
                detectedTriggers = listOf("COMPRESOR", "SANDEN", "JETTA", "TOTAL", "EFECTIVO"),
                rawTranscription = "Autopartes Los Álamos | Compresor A/C Sanden 5Q0820803 | Jetta MK6 | $3,800",
                compiledFormattedText = "=== NOTA COMPRA / PROVEEDOR ===\nYONKE: Autopartes y Yonke Los Álamos\nPIEZA: Compresor Aire Acondicionado OEM (5Q0820803)\nCOSTO: $3,800.00 MXN"
            )
        )
    )

    private val _selectedScreenshot = MutableStateFlow(sampleScreenshots[0])
    val selectedScreenshot: StateFlow<SampleScreenshot> = _selectedScreenshot.asStateFlow()

    private val _selectedPresetId = MutableStateFlow("preset_ficha_tecnica")
    val selectedPresetId: StateFlow<String> = _selectedPresetId.asStateFlow()

    private val _ocrExtractedData = MutableStateFlow(sampleScreenshots[0].detectedData)
    val ocrExtractedData: StateFlow<OcrExtractedData> = _ocrExtractedData.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // Workflow Engine State
    private val _workflowState = MutableStateFlow(
        WorkflowExecutionState(
            isExecuting = false,
            currentStepIndex = -1,
            nodes = createDefaultWorkflowNodes(),
            logs = listOf("Workflow en espera. Selecciona un Preset y ejecuta el escaneo.")
        )
    )
    val workflowState: StateFlow<WorkflowExecutionState> = _workflowState.asStateFlow()

    private fun createDefaultWorkflowNodes(): List<WorkflowNode> {
        return listOf(
            WorkflowNode(
                id = "n1",
                type = WorkflowNodeType.TRIGGER_OCR,
                title = "1. Captura OCR Detectada",
                subtitle = "Inspección de puntos clave y zonas",
                status = NodeStatus.IDLE,
                channel = "PRIMARY"
            ),
            WorkflowNode(
                id = "n2",
                type = WorkflowNodeType.PARSE_CLASSIFY,
                title = "2. Clasificación de Campos",
                subtitle = "Extracción de OEM, Donante y Precios",
                status = NodeStatus.IDLE,
                channel = "PRIMARY"
            ),
            WorkflowNode(
                id = "n3",
                type = WorkflowNodeType.ROUTER_CONDITIONAL,
                title = "3. Router Inteligente",
                subtitle = "Evaluación de triggers y reglas de negocio",
                status = NodeStatus.IDLE,
                channel = "PRIMARY"
            ),
            WorkflowNode(
                id = "n4",
                type = WorkflowNodeType.ROOM_MEMORY_SYNC,
                title = "4. Sincronización Memoria Local",
                subtitle = "Persistencia en SQLite / Room con Hash",
                status = NodeStatus.IDLE,
                channel = "BRANCH_A"
            ),
            WorkflowNode(
                id = "n5",
                type = WorkflowNodeType.WHATSAPP_BROADCAST,
                title = "5. Formato Multicanal",
                subtitle = "Generación de texto WhatsApp y Smart Link",
                status = NodeStatus.IDLE,
                channel = "BRANCH_B"
            ),
            WorkflowNode(
                id = "n6",
                type = WorkflowNodeType.PUSH_NOTIFICATION_ALERT,
                title = "6. Disparo Alerta Push",
                subtitle = "Notificación al centro de alertas del sistema",
                status = NodeStatus.IDLE,
                channel = "BRANCH_A"
            ),
            WorkflowNode(
                id = "n7",
                type = WorkflowNodeType.ORDER_DISPATCH,
                title = "7. Despacho a Cotizador",
                subtitle = "Carga de snapshot en el editor activo",
                status = NodeStatus.IDLE,
                channel = "PRIMARY"
            )
        )
    }

    fun selectScreenshot(screenshot: SampleScreenshot) {
        _selectedScreenshot.value = screenshot
        _selectedPresetId.value = screenshot.presetId
        _ocrExtractedData.value = screenshot.detectedData
        _workflowState.update { it.copy(isExecuting = false, currentStepIndex = -1, nodes = createDefaultWorkflowNodes(), isComplete = false) }
    }

    fun selectPreset(presetId: String) {
        _selectedPresetId.value = presetId
    }

    fun runOcrScan() {
        viewModelScope.launch {
            _isScanning.value = true
            repository.logAudit(
                accion = "OCR_SCAN_INICIADO",
                entidad = "ScannerOCR",
                entidadId = _selectedScreenshot.value.id,
                detalles = "Analizando captura [${_selectedScreenshot.value.title}] con preset [${_selectedPresetId.value}]",
                severidad = "INFO",
                categoria = "OCR",
                memoriaTipo = "RAM_CACHE"
            )
            delay(800)
            _ocrExtractedData.value = _selectedScreenshot.value.detectedData
            _isScanning.value = false
            _userMessage.value = "✅ Texto extraído y clasificado (${_selectedScreenshot.value.detectedData.detectedTriggers.size} triggers detectados)"
        }
    }

    fun executeAutomatedWorkflow() {
        viewModelScope.launch {
            if (_workflowState.value.isExecuting) return@launch

            val data = _ocrExtractedData.value
            _workflowState.value = WorkflowExecutionState(
                isExecuting = true,
                currentStepIndex = 0,
                nodes = createDefaultWorkflowNodes(),
                logs = mutableListOf("🚀 Iniciando ejecución de Workflow Automático multicanal...")
            )

            val nodes = _workflowState.value.nodes.toMutableList()
            val logs = mutableListOf<String>()

            // Step 1: Trigger OCR
            nodes[0] = nodes[0].copy(status = NodeStatus.RUNNING)
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList(), currentStepIndex = 0)
            delay(400)
            nodes[0] = nodes[0].copy(status = NodeStatus.SUCCESS, outputData = "Captura analizada: ${_selectedScreenshot.value.title}")
            logs.add("✔️ [TRIGGER] Captura cargada. Preset: ${_selectedPresetId.value}")
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList(), logs = logs.toList(), currentStepIndex = 1)

            // Step 2: Parse & Classify
            nodes[1] = nodes[1].copy(status = NodeStatus.RUNNING)
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList())
            delay(500)
            nodes[1] = nodes[1].copy(status = NodeStatus.SUCCESS, outputData = "OEM: ${data.partOem} | Pieza: ${data.partName}")
            logs.add("✔️ [CLASIFICACION] Pieza clasificada: ${data.partName} (${data.donorVehicle})")
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList(), logs = logs.toList(), currentStepIndex = 2)

            // Step 3: Router
            nodes[2] = nodes[2].copy(status = NodeStatus.RUNNING)
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList())
            delay(400)
            nodes[2] = nodes[2].copy(status = NodeStatus.SUCCESS, outputData = "Regla activada: Desglose Venta ($${data.priceVenta})")
            logs.add("✔️ [ROUTER] Triggers evaluados: ${data.detectedTriggers.joinToString(", ")}")
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList(), logs = logs.toList(), currentStepIndex = 3)

            // Step 4: Room Memory Sync
            nodes[3] = nodes[3].copy(status = NodeStatus.RUNNING)
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList())
            delay(400)
            repository.logAudit(
                accion = "WORKFLOW_MEMORIA_SYNC",
                entidad = "WorkflowOCR",
                entidadId = "WF-${UUID.randomUUID().toString().take(6)}",
                detalles = "Data clasificada sincronizada en memoria Room. Pieza: ${data.partName}",
                severidad = "INFO",
                categoria = "MEMORIA_LOCAL",
                memoriaTipo = "ROOM_SQLITE"
            )
            nodes[3] = nodes[3].copy(status = NodeStatus.SUCCESS, outputData = "Persistido en Room SQLite")
            logs.add("✔️ [MEMORIA LOCAL] Registro guardado con hash criptográfico")
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList(), logs = logs.toList(), currentStepIndex = 4)

            // Step 5: WhatsApp Broadcast
            nodes[4] = nodes[4].copy(status = NodeStatus.RUNNING)
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList())
            delay(400)
            nodes[4] = nodes[4].copy(status = NodeStatus.SUCCESS, outputData = "Texto para WhatsApp generado")
            logs.add("✔️ [MULTICANAL] Formato compilado listo para compartir con el cliente")
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList(), logs = logs.toList(), currentStepIndex = 5)

            // Step 6: Push Notification Alert
            nodes[5] = nodes[5].copy(status = NodeStatus.RUNNING)
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList())
            delay(400)
            repository.insertNotification(
                NotificationItem(
                    title = "⚡ Workflow OCR Completado",
                    message = "Se clasificó: ${data.partName} (${data.donorVehicle}) por $${data.priceVenta} MXN.",
                    category = "OCR",
                    priority = "NORMAL",
                    actionRoute = "EDITOR"
                )
            )
            nodes[5] = nodes[5].copy(status = NodeStatus.SUCCESS, outputData = "Push Alerta emitida")
            logs.add("✔️ [PUSH] Alerta enviada al centro de notificaciones")
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList(), logs = logs.toList(), currentStepIndex = 6)

            // Step 7: Order Dispatch / Transfer
            nodes[6] = nodes[6].copy(status = NodeStatus.RUNNING)
            _workflowState.value = _workflowState.value.copy(nodes = nodes.toList())
            delay(300)
            nodes[6] = nodes[6].copy(status = NodeStatus.SUCCESS, outputData = "Listo para aplicar al Cotizador")
            logs.add("🏁 [COMPLETADO] Workflow finalizado exitosamente. Haz clic en 'Transferir al Cotizador'.")

            _workflowState.value = WorkflowExecutionState(
                isExecuting = false,
                currentStepIndex = 7,
                nodes = nodes.toList(),
                logs = logs.toList(),
                isComplete = true
            )
            _userMessage.value = "🎉 ¡Workflow ejecutado y memorizado en Room con éxito!"
        }
    }

    fun transferOcrDataToQuote() {
        val data = _ocrExtractedData.value
        updateQuoteState { current ->
            val updatedBreakdown = current.breakdownItems.toMutableList()
            if (data.priceVenta > 0) {
                if (updatedBreakdown.isEmpty()) {
                    updatedBreakdown.add(
                        BreakdownItem(
                            concept = data.partName.ifBlank { "Autoparte Escaneada" },
                            qty = "1",
                            condition = "OEM Original",
                            priceVenta = data.priceVenta,
                            priceCambio = data.priceCambio
                        )
                    )
                } else {
                    updatedBreakdown[0] = updatedBreakdown[0].copy(
                        concept = data.partName.ifBlank { updatedBreakdown[0].concept },
                        priceVenta = data.priceVenta,
                        priceCambio = data.priceCambio
                    )
                }
            }

            current.copy(
                donorVehicle = data.donorVehicle.ifBlank { current.donorVehicle },
                partName = data.partName.ifBlank { current.partName },
                partOem = data.partOem.ifBlank { current.partOem },
                breakdownItems = updatedBreakdown
            )
        }
        _userMessage.value = "✅ Datos transferidos al Cotizador activo."
        _currentTab.value = JsvTab.EDITOR
    }

    // Metrics State
    val metrics: StateFlow<MetricsData> = allOrders.combine(_quoteState) { orders, _ ->
        val total = orders.size
        val delivered = orders.count { it.estado.equals("entregado", ignoreCase = true) }
        val percent = if (total > 0) (delivered.toFloat() / total) * 100f else 0f
        val revenue = orders.sumOf { it.montoTotal }

        val statusCounts = mutableMapOf(
            "cotizado" to 0,
            "pagado" to 0,
            "envio" to 0,
            "entregado" to 0,
            "cancelado" to 0
        )
        orders.forEach { o ->
            val key = o.estado.lowercase()
            statusCounts[key] = (statusCounts[key] ?: 0) + 1
        }

        val modelCounts = orders.groupingBy { it.vehiculo.ifBlank { "Varios" } }.eachCount()
        val topModel = modelCounts.maxByOrNull { it.value }?.key ?: "N/A"

        val pieceCounts = orders.groupingBy { it.pieza.ifBlank { "Autoparte" } }.eachCount()
        val topPiece = pieceCounts.maxByOrNull { it.value }?.key ?: "N/A"

        MetricsData(
            totalQuotes = total,
            deliveredCount = delivered,
            deliveredPercent = percent,
            totalRevenue = revenue,
            topModel = topModel,
            topPiece = topPiece,
            statusCounts = statusCounts
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MetricsData()
    )

    // Smart Search Links (dynamically computed from current quote)
    val smartLinks: StateFlow<SmartSearchLinks> = _quoteState.combine(_quoteState) { state, _ ->
        val terms = mutableListOf<String>()
        if (state.donorVehicle.isNotBlank()) terms.add(state.donorVehicle)
        if (state.partName.isNotBlank()) terms.add(state.partName)
        if (state.partOem.isNotBlank()) terms.add(state.partOem)
        val query = terms.joinToString(" ").trim().ifBlank { "Autopartes Originales México" }
        val encoded = try {
            URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            query.replace(" ", "+")
        }

        SmartSearchLinks(
            googleImages = "https://www.google.com/search?q=$encoded&tbm=isch",
            googleWeb = "https://www.google.com/search?q=$encoded",
            mercadoLibre = "https://listado.mercadolibre.com.mx/$encoded#D[A:$encoded]",
            amazon = "https://www.amazon.com.mx/s?k=$encoded",
            youtube = "https://www.youtube.com/results?search_query=$encoded+autoparte",
            facebook = "https://www.facebook.com/search/top?q=$encoded",
            queryDisplay = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SmartSearchLinks("", "", "", "", "", "", "")
    )

    init {
        // Load initial draft from Room if available
        viewModelScope.launch {
            val savedDraft = repository.activeDraftState.firstOrNull()
            if (savedDraft != null) {
                _quoteState.value = savedDraft
            }
        }
    }

    // --- Quote State Modifiers ---
    fun updateQuoteState(transform: (QuoteState) -> QuoteState) {
        _quoteState.update { current ->
            val updated = transform(current)
            viewModelScope.launch {
                repository.saveActiveDraft(updated)
            }
            updated
        }
    }

    fun setQuoteType(type: String) {
        updateQuoteState { it.copy(quoteType = type) }
    }

    fun applyVehicle(vehicle: Vehicle) {
        updateQuoteState {
            it.copy(
                donorVehicle = "${vehicle.marca} ${vehicle.modelo}",
                yearRange = vehicle.anios,
                engineRange = vehicle.motor
            )
        }
        _userMessage.value = "Vehículo aplicado: ${vehicle.marca} ${vehicle.modelo}"
    }

    fun applyPhotoToQuote(photo: PhotoItem) {
        updateQuoteState { it.copy(imgStockLocal = photo.filename) }
        _userMessage.value = "Foto aplicada al registro visual: ${photo.title}"
    }

    fun addBreakdownItem() {
        updateQuoteState {
            val newItems = it.breakdownItems.toMutableList().apply {
                add(
                    BreakdownItem(
                        concept = "Pieza OEM Adicional",
                        qty = "1",
                        condition = "OEM Original",
                        priceVenta = 0.0,
                        priceCambio = 0.0
                    )
                )
            }
            it.copy(breakdownItems = newItems)
        }
    }

    fun removeBreakdownItem(id: String) {
        updateQuoteState {
            val newItems = it.breakdownItems.filterNot { item -> item.id == id }
            it.copy(breakdownItems = newItems)
        }
    }

    fun updateBreakdownItem(item: BreakdownItem) {
        updateQuoteState {
            val newItems = it.breakdownItems.map { curr -> if (curr.id == item.id) item else curr }
            it.copy(breakdownItems = newItems)
        }
    }

    fun addOption() {
        updateQuoteState {
            val newOptions = it.options.toMutableList().apply {
                add(
                    QuoteOption(
                        title = "Nueva Opción Proveedor",
                        price = 0.0,
                        description = "Inspección técnica y verificación de sellos de garantía.",
                        condition = "Seminuevo",
                        delivery = "3-5 días",
                        warranty = "30 días"
                    )
                )
            }
            it.copy(options = newOptions)
        }
    }

    fun removeOption(id: String) {
        updateQuoteState {
            val newOptions = it.options.filterNot { opt -> opt.id == id }
            it.copy(options = newOptions)
        }
    }

    fun updateOption(option: QuoteOption) {
        updateQuoteState {
            val newOptions = it.options.map { curr -> if (curr.id == option.id) option else curr }
            it.copy(options = newOptions)
        }
    }

    fun addProcessStep(title: String, desc: String) {
        updateQuoteState {
            val newSteps = it.procesoPasos.toMutableList().apply {
                add(OrderProcessStep(title = title, description = desc, marked = false))
            }
            it.copy(procesoPasos = newSteps)
        }
    }

    fun toggleProcessStep(id: String) {
        updateQuoteState {
            val newSteps = it.procesoPasos.map { s -> if (s.id == id) s.copy(marked = !s.marked) else s }
            it.copy(procesoPasos = newSteps)
        }
    }

    fun removeProcessStep(id: String) {
        updateQuoteState {
            val newSteps = it.procesoPasos.filterNot { s -> s.id == id }
            it.copy(procesoPasos = newSteps)
        }
    }

    fun resetQuoteState() {
        val clean = QuoteState()
        _quoteState.value = clean
        viewModelScope.launch {
            repository.saveActiveDraft(clean)
        }
        _userMessage.value = "Cotizador restablecido a valores por defecto."
    }

    // --- Order Save & Load ---
    fun saveQuoteAsOrder(clienteNombre: String, clienteTelefono: String, clienteDireccion: String) {
        viewModelScope.launch {
            val state = _quoteState.value
            val total = state.getEffectiveTotal()
            val jsonSnapshot = JsvJsonHelper.quoteStateToJson(state)
            val order = Order(
                titulo = if (state.quoteType == "stock_local") "${state.partName} - ${state.donorVehicle}" else "Sobre Pedido: ${state.partName}",
                vehiculo = state.donorVehicle,
                pieza = state.partName,
                clienteNombre = clienteNombre,
                clienteTelefono = clienteTelefono,
                clienteDireccion = clienteDireccion,
                montoTotal = total,
                cotizacionJson = jsonSnapshot
            )
            repository.insertOrder(order)
            _userMessage.value = "¡Cotización guardada como Pedido #${order.id}!"
            _currentTab.value = JsvTab.SEGUIMIENTO
        }
    }

    fun loadOrderIntoEditor(order: Order) {
        if (order.cotizacionJson.isNotBlank()) {
            val loadedState = JsvJsonHelper.jsonToQuoteState(order.cotizacionJson)
            _quoteState.value = loadedState
            viewModelScope.launch {
                repository.saveActiveDraft(loadedState)
                repository.logAudit(
                    accion = "PEDIDO_CARGADO",
                    entidad = "Pedido",
                    entidadId = order.id,
                    detalles = "Pedido #${order.id} cargado en el editor activo",
                    severidad = "INFO",
                    categoria = "USUARIO"
                )
            }
            _userMessage.value = "Pedido #${order.id} cargado en el Editor."
            _currentTab.value = JsvTab.EDITOR
        } else {
            _userMessage.value = "El pedido no contiene snapshot JSON."
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String, tracking: String = "", notes: String = "") {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus, tracking, notes)
            _userMessage.value = "Estado de pedido #$orderId actualizado a $newStatus."
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
            _userMessage.value = "Pedido eliminado."
        }
    }

    // --- Warranty Management ---
    fun createWarrantyFromQuote(
        clienteNombre: String,
        clienteTelefono: String,
        clienteDireccion: String,
        preset: String = "general"
    ) {
        viewModelScope.launch {
            val state = _quoteState.value
            val warranty = Warranty(
                presetActivo = preset,
                tiempoGarantia = if (preset == "compresor") "90 días naturales" else "30 días naturales",
                terminos = "Póliza oficial JSV-AUTOPARTES. Garantía directa contra defectos internos y fallas de funcionamiento. Sellos de garantía intactos.",
                clienteNombre = clienteNombre,
                clienteTelefono = clienteTelefono,
                clienteDireccion = clienteDireccion,
                piezaNombre = state.partName,
                piezaMarca = state.donorVehicle.split(" ").firstOrNull() ?: "",
                piezaModelo = state.donorVehicle,
                piezaNumeroParte = state.partOem,
                piezaIdSerial = "SN-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()),
                piezaFoto = state.imgStockLocal
            )
            repository.insertWarranty(warranty)
            _userMessage.value = "¡Garantía #${warranty.id} generada y registrada!"
            _currentTab.value = JsvTab.GARANTIA
        }
    }

    fun saveWarranty(warranty: Warranty) {
        viewModelScope.launch {
            repository.insertWarranty(warranty)
            _userMessage.value = "Garantía guardada con éxito."
        }
    }

    fun deleteWarranty(warrantyId: String) {
        viewModelScope.launch {
            repository.deleteWarranty(warrantyId)
            _userMessage.value = "Garantía eliminada."
        }
    }

    // --- Vehicle Management ---
    fun addVehicle(marca: String, modelo: String, anios: String, motor: String) {
        if (marca.isBlank() || modelo.isBlank()) {
            _userMessage.value = "Ingresa al menos marca y modelo."
            return
        }
        viewModelScope.launch {
            val v = Vehicle(marca = marca.trim(), modelo = modelo.trim(), anios = anios.trim(), motor = motor.trim())
            repository.insertVehicle(v)
            _userMessage.value = "Vehículo agregado al catálogo: ${v.marca} ${v.modelo}"
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle.id, vehicle.displayName)
            _userMessage.value = "Vehículo eliminado."
        }
    }

    // --- Photo Bank Management ---
    fun addPhoto(filename: String, title: String, category: String) {
        viewModelScope.launch {
            val p = PhotoItem(filename = filename, title = title, category = category)
            repository.insertPhoto(p)
            _userMessage.value = "Foto guardada en el Banco: $title"
        }
    }

    fun rotatePhoto(photo: PhotoItem) {
        viewModelScope.launch {
            repository.rotatePhoto(photo.id, photo.rotationDegrees)
        }
    }

    fun deletePhoto(photo: PhotoItem) {
        viewModelScope.launch {
            repository.deletePhoto(photo.id, photo.title)
            _userMessage.value = "Foto eliminada."
        }
    }

    // --- Markdown Import / Export ---
    fun exportMarkdown(): String {
        return JsvJsonHelper.exportToMarkdown(_quoteState.value)
    }

    fun importMarkdownText(text: String): Boolean {
        val parsed = JsvJsonHelper.importFromMarkdown(text)
        return if (parsed != null) {
            _quoteState.value = parsed
            viewModelScope.launch {
                repository.saveActiveDraft(parsed)
                repository.logAudit(
                    accion = "IMPORTACION_MD",
                    entidad = "Cotización",
                    entidadId = parsed.folioRef,
                    detalles = "Cotización importada exitosamente desde archivo o texto Markdown con JSON embebido.",
                    severidad = "INFO",
                    categoria = "USUARIO"
                )
            }
            _userMessage.value = "¡Cotización importada correctamente!"
            _currentTab.value = JsvTab.EDITOR
            true
        } else {
            _userMessage.value = "No se detectó un JSON o formato Markdown válido de JSV Studio."
            false
        }
    }

    fun getWhatsAppShareText(): String {
        return JsvJsonHelper.formatForWhatsApp(_quoteState.value)
    }

    fun exportAuditReport(): String {
        val logs = allAuditLogs.value
        val sb = StringBuilder()
        sb.appendLine("# REPORTE DE AUDITORIA FORENSE Y MEMORIA LOCAL JSV")
        sb.appendLine("Generado: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
        sb.appendLine("Total Eventos: ${logs.size} | Accesos Memoria: ${memoryAccessCount.value}")
        sb.appendLine("------------------------------------------------------------")
        logs.forEach { l ->
            sb.appendLine("[${l.fecha}] [${l.severidad}] [${l.memoriaTipo}] ${l.accion} -> ${l.entidad} (#${l.entidadId})")
            sb.appendLine("   Detalles: ${l.detalles}")
            sb.appendLine("   Hash Verificación: ${l.hashIntegridad} | Latencia: ${l.tiempoEjecucionMs}ms")
            sb.appendLine()
        }
        return sb.toString()
    }
}

fun defaultModularPanels(): List<UiPanelModule> = listOf(
    UiPanelModule("EDITOR", "Cotizador Oficial", "Editor de cotizaciones y fichas técnicas", "edit", "COTIZACION", isVisible = true, isPriorityFirst = true, orderIndex = 0),
    UiPanelModule("SEGUIMIENTO", "Seguimiento de Pedidos", "Control de pedidos y estados de despacho", "local_shipping", "OPERACIONES", isVisible = true, isPriorityFirst = false, orderIndex = 1),
    UiPanelModule("GARANTIA", "Pólizas de Garantía", "Registro y validación de 30 días", "verified_user", "OPERACIONES", isVisible = true, isPriorityFirst = false, orderIndex = 2),
    UiPanelModule("VEHICULOS", "Catálogo Vehículos", "Donantes y despieces disponibles", "directions_car", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 3),
    UiPanelModule("FOTOS", "Banco de Fotos", "Archivo fotográfico de inspección", "photo_camera", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 4),
    UiPanelModule("LINKS", "Smart Links", "Búsqueda rápida en plataformas líderes", "public", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 5),
    UiPanelModule("SCANNER_OCR", "Escáner OCR", "Extracción inteligente de fichas y fotos", "document_scanner", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 6),
    UiPanelModule("AUDITORIA", "Auditoría & Memoria", "Bitácora forense de seguridad", "security", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 7),
    UiPanelModule("MODULOS", "Gestor de Módulos", "Activación dinámica de componentes", "extension", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 8),
    UiPanelModule("NOTIFICACIONES", "Centro de Alertas", "Notificaciones push y avisos de garantía", "notifications", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 9),
    UiPanelModule("AJUSTES", "Ajustes & Canvas", "Personalización profunda de interfaz y formatos", "settings", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 10)
)

fun getFastQuotePanels(): List<UiPanelModule> = listOf(
    UiPanelModule("EDITOR", "Cotizador Oficial", "Editor de cotizaciones y fichas técnicas", "edit", "COTIZACION", isVisible = true, isPriorityFirst = true, orderIndex = 0),
    UiPanelModule("LINKS", "Smart Links", "Búsqueda rápida en plataformas líderes", "public", "HERRAMIENTAS", isVisible = true, isPriorityFirst = true, orderIndex = 1),
    UiPanelModule("FOTOS", "Banco de Fotos", "Archivo fotográfico de inspección", "photo_camera", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 2),
    UiPanelModule("SEGUIMIENTO", "Seguimiento de Pedidos", "Control de pedidos y estados de despacho", "local_shipping", "OPERACIONES", isVisible = true, isPriorityFirst = false, orderIndex = 3),
    UiPanelModule("AJUSTES", "Ajustes & Canvas", "Personalización profunda de interfaz y formatos", "settings", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 4),
    UiPanelModule("GARANTIA", "Pólizas de Garantía", "Registro y validación de 30 días", "verified_user", "OPERACIONES", isVisible = false, isPriorityFirst = false, orderIndex = 5),
    UiPanelModule("VEHICULOS", "Catálogo Vehículos", "Donantes y despieces disponibles", "directions_car", "HERRAMIENTAS", isVisible = false, isPriorityFirst = false, orderIndex = 6),
    UiPanelModule("SCANNER_OCR", "Escáner OCR", "Extracción inteligente de fichas y fotos", "document_scanner", "HERRAMIENTAS", isVisible = false, isPriorityFirst = false, orderIndex = 7),
    UiPanelModule("AUDITORIA", "Auditoría & Memoria", "Bitácora forense de seguridad", "security", "SISTEMA", isVisible = false, isPriorityFirst = false, orderIndex = 8),
    UiPanelModule("MODULOS", "Gestor de Módulos", "Activación dinámica de componentes", "extension", "SISTEMA", isVisible = false, isPriorityFirst = false, orderIndex = 9),
    UiPanelModule("NOTIFICACIONES", "Centro de Alertas", "Notificaciones push y avisos de garantía", "notifications", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 10)
)

fun getOperationsPanels(): List<UiPanelModule> = listOf(
    UiPanelModule("SEGUIMIENTO", "Seguimiento de Pedidos", "Control de pedidos y estados de despacho", "local_shipping", "OPERACIONES", isVisible = true, isPriorityFirst = true, orderIndex = 0),
    UiPanelModule("GARANTIA", "Pólizas de Garantía", "Registro y validación de 30 días", "verified_user", "OPERACIONES", isVisible = true, isPriorityFirst = true, orderIndex = 1),
    UiPanelModule("SCANNER_OCR", "Escáner OCR", "Extracción inteligente de fichas y fotos", "document_scanner", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 2),
    UiPanelModule("VEHICULOS", "Catálogo Vehículos", "Donantes y despieces disponibles", "directions_car", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 3),
    UiPanelModule("EDITOR", "Cotizador Oficial", "Editor de cotizaciones y fichas técnicas", "edit", "COTIZACION", isVisible = true, isPriorityFirst = false, orderIndex = 4),
    UiPanelModule("FOTOS", "Banco de Fotos", "Archivo fotográfico de inspección", "photo_camera", "HERRAMIENTAS", isVisible = true, isPriorityFirst = false, orderIndex = 5),
    UiPanelModule("LINKS", "Smart Links", "Búsqueda rápida en plataformas líderes", "public", "HERRAMIENTAS", isVisible = false, isPriorityFirst = false, orderIndex = 6),
    UiPanelModule("AUDITORIA", "Auditoría & Memoria", "Bitácora forense de seguridad", "security", "SISTEMA", isVisible = false, isPriorityFirst = false, orderIndex = 7),
    UiPanelModule("MODULOS", "Gestor de Módulos", "Activación dinámica de componentes", "extension", "SISTEMA", isVisible = false, isPriorityFirst = false, orderIndex = 8),
    UiPanelModule("NOTIFICACIONES", "Centro de Alertas", "Notificaciones push y avisos de garantía", "notifications", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 9),
    UiPanelModule("AJUSTES", "Ajustes & Canvas", "Personalización profunda de interfaz y formatos", "settings", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 10)
)

fun getForensicPanels(): List<UiPanelModule> = listOf(
    UiPanelModule("AUDITORIA", "Auditoría & Memoria", "Bitácora forense de seguridad", "security", "SISTEMA", isVisible = true, isPriorityFirst = true, orderIndex = 0),
    UiPanelModule("MODULOS", "Gestor de Módulos", "Activación dinámica de componentes", "extension", "SISTEMA", isVisible = true, isPriorityFirst = true, orderIndex = 1),
    UiPanelModule("AJUSTES", "Ajustes & Canvas", "Personalización profunda de interfaz y formatos", "settings", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 2),
    UiPanelModule("NOTIFICACIONES", "Centro de Alertas", "Notificaciones push y avisos de garantía", "notifications", "SISTEMA", isVisible = true, isPriorityFirst = false, orderIndex = 3),
    UiPanelModule("EDITOR", "Cotizador Oficial", "Editor de cotizaciones y fichas técnicas", "edit", "COTIZACION", isVisible = true, isPriorityFirst = false, orderIndex = 4),
    UiPanelModule("SEGUIMIENTO", "Seguimiento de Pedidos", "Control de pedidos y estados de despacho", "local_shipping", "OPERACIONES", isVisible = false, isPriorityFirst = false, orderIndex = 5),
    UiPanelModule("GARANTIA", "Pólizas de Garantía", "Registro y validación de 30 días", "verified_user", "OPERACIONES", isVisible = false, isPriorityFirst = false, orderIndex = 6),
    UiPanelModule("VEHICULOS", "Catálogo Vehículos", "Donantes y despieces disponibles", "directions_car", "HERRAMIENTAS", isVisible = false, isPriorityFirst = false, orderIndex = 7),
    UiPanelModule("FOTOS", "Banco de Fotos", "Archivo fotográfico de inspección", "photo_camera", "HERRAMIENTAS", isVisible = false, isPriorityFirst = false, orderIndex = 8),
    UiPanelModule("LINKS", "Smart Links", "Búsqueda rápida en plataformas líderes", "public", "HERRAMIENTAS", isVisible = false, isPriorityFirst = false, orderIndex = 9),
    UiPanelModule("SCANNER_OCR", "Escáner OCR", "Extracción inteligente de fichas y fotos", "document_scanner", "HERRAMIENTAS", isVisible = false, isPriorityFirst = false, orderIndex = 10)
)

fun serializePanelsJson(panels: List<UiPanelModule>): String {
    val arr = JSONArray()
    panels.forEach { p ->
        val obj = JSONObject()
        obj.put("id", p.id)
        obj.put("title", p.title)
        obj.put("subtitle", p.subtitle)
        obj.put("iconName", p.iconName)
        obj.put("category", p.category)
        obj.put("isVisible", p.isVisible)
        obj.put("isPriorityFirst", p.isPriorityFirst)
        obj.put("orderIndex", p.orderIndex)
        arr.put(obj)
    }
    return arr.toString()
}

fun parsePanelsJson(jsonStr: String): List<UiPanelModule> {
    val result = mutableListOf<UiPanelModule>()
    try {
        val arr = JSONArray(jsonStr)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            result.add(
                UiPanelModule(
                    id = obj.getString("id"),
                    title = obj.optString("title", ""),
                    subtitle = obj.optString("subtitle", ""),
                    iconName = obj.optString("iconName", "circle"),
                    category = obj.optString("category", "SISTEMA"),
                    isVisible = obj.optBoolean("isVisible", true),
                    isPriorityFirst = obj.optBoolean("isPriorityFirst", false),
                    orderIndex = obj.optInt("orderIndex", i)
                )
            )
        }
    } catch (e: Exception) {
        return defaultModularPanels()
    }
    return result
}

