package com.example.ui.screens.editor

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreakdownItem
import com.example.data.model.QuoteOption
import com.example.data.model.Vehicle
import com.example.ui.components.DocumentPreviewView
import com.example.ui.components.JsvSectionHeader
import com.example.ui.theme.JsvAmber
import com.example.ui.theme.JsvBlue
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvDarker
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvOnPrimary
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvSurfaceLight
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.viewmodel.JsvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteEditorScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.quoteState.collectAsState()
    val vehicles by viewModel.allVehicles.collectAsState()
    val photos by viewModel.allPhotos.collectAsState()

    var showPreviewModal by remember { mutableStateOf(false) }
    var showSaveOrderDialog by remember { mutableStateOf(false) }
    var showVehiclePicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    var editorWorkflowMode by remember { mutableStateOf("completo") } // "completo", "dinamico_expreso", "dinamico_completo"
    var wizardStep by remember { mutableStateOf(0) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(horizontal = 12.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // --- STOCK / SOBRE PEDIDO TYPE SELECTOR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(JsvSurface)
                .border(1.dp, JsvBorder, RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            val isStock = state.quoteType == "stock_local"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isStock) JsvGold else Color.Transparent)
                    .clickable { viewModel.setQuoteType("stock_local") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🟢 STOCK LOCAL",
                    color = if (isStock) Color.Black else JsvTextMuted,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (!isStock) JsvGold else Color.Transparent)
                    .clickable { viewModel.setQuoteType("sobre_pedido") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🟡 SOBRE PEDIDO",
                    color = if (!isStock) Color.Black else JsvTextMuted,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- WORKFLOW MODE SELECTOR (Completo / Dinámico Expreso / Dinámico Completo) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(JsvSurface)
                .border(1.dp, JsvBorder, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val workflowModes = listOf(
                Triple("completo", "📋 Completo", "Vista tradicional"),
                Triple("dinamico_expreso", "⚡ Dinámico Expreso", "Paso a paso clave"),
                Triple("dinamico_completo", "🔄 Dinámico Completo", "Paso a paso total")
            )
            workflowModes.forEach { (modeKey, modeLabel, _) ->
                val isSelected = editorWorkflowMode == modeKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) JsvGold else Color.Transparent)
                        .clickable {
                            editorWorkflowMode = modeKey
                            wizardStep = 0
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = modeLabel,
                        color = if (isSelected) Color.Black else JsvTextMuted,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- RENDER BASED ON WORKFLOW MODE ---
        when (editorWorkflowMode) {
            "dinamico_expreso" -> {
                val maxSteps = 4
                Text(
                    text = "⚡ MODO DINÁMICO EXPRESO (Puntos Clave)",
                    color = JsvGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                when (wizardStep) {
                    0 -> {
                        // Paso 1: Vehículo Donante
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader(
                                    title = "🚗 Paso 1/4: Vehículo Donante",
                                    trailingContent = {
                                        TextButton(onClick = { showVehiclePicker = true }) {
                                            Text("Catálogo", fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                JsvField(
                                    label = "Vehículo Donante (Marca y Modelo)",
                                    value = state.donorVehicle,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(donorVehicle = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    JsvField(
                                        label = "Años",
                                        value = state.yearRange,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(yearRange = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                    JsvField(
                                        label = "Motor",
                                        value = state.engineRange,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(engineRange = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Paso 2: Autoparte y OEM
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader("⚙️ Paso 2/4: Autoparte y OEM")
                                Spacer(modifier = Modifier.height(8.dp))
                                JsvField(
                                    label = "Nombre de la Pieza",
                                    value = state.partName,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(partName = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                JsvField(
                                    label = "Número OEM / Ref",
                                    value = state.partOem,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(partOem = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    2 -> {
                        // Paso 3: Precios
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader("💰 Paso 3/4: Precios y Desglose Principal")
                                Spacer(modifier = Modifier.height(8.dp))
                                if (state.breakdownItems.isNotEmpty()) {
                                    val firstItem = state.breakdownItems.first()
                                    BreakdownItemEditor(
                                        index = 0,
                                        item = firstItem,
                                        onUpdate = { updated ->
                                            viewModel.updateBreakdownItem(updated)
                                        },
                                        onDelete = {}
                                    )
                                } else {
                                    Button(onClick = { viewModel.addBreakdownItem() }) {
                                        Text("Agregar Partida")
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Paso 4: Garantía y Ubicación
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader("🛡️ Paso 4/4: Garantía y Ubicación")
                                Spacer(modifier = Modifier.height(8.dp))
                                JsvField(
                                    label = "Ubicación en Almacén",
                                    value = state.itemLocation,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(itemLocation = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                JsvField(
                                    label = "Garantía Oficial",
                                    value = state.guaranteeText,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(guaranteeText = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Wizard navigation buttons
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (wizardStep > 0) {
                        OutlinedButton(
                            onClick = { wizardStep-- },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JsvGold)
                        ) {
                            Text("⬅️ Anterior", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Text(
                        text = "Paso ${wizardStep + 1} de $maxSteps",
                        color = JsvTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (wizardStep < maxSteps - 1) {
                        Button(
                            onClick = { wizardStep++ },
                            colors = ButtonDefaults.buttonColors(containerColor = JsvGold)
                        ) {
                            Text("Siguiente ➔", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = { showPreviewModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = JsvGreen)
                        ) {
                            Text("💾 Vista Previa", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }

            "dinamico_completo" -> {
                val maxSteps = 6
                Text(
                    text = "🔄 MODO DINÁMICO COMPLETO (Paso a Paso Total)",
                    color = JsvGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                when (wizardStep) {
                    0 -> {
                        // Paso 1: Encabezado
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader("✏️ Paso 1/6: Encabezado y Folio")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    JsvField(
                                        label = "Folio Ref",
                                        value = state.folioRef,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(folioRef = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                    JsvField(
                                        label = "Fecha",
                                        value = state.dateStr,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(dateStr = v) } },
                                        modifier = Modifier.weight(1.3f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    JsvField(
                                        label = "Título Documento",
                                        value = state.docTitle,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(docTitle = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                    JsvField(
                                        label = "Subtítulo / Referencia",
                                        value = state.docSubtitle,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(docSubtitle = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Paso 2: Ficha del Vehículo
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader(
                                    title = "🚗 Paso 2/6: Ficha del Vehículo Donante",
                                    trailingContent = {
                                        TextButton(onClick = { showVehiclePicker = true }) {
                                            Text("Catálogo", fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                JsvField(
                                    label = "Vehículo Donante (Marca y Modelo)",
                                    value = state.donorVehicle,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(donorVehicle = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    JsvField(
                                        label = "Rango de Años",
                                        value = state.yearRange,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(yearRange = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                    JsvField(
                                        label = "Motor",
                                        value = state.engineRange,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(engineRange = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                JsvField(
                                    label = "Ubicación / Posición de la Pieza",
                                    value = state.itemLocation,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(itemLocation = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    2 -> {
                        // Paso 3: Especificaciones Técnicas
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader("⚙️ Paso 3/6: Especificaciones Técnicas")
                                Spacer(modifier = Modifier.height(8.dp))
                                JsvField(
                                    label = "Nombre de la Pieza / Autoparte",
                                    value = state.partName,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(partName = v) } },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    JsvField(
                                        label = "Número OEM",
                                        value = state.partOem,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(partOem = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                    JsvField(
                                        label = "No. Ingeniería",
                                        value = state.partEng,
                                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(partEng = v) } },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        // Paso 4: Desglose y Precios
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader(
                                    title = "💰 Paso 4/6: Desglose de Partidas",
                                    trailingContent = {
                                        TextButton(onClick = { viewModel.addBreakdownItem() }) {
                                            Text("+ Agregar Partida", fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                state.breakdownItems.forEachIndexed { index, item ->
                                    BreakdownItemEditor(
                                        index = index,
                                        item = item,
                                        onUpdate = { updated -> viewModel.updateBreakdownItem(updated) },
                                        onDelete = { viewModel.removeBreakdownItem(item.id) }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                    4 -> {
                        // Paso 5: Garantía y Términos
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader("🛡️ Paso 5/6: Garantía Oficial")
                                Spacer(modifier = Modifier.height(8.dp))
                                JsvField(
                                    label = "Texto de Garantía",
                                    value = state.guaranteeText,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(guaranteeText = v) } },
                                    singleLine = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    5 -> {
                        // Paso 6: Logística y Envíos
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JsvSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                JsvSectionHeader("📦 Paso 6/6: Logística y Envíos")
                                Spacer(modifier = Modifier.height(8.dp))
                                JsvField(
                                    label = "Condiciones de Envío y Logística",
                                    value = state.logisticsText,
                                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(logisticsText = v) } },
                                    singleLine = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Wizard navigation buttons
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (wizardStep > 0) {
                        OutlinedButton(
                            onClick = { wizardStep-- },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JsvGold)
                        ) {
                            Text("⬅️ Anterior", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Text(
                        text = "Paso ${wizardStep + 1} de $maxSteps",
                        color = JsvTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (wizardStep < maxSteps - 1) {
                        Button(
                            onClick = { wizardStep++ },
                            colors = ButtonDefaults.buttonColors(containerColor = JsvGold)
                        ) {
                            Text("Siguiente ➔", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = { showPreviewModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = JsvGreen)
                        ) {
                            Text("💾 Vista Previa", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }

            else -> {
                // --- MODO COMPLETO (TRADICIONAL) ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = JsvSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        JsvSectionHeader("✏️ Encabezado y Folio")
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JsvField(
                        label = "Folio Ref",
                        value = state.folioRef,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(folioRef = v) } },
                        modifier = Modifier.weight(1f)
                    )
                    JsvField(
                        label = "Fecha",
                        value = state.dateStr,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(dateStr = v) } },
                        modifier = Modifier.weight(1.3f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JsvField(
                        label = "Título Documento",
                        value = state.docTitle,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(docTitle = v) } },
                        modifier = Modifier.weight(1f)
                    )
                    JsvField(
                        label = "Subtítulo / Referencia",
                        value = state.docSubtitle,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(docSubtitle = v) } },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- FICHA DEL VEHÍCULO ---
        Card(
            colors = CardDefaults.cardColors(containerColor = JsvSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                JsvSectionHeader(
                    title = "🚗 Ficha del Vehículo",
                    trailingContent = {
                        TextButton(
                            onClick = { showVehiclePicker = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = JsvDark)
                        ) {
                            Text("Elegir Catálogo", fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                JsvField(
                    label = "Vehículo Donante (Marca y Modelo)",
                    value = state.donorVehicle,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(donorVehicle = v) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JsvField(
                        label = "Rango de Años",
                        value = state.yearRange,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(yearRange = v) } },
                        modifier = Modifier.weight(1f)
                    )
                    JsvField(
                        label = "Motor",
                        value = state.engineRange,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(engineRange = v) } },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                JsvField(
                    label = "Ubicación / Posición de la Pieza",
                    value = state.itemLocation,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(itemLocation = v) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- ESPECIFICACIONES DE LA PIEZA ---
        Card(
            colors = CardDefaults.cardColors(containerColor = JsvSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                JsvSectionHeader("⚙️ Especificaciones Técnicas")
                Spacer(modifier = Modifier.height(8.dp))

                JsvField(
                    label = "Nombre de la Pieza / Autoparte",
                    value = state.partName,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(partName = v) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JsvField(
                        label = "Número OEM",
                        value = state.partOem,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(partOem = v) } },
                        modifier = Modifier.weight(1f)
                    )
                    JsvField(
                        label = "No. Ingeniería",
                        value = state.partEng,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(partEng = v) } },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    JsvField(
                        label = "Sistema Automotriz",
                        value = state.partSystem,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(partSystem = v) } },
                        modifier = Modifier.weight(1f)
                    )
                    JsvField(
                        label = "Condición",
                        value = state.partCondition,
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(partCondition = v) } },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                JsvField(
                    label = "Origen del Repuesto",
                    value = state.partOrigin,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(partOrigin = v) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                JsvField(
                    label = "Recomendación Técnica de Instalación",
                    value = state.techVerifyNote,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(techVerifyNote = v) } },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- FOTO GENERAL & REGISTRO VISUAL ---
        Card(
            colors = CardDefaults.cardColors(containerColor = JsvSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                JsvSectionHeader(
                    title = "📸 Soporte Visual e Inspección",
                    trailingContent = {
                        TextButton(
                            onClick = { showPhotoPicker = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = JsvDark)
                        ) {
                            Text("Banco de Fotos", fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                JsvField(
                    label = "Pie de Foto / Marcado",
                    value = state.imgCaptionText,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(imgCaptionText = v) } },
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.imgStockLocal.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JsvDarkElevated, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = JsvGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Foto asociada: ${state.imgStockLocal}",
                                color = JsvTextBody,
                                fontSize = 10.sp
                            )
                        }
                        IconButton(
                            onClick = { viewModel.updateQuoteState { it.copy(imgStockLocal = "") } },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Quitar", tint = JsvRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- STOCK LOCAL: OPCIONES DE PRECIO ---
        if (state.quoteType == "stock_local") {
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    JsvSectionHeader("💰 Tabla de Opciones y Precios")
                    Spacer(modifier = Modifier.height(8.dp))

                    state.breakdownItems.forEachIndexed { index, item ->
                        BreakdownItemEditor(
                            index = index + 1,
                            item = item,
                            onUpdate = { updated -> viewModel.updateBreakdownItem(updated) },
                            onDelete = { viewModel.removeBreakdownItem(item.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = { viewModel.addBreakdownItem() },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = JsvOnPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AGREGAR CONFIGURACIÓN DE PRECIO", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    // Totals summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JsvDarkElevated, RoundedCornerShape(6.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total al Cambio:", color = JsvTextMuted, fontSize = 10.sp)
                            Text("$${String.format("%,.0f", state.calculateTotalStockCambio())} MXN", color = JsvGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Venta Directa:", color = JsvTextMuted, fontSize = 10.sp)
                            Text("$${String.format("%,.0f", state.calculateTotalStockVenta())} MXN", color = JsvGreen, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        } else {
            // --- SOBRE PEDIDO: OPCIONES Y PROCESO ---
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    JsvSectionHeader("📦 Opciones Sobre Pedido")
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Comisión de Gestión y Logística: ${state.porcentajeComision.toInt()}%",
                        color = JsvTextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = state.porcentajeComision.toFloat(),
                        onValueChange = { v -> viewModel.updateQuoteState { it.copy(porcentajeComision = v.toDouble()) } },
                        valueRange = 0f..35f,
                        steps = 6,
                        colors = SliderDefaults.colors(thumbColor = JsvGold, activeTrackColor = JsvGold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    state.options.forEachIndexed { index, opt ->
                        QuoteOptionEditor(
                            index = index + 1,
                            option = opt,
                            onUpdate = { updated -> viewModel.updateOption(updated) },
                            onDelete = { viewModel.removeOption(opt.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = { viewModel.addOption() },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = JsvOnPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AGREGAR OPCIÓN DE PROVEEDOR", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sobre pedido total calculation
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JsvDarkElevated, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal Base:", color = JsvTextMuted, fontSize = 10.sp)
                            Text("$${String.format("%,.0f", state.calculateTotalSobrePedidoBase())} MXN", color = JsvTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Comisión Logística (${state.porcentajeComision.toInt()}%):", color = JsvTextMuted, fontSize = 10.sp)
                            Text("$${String.format("%,.0f", state.calculateSobrePedidoComision())} MXN", color = JsvTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = JsvBorder, modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TOTAL FINAL COTIZADO:", color = JsvGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Text("$${String.format("%,.0f", state.calculateTotalSobrePedidoFinal())} MXN", color = JsvGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    JsvSectionHeader("📋 Pasos del Proceso de Pedido")
                    Spacer(modifier = Modifier.height(6.dp))

                    state.procesoPasos.forEach { paso ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JsvDarkElevated, RoundedCornerShape(6.dp))
                                .border(1.dp, if (paso.marked) JsvGold else JsvBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = paso.marked,
                                onCheckedChange = { viewModel.toggleProcessStep(paso.id) },
                                colors = CheckboxDefaults.colors(checkedColor = JsvGold, checkmarkColor = Color.Black)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(paso.title, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                Text(paso.description, color = JsvTextMuted, fontSize = 9.sp)
                            }
                            IconButton(
                                onClick = { viewModel.removeProcessStep(paso.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = JsvRed)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- LOGÍSTICA Y GARANTÍA ---
        Card(
            colors = CardDefaults.cardColors(containerColor = JsvSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                JsvSectionHeader("🛡️ Logística y Garantía")
                Spacer(modifier = Modifier.height(8.dp))

                JsvField(
                    label = "Título Logística",
                    value = state.logisticsTitle,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(logisticsTitle = v) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                JsvField(
                    label = "Condiciones de Entrega",
                    value = state.logisticsText,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(logisticsText = v) } },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                JsvField(
                    label = "Título Garantía",
                    value = state.guaranteeTitle,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(guaranteeTitle = v) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                JsvField(
                    label = "Términos de Garantía",
                    value = state.guaranteeText,
                    onValueChange = { v -> viewModel.updateQuoteState { it.copy(guaranteeText = v) } },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

        Spacer(modifier = Modifier.height(16.dp))

        // --- ACTION BUTTONS ---
        Button(
            onClick = { showPreviewModal = true },
            colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = JsvOnPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("👁️ PREVISUALIZAR DOCUMENTO OFICIAL", color = JsvOnPrimary, fontWeight = FontWeight.Black, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { showSaveOrderDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = JsvGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = JsvOnPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("GUARDAR PEDIDO", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }

            Button(
                onClick = {
                    val shareText = viewModel.getWhatsAppShareText()
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Compartir Cotización JSV"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = JsvBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = JsvOnPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("COMPARTIR", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.resetQuoteState() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = JsvRed),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(42.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = JsvRed)
            Spacer(modifier = Modifier.width(6.dp))
            Text("RESTABLECER FORMULARIO", color = JsvRed, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // --- PREVIEW MODAL BOTTOM SHEET ---
    if (showPreviewModal) {
        ModalBottomSheet(
            onDismissRequest = { showPreviewModal = false },
            containerColor = JsvDarker
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vista Previa: Cotización JSV",
                        color = JsvGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    IconButton(onClick = { showPreviewModal = false }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = JsvTextWhite)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                DocumentPreviewView(state = state, modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        val shareText = viewModel.getWhatsAppShareText()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Compartir Cotización"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compartir por WhatsApp / Enviar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // --- SAVE ORDER DIALOG ---
    if (showSaveOrderDialog) {
        var clienteNombre by remember { mutableStateOf("") }
        var clienteTelefono by remember { mutableStateOf("") }
        var clienteDireccion by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSaveOrderDialog = false },
            containerColor = JsvSurface,
            title = { Text("Guardar como Pedido", color = JsvGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Se registrará en el módulo de Seguimiento con snapshot completo auditable.",
                        color = JsvTextMuted,
                        fontSize = 10.5.sp
                    )
                    JsvField(label = "Nombre del Cliente", value = clienteNombre, onValueChange = { clienteNombre = it })
                    JsvField(label = "Teléfono WhatsApp", value = clienteTelefono, onValueChange = { clienteTelefono = it }, keyboardType = KeyboardType.Phone)
                    JsvField(label = "Dirección de Entrega", value = clienteDireccion, onValueChange = { clienteDireccion = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveQuoteAsOrder(clienteNombre, clienteTelefono, clienteDireccion)
                        showSaveOrderDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGreen)
                ) {
                    Text("GUARDAR Y SEGUIR", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveOrderDialog = false }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }

    // --- VEHICLE PICKER MODAL ---
    if (showVehiclePicker) {
        AlertDialog(
            onDismissRequest = { showVehiclePicker = false },
            containerColor = JsvSurface,
            title = { Text("Seleccionar Vehículo Donante", color = JsvGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (vehicles.isEmpty()) {
                        Text("No hay vehículos registrados en el catálogo.", color = JsvTextMuted)
                    } else {
                        vehicles.forEach { v ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.applyVehicle(v)
                                        showVehiclePicker = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = JsvGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("${v.marca} ${v.modelo}", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("${v.anios} | ${v.motor}", color = JsvTextMuted, fontSize = 9.sp)
                                }
                            }
                            HorizontalDivider(color = JsvBorder)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVehiclePicker = false }) {
                    Text("CERRAR", color = JsvGold)
                }
            }
        )
    }

    // --- PHOTO PICKER MODAL ---
    if (showPhotoPicker) {
        AlertDialog(
            onDismissRequest = { showPhotoPicker = false },
            containerColor = JsvSurface,
            title = { Text("Banco de Fotos de Inspección", color = JsvGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    photos.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.applyPhotoToQuote(p)
                                    showPhotoPicker = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = JsvBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(p.title, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("${p.category} | ${p.filename}", color = JsvTextMuted, fontSize = 9.sp)
                            }
                        }
                        HorizontalDivider(color = JsvBorder)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoPicker = false }) {
                    Text("CERRAR", color = JsvGold)
                }
            }
        )
    }
}
