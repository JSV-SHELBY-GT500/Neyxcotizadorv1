package com.example.ui.screens.ocr

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NodeStatus
import com.example.data.model.WorkflowNode
import com.example.data.model.WorkflowNodeType
import com.example.ui.theme.JsvAmber
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvOnPrimary
import com.example.ui.theme.JsvPurple
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.viewmodel.JsvViewModel
import com.example.ui.viewmodel.SampleScreenshot

@Composable
fun OcrScannerWorkflowScreen(viewModel: JsvViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Escáner & Presets, 1: Mapeador Mental de Workflows

    val selectedScreenshot by viewModel.selectedScreenshot.collectAsState()
    val ocrData by viewModel.ocrExtractedData.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val workflowState by viewModel.workflowState.collectAsState()
    val sampleScreenshots = viewModel.sampleScreenshots

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
    ) {
        // Sub-navigation tab row
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = JsvSurface,
            contentColor = JsvGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                    height = 3.dp,
                    color = JsvGold
                )
            }
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("1. Escáner & Presets", fontSize = 11.sp, fontWeight = if (activeSubTab == 0) FontWeight.Bold else FontWeight.Normal)
                    }
                },
                selectedContentColor = JsvGold,
                unselectedContentColor = JsvTextMuted
            )

            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("2. Mapeador Workflow", fontSize = 11.sp, fontWeight = if (activeSubTab == 1) FontWeight.Bold else FontWeight.Normal)
                    }
                },
                selectedContentColor = JsvGold,
                unselectedContentColor = JsvTextMuted
            )
        }

        if (activeSubTab == 0) {
            // TAB 1: Escáner de Capturas y Presets de Puntos Clave
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // Screenshot Selector Library
                item {
                    Text("Selecciona una Captura de Pantalla para Escaneo:", color = JsvTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sampleScreenshots) { ss ->
                            val isSelected = ss.id == selectedScreenshot.id
                            Card(
                                modifier = Modifier
                                    .width(210.dp)
                                    .clickable { viewModel.selectScreenshot(ss) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) JsvSurface else JsvSurface.copy(alpha = 0.6f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) JsvGold else JsvBorder
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isSelected) JsvGold else JsvDarkElevated)
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(ss.category, color = if (isSelected) JsvOnPrimary else JsvTextMuted, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        if (isSelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = JsvGold, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(ss.title, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, maxLines = 1)
                                    Text(ss.description, color = JsvTextBody, fontSize = 8.5.sp, maxLines = 2)
                                }
                            }
                        }
                    }
                }

                // Interactive Screenshot Viewport with Preset Key-Zone Overlays
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = JsvSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CropFree, contentDescription = null, tint = JsvGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Viewport de Captura con Preset de Puntos Clave", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }

                                Text("Preset: ${selectedScreenshot.presetId.replace("preset_", "").uppercase()}", color = JsvGold, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Canvas displaying Simulated Document with Position Overlays
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(JsvDarkElevated)
                                    .border(1.dp, JsvBorder, RoundedCornerShape(10.dp))
                            ) {
                                // Raw simulated document text
                                Text(
                                    text = selectedScreenshot.simulatedImageText,
                                    color = JsvTextWhite.copy(alpha = 0.85f),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(10.dp)
                                )

                                // Keypoint Canvas bounding box overlay
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val stroke = Stroke(
                                        width = 2.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    )

                                    // Draw Zone 1: Header / Vehicle
                                    drawRect(
                                        color = Color(0xFFD0BCFF),
                                        topLeft = Offset(size.width * 0.03f, size.height * 0.05f),
                                        size = Size(size.width * 0.94f, size.height * 0.28f),
                                        style = stroke
                                    )

                                    // Draw Zone 2: Part OEM & Prices
                                    drawRect(
                                        color = Color(0xFFFFB74D),
                                        topLeft = Offset(size.width * 0.03f, size.height * 0.38f),
                                        size = Size(size.width * 0.94f, size.height * 0.30f),
                                        style = stroke
                                    )

                                    // Draw Zone 3: Warranty / Triggers
                                    drawRect(
                                        color = Color(0xFF81C784),
                                        topLeft = Offset(size.width * 0.03f, size.height * 0.72f),
                                        size = Size(size.width * 0.94f, size.height * 0.24f),
                                        style = stroke
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.runOcrScan() },
                                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    enabled = !isScanning
                                ) {
                                    if (isScanning) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = JsvOnPrimary, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ESCANEANDO...", color = JsvOnPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ANALIZAR CAPTURA", color = JsvOnPrimary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                Button(
                                    onClick = { activeSubTab = 1 },
                                    colors = ButtonDefaults.buttonColors(containerColor = JsvBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(42.dp)
                                ) {
                                    Icon(Icons.Default.AccountTree, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VER WORKFLOW", color = JsvOnPrimary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Extracted & Classified Data Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = JsvSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DataObject, contentDescription = null, tint = JsvGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Datos Extraídos y Clasificados por Preset", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(JsvGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("${ocrData.confidencePercent}% CONFIANZA", color = JsvGreen, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Triggers chips
                            Text("Triggers / Palabras Clave Detectadas:", color = JsvTextMuted, fontSize = 9.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(ocrData.detectedTriggers) { trig ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(JsvGold.copy(alpha = 0.2f))
                                            .border(1.dp, JsvGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("🏷️ $trig", color = JsvGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OcrDataFieldRow("Vehículo Donante", ocrData.donorVehicle)
                            OcrDataFieldRow("Pieza Clasificada", ocrData.partName)
                            OcrDataFieldRow("Número de Parte OEM", ocrData.partOem.ifBlank { "N/A" })
                            OcrDataFieldRow("Precio Venta Sugerido", "$${ocrData.priceVenta} MXN")
                            OcrDataFieldRow("Precio a Cambio", "$${ocrData.priceCambio} MXN")
                            OcrDataFieldRow("Garantía", ocrData.warranty)
                            if (ocrData.clientName.isNotBlank()) {
                                OcrDataFieldRow("Cliente", "${ocrData.clientName} (${ocrData.clientPhone})")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Formatted Output Text Box
                            Text("Bloque de Texto Compilado:", color = JsvTextMuted, fontSize = 9.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(JsvDarkElevated)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = ocrData.compiledFormattedText,
                                    color = JsvTextWhite,
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // CTAs
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(ocrData.compiledFormattedText))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JsvDarkElevated),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = JsvTextWhite, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("COPIAR", color = JsvTextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.transferOcrDataToQuote() },
                                    colors = ButtonDefaults.buttonColors(containerColor = JsvGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(38.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("APLICAR AL COTIZADOR", color = JsvOnPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TAB 2: Mapeador Visual de Workflows (Mental Map / Diagrama de Flujo)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Workflow Engine Header & Runner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = JsvSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(JsvGold.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Psychology, contentDescription = null, tint = JsvGold, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Diagrama Mental de Workflows", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Flujo multicanal automatizado con triggers", color = JsvTextMuted, fontSize = 9.sp)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.executeAutomatedWorkflow() },
                                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(36.dp),
                                    enabled = !workflowState.isExecuting
                                ) {
                                    if (workflowState.isExecuting) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = JsvOnPrimary, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("EJECUTANDO...", color = JsvOnPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("EJECUTAR FLUJO", color = JsvOnPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (workflowState.isExecuting) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = JsvGold,
                                    trackColor = JsvDarkElevated
                                )
                            }
                        }
                    }
                }

                // Visual Workflow Nodes (Flowchart Diagram)
                item {
                    Text("Nodos del Flujo de Trabajo Automático:", color = JsvTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        workflowState.nodes.forEachIndexed { index, node ->
                            WorkflowNodeCard(node = node)
                            if (index < workflowState.nodes.size - 1) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (node.status == NodeStatus.SUCCESS) JsvGold else JsvTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Workflow Execution Logs Console
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = JsvSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Code, contentDescription = null, tint = JsvBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Consola de Ejecución en Vivo", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                if (workflowState.isComplete) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(JsvGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("FLOW 100% OK", color = JsvGreen, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(JsvDarkElevated)
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    workflowState.logs.forEach { logLine ->
                                        Text(
                                            text = logLine,
                                            color = if (logLine.startsWith("✔️") || logLine.startsWith("🏁")) JsvGreen else if (logLine.startsWith("🚀")) JsvGold else JsvTextWhite,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            if (workflowState.isComplete) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.transferOcrDataToQuote() },
                                    colors = ButtonDefaults.buttonColors(containerColor = JsvGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("TRANSFERIR DATA CLASIFICADA AL COTIZADOR", color = JsvOnPrimary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkflowNodeCard(node: WorkflowNode) {
    val (nodeBorder, nodeBg, statusIcon, statusText) = when (node.status) {
        NodeStatus.RUNNING -> Quadruple(JsvGold, JsvGold.copy(alpha = 0.1f), Icons.Default.Refresh, "PROCESANDO...")
        NodeStatus.SUCCESS -> Quadruple(JsvGreen, JsvGreen.copy(alpha = 0.08f), Icons.Default.CheckCircle, "COMPLETADO")
        NodeStatus.FAILED -> Quadruple(JsvRed, JsvRed.copy(alpha = 0.1f), Icons.Default.Warning, "ERROR")
        NodeStatus.IDLE -> Quadruple(JsvBorder, JsvSurface, Icons.Default.TouchApp, "EN ESPERA")
    }

    val channelTag = when (node.channel) {
        "BRANCH_A" -> "Canal: Memoria & Seguridad"
        "BRANCH_B" -> "Canal: Cliente & Mensajería"
        else -> "Canal Principal"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = nodeBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, nodeBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(JsvDarkElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(statusIcon, contentDescription = null, tint = nodeBorder, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(node.title, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    Text(node.subtitle, color = JsvTextBody, fontSize = 8.5.sp)
                    if (node.outputData.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Output: ${node.outputData}", color = JsvGold, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(nodeBorder.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(statusText, color = nodeBorder, fontSize = 7.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(channelTag, color = JsvTextMuted, fontSize = 7.sp)
            }
        }
    }
}

@Composable
private fun OcrDataFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = JsvTextMuted, fontSize = 10.sp)
        Text(value, color = JsvTextWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
