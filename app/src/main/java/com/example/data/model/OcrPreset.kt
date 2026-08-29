package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Entity(tableName = "ocr_presets")
data class OcrPreset(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val documentType: String = "FICHA_TECNICA", // "FICHA_TECNICA", "COTIZACION_CHAT", "GARANTIA_SERIAL", "TICKET_FACTURA", "PERSONALIZADO"
    val zonesJson: String,                      // JSON list of OcrKeyZone
    val triggersJson: String = "[\"OEM\", \"PRECIO\", \"TOTAL\", \"MOTOR\", \"VIN\", \"ANTICIPO\"]",
    val outputTemplate: String = "FORMATO_JSV_ESTANDAR",
    val isDefault: Boolean = false,
    val createdDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)

data class OcrKeyZone(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val label: String,
    val xPercent: Float,         // 0.0f .. 1.0f
    val yPercent: Float,         // 0.0f .. 1.0f
    val widthPercent: Float,     // 0.0f .. 1.0f
    val heightPercent: Float,    // 0.0f .. 1.0f
    val fieldMapping: String,    // "donorVehicle", "partName", "partOem", "priceVenta", "priceCambio", "warranty", "clientName", "clientPhone", "notes"
    val keywords: List<String> = emptyList()
)

data class OcrExtractedData(
    val donorVehicle: String = "",
    val partName: String = "",
    val partOem: String = "",
    val priceVenta: Double = 0.0,
    val priceCambio: Double = 0.0,
    val warranty: String = "30 días",
    val clientName: String = "",
    val clientPhone: String = "",
    val detectedTriggers: List<String> = emptyList(),
    val rawTranscription: String = "",
    val compiledFormattedText: String = "",
    val confidencePercent: Int = 96
)

enum class WorkflowNodeType {
    TRIGGER_OCR,
    PARSE_CLASSIFY,
    ROUTER_CONDITIONAL,
    ROOM_MEMORY_SYNC,
    WHATSAPP_BROADCAST,
    PUSH_NOTIFICATION_ALERT,
    ORDER_DISPATCH
}

enum class NodeStatus {
    IDLE,
    RUNNING,
    SUCCESS,
    FAILED
}

data class WorkflowNode(
    val id: String,
    val type: WorkflowNodeType,
    val title: String,
    val subtitle: String,
    val status: NodeStatus = NodeStatus.IDLE,
    val outputData: String = "",
    val executionTimeMs: Long = 0,
    val channel: String = "PRIMARY" // "PRIMARY", "BRANCH_A", "BRANCH_B"
)

data class WorkflowExecutionState(
    val isExecuting: Boolean = false,
    val currentStepIndex: Int = -1,
    val nodes: List<WorkflowNode> = emptyList(),
    val logs: List<String> = emptyList(),
    val isComplete: Boolean = false
)
