package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class UiPresetType(val id: String, val title: String, val description: String, val icon: String) {
    DASHBOARD_PRO(
        id = "dashboard_pro",
        title = "Dashboard Integral Pro",
        description = "Vista balanceada con todas las herramientas de cotización, pedidos, fotos y métricas accesibles.",
        icon = "dashboard"
    ),
    COTIZADOR_SPEED(
        id = "cotizador_speed",
        title = "Cotizador Ultra Rápido",
        description = "Prioridad máxima en edición rápida de cotizaciones, WhatsApp instantáneo y vista previa.",
        icon = "bolt"
    ),
    OPERACIONES_TALLER(
        id = "operaciones_taller",
        title = "Operaciones & Despacho",
        description = "Enfoque en pedidos en curso, garantías, banco de vehículos y escáner OCR para almacén.",
        icon = "build"
    ),
    FORENSE_AUDITORIA(
        id = "forense_auditoria",
        title = "Auditoría & Seguridad",
        description = "Panel técnico con registro forense SQLite, integridad de memoria, módulos y telemetría.",
        icon = "security"
    ),
    CANVAS_CUSTOM(
        id = "canvas_custom",
        title = "Personalizado por Canvas",
        description = "Acomodo modular a la medida definido directamente en el lienzo interactivo de diseño.",
        icon = "brush"
    )
}

data class UiPanelModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val category: String, // "COTIZACION", "OPERACIONES", "HERRAMIENTAS", "SISTEMA"
    val isVisible: Boolean = true,
    val isPriorityFirst: Boolean = false,
    val orderIndex: Int = 0
)

data class AppThemeConfig(
    val primaryAccentHex: String = "#FFC700",      // Amarillo Industrial JSV
    val backgroundMode: String = "DARK_ZINC",       // "DARK_ZINC", "OLED_BLACK", "STEALTH_GRAY", "LIGHT_CLEAN"
    val uiDensity: String = "STANDARD",             // "COMPACT", "STANDARD", "SPACIOUS"
    val fontScale: Float = 1.0f,
    val hapticFeedback: Boolean = true,
    val soundClicks: Boolean = false,
    val quickActionBar: Boolean = true,
    val highContrastCards: Boolean = true
)

data class DeliverableFormatConfig(
    val brandTitle: String = "JSV-AUTOPARTES",
    val brandSub: String = "COMPRA VENTA DE AUTOPARTES ORIGINALES",
    val brandFont: String = "IMPACT_HEAVY",         // "IMPACT_HEAVY", "HEAVY_BLACK", "CONDENSED_BOLD", "MONOSPACE_TECH"
    val brandColorHex: String = "#FFC700",          // Amarillo Primario JSV
    val headerBgHex: String = "#18181B",            // Gris Oscuro Zinc
    val paperBgHex: String = "#FFFFFF",             // Blanco Imprimible
    val accentColorHex: String = "#FFC700",         // Amarillo Resaltador
    val darkTextColorHex: String = "#0A0A0C",       // Negro Profundo
    val lightBorderColorHex: String = "#CBD5E1",    // Gris Claro
    val watermarkText: String = "JSV-AUTOPARTES ORIGINAL",
    val watermarkOpacity: Float = 0.12f,
    val watermarkAngle: Float = -28f,
    val watermarkEnabled: Boolean = true,
    val watermarkStyle: String = "DIAGONAL_CENTER", // "DIAGONAL_CENTER", "REPEATED_PATTERN", "CORNER_STAMP"
    val sealType: String = "SELLO_DIGITAL_DORADO",  // "SELLO_DIGITAL_DORADO", "SELLO_OFICIAL_NEGRO", "FIRMA_GARANTIZADA", "NINGUNO"
    val showLegalNotes: Boolean = true,
    val showCompanyFooter: Boolean = true,
    val footerPhone: String = "WHATSAPP: 871 141 2774",
    val footerAddress: String = "CARRETERA A BERMEJILLO KM 16 • TORREÓN, COAH. / GÓMEZ PALACIO",
    val pdfExportQuality: String = "300_DPI_ULTRA"
)

data class EfficiencyConfig(
    val autoCopyWhatsAppOnGenerate: Boolean = true,
    val generatePdfOnSave: Boolean = true,
    val ocrAutoConfidenceThreshold: Float = 0.75f,
    val autoClearCacheDays: Int = 15,
    val autoSaveDraftIntervalSec: Int = 5,
    val showFastSearchLinks: Boolean = true
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val activePresetId: String = UiPresetType.DASHBOARD_PRO.id,
    val customPresetName: String = "Mi Preset Personalizado",
    val panelsJson: String = "",
    val themeAccentHex: String = "#FFC700",
    val themeBackgroundMode: String = "DARK_ZINC",
    val themeUiDensity: String = "STANDARD",
    val themeFontScale: Float = 1.0f,
    val themeHaptic: Boolean = true,
    val formatBrandTitle: String = "JSV-AUTOPARTES",
    val formatBrandSub: String = "COMPRA VENTA DE AUTOPARTES ORIGINALES",
    val formatBrandFont: String = "IMPACT_HEAVY",
    val formatBrandColorHex: String = "#FFC700",
    val formatHeaderBgHex: String = "#18181B",
    val formatWatermarkText: String = "JSV-AUTOPARTES ORIGINAL",
    val formatWatermarkOpacity: Float = 0.12f,
    val formatWatermarkAngle: Float = -28f,
    val formatWatermarkEnabled: Boolean = true,
    val formatWatermarkStyle: String = "DIAGONAL_CENTER",
    val formatSealType: String = "SELLO_DIGITAL_DORADO",
    val formatFooterPhone: String = "WHATSAPP: 871 141 2774",
    val formatFooterAddress: String = "CARRETERA A BERMEJILLO KM 16 • TORREÓN, COAH. / GÓMEZ PALACIO",
    val effAutoCopyWhatsApp: Boolean = true,
    val effAutoSaveSec: Int = 5,
    val lastUpdated: Long = System.currentTimeMillis()
)
