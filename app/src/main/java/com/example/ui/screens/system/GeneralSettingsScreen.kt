package com.example.ui.screens.system

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppThemeConfig
import com.example.data.model.DeliverableFormatConfig
import com.example.data.model.EfficiencyConfig
import com.example.data.model.UiPanelModule
import com.example.data.model.UiPresetType
import com.example.ui.components.DocumentPreviewView
import com.example.ui.theme.JsvAmber
import com.example.ui.theme.JsvBlack
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvDarker
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGoldDark
import com.example.ui.theme.JsvGrayLight
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvOnPrimary
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvSurfaceLight
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.theme.JsvWhite
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun GeneralSettingsScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val activePreset by viewModel.activeUiPreset.collectAsState()
    val modularPanels by viewModel.modularPanels.collectAsState()
    val appTheme by viewModel.appThemeConfig.collectAsState()
    val deliverableFormat by viewModel.deliverableFormatConfig.collectAsState()
    val efficiency by viewModel.efficiencyConfig.collectAsState()
    val quoteState by viewModel.quoteState.collectAsState()

    var selectedSection by remember { mutableIntStateOf(0) }
    var showCustomPresetDialog by remember { mutableStateOf(false) }
    var customPresetNameInput by remember { mutableStateOf("Mi Preset Taller Pro") }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .testTag("general_settings_screen")
    ) {
        // --- HEADER BANNER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(JsvDark)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(JsvGold)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AJUSTES",
                                color = JsvOnPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONFIGURACIÓN & CANVAS",
                            color = JsvTextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Personalización modular, diseño visual y formatos de entrega",
                        color = JsvTextMuted,
                        fontSize = 9.5.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.reintegrateColorsFromTxtSpec() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = JsvGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JsvGold),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("reintegrate_colors_button")
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reintegrar Paleta", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SUB TABS NAVIGATION ---
        val subTabs = listOf(
            Pair("📐 Interfaz & Canvas", Icons.Default.Dashboard),
            Pair("📄 Formatos & Marca", Icons.Default.Description),
            Pair("🎨 Paleta & Estilo App", Icons.Default.Palette),
            Pair("⚡ Eficiencia", Icons.Default.Bolt)
        )

        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = JsvDarkElevated,
            contentColor = JsvGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = JsvGold,
                    height = 3.dp
                )
            }
        ) {
            subTabs.forEachIndexed { index, pair ->
                val isSelected = selectedSection == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedSection = index },
                    text = {
                        Text(
                            text = pair.first,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) JsvGold else JsvTextMuted
                        )
                    },
                    modifier = Modifier.testTag("settings_subtab_$index")
                )
            }
        }

        // --- SECTION CONTENT ---
        Box(modifier = Modifier.weight(1f)) {
            when (selectedSection) {
                0 -> ModularCanvasSettingsSection(
                    activePreset = activePreset,
                    panels = modularPanels,
                    onApplyPreset = { viewModel.applyUiPreset(it) },
                    onToggleVisibility = { viewModel.togglePanelVisibility(it) },
                    onSetShowFirst = { id, show -> viewModel.setPanelShowFirst(id, show) },
                    onMovePanel = { id, up -> viewModel.movePanel(id, up) },
                    onOpenSaveDialog = { showCustomPresetDialog = true }
                )
                1 -> DeliverableFormatSettingsSection(
                    formatConfig = deliverableFormat,
                    quoteState = quoteState,
                    onUpdateFormat = { viewModel.updateDeliverableFormat(it) }
                )
                2 -> AppThemeSettingsSection(
                    themeConfig = appTheme,
                    onUpdateTheme = { viewModel.updateAppTheme(it) },
                    onReintegratePalette = { viewModel.reintegrateColorsFromTxtSpec() }
                )
                3 -> EfficiencySettingsSection(
                    efficiency = efficiency,
                    onUpdateEfficiency = { viewModel.updateEfficiency(it) },
                    onResetDefaults = { showResetConfirmDialog = true }
                )
            }
        }
    }

    // --- SAVE CUSTOM PRESET DIALOG ---
    if (showCustomPresetDialog) {
        AlertDialog(
            onDismissRequest = { showCustomPresetDialog = false },
            containerColor = JsvDark,
            title = {
                Text(
                    text = "Guardar Preset de Canvas",
                    color = JsvGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ingresa un nombre para registrar el acomodo actual de paneles y prioridades de tu interfaz modular:",
                        color = JsvTextBody,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customPresetNameInput,
                        onValueChange = { customPresetNameInput = it },
                        label = { Text("Nombre del Preset") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedTextColor = JsvTextWhite,
                            unfocusedTextColor = JsvTextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_preset_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customPresetNameInput.isNotBlank()) {
                            viewModel.saveCustomCanvasPreset(customPresetNameInput.trim())
                            showCustomPresetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold, contentColor = JsvOnPrimary)
                ) {
                    Text("Guardar Preset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCustomPresetDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = JsvTextMuted)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- RESET CONFIRMATION DIALOG ---
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            containerColor = JsvDark,
            title = {
                Text("Restaurar Valores de Fábrica", color = JsvRed, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "¿Estás seguro de restablecer toda la configuración de la app, paleta original de colores (Amarillo/Gris/Negro), y acomodo de paneles a los valores iniciales oficiales?",
                    color = JsvTextBody,
                    fontSize = 12.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToFactoryDefaults()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvRed, contentColor = Color.White)
                ) {
                    Text("Restablecer Todo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetConfirmDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = JsvTextMuted)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// SUBSECTION 1: MODULAR CANVAS & PRESETS
// ==========================================
@Composable
private fun ModularCanvasSettingsSection(
    activePreset: UiPresetType,
    panels: List<UiPanelModule>,
    onApplyPreset: (UiPresetType) -> Unit,
    onToggleVisibility: (String) -> Unit,
    onSetShowFirst: (String, Boolean) -> Unit,
    onMovePanel: (String, Boolean) -> Unit,
    onOpenSaveDialog: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- PRESET SELECTOR CAROUSEL ---
        Text(
            text = "1. PRESETS DE INTERFAZ MODULAR",
            color = JsvGold,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            UiPresetType.entries.forEach { preset ->
                val isSelected = activePreset == preset
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onApplyPreset(preset) }
                        .testTag("preset_card_${preset.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) JsvDarkElevated else JsvDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) JsvGold else JsvBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = getIconForName(preset.icon),
                                contentDescription = null,
                                tint = if (isSelected) JsvGold else JsvTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(JsvGold)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("ACTIVO", color = JsvOnPrimary, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = preset.title,
                            color = JsvTextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = preset.description,
                            color = JsvTextMuted,
                            fontSize = 8.5.sp,
                            lineHeight = 11.sp
                        )
                    }
                }
            }
        }

        // --- INTERACTIVE CANVAS SIMULATOR ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brush, contentDescription = null, tint = JsvGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CANVAS INTERACTIVO DE ACOMODO",
                            color = JsvGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.5.sp
                        )
                    }
                    Button(
                        onClick = onOpenSaveDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = JsvGold, contentColor = JsvOnPrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("save_canvas_preset_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Guardar Preset", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lienzo visual en tiempo real de los módulos cargados en pantalla. Los módulos marcados con 'Mostrar Primero' se anclan al inicio del flujo.",
                    color = JsvTextMuted,
                    fontSize = 8.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas Mockup Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(JsvDarker)
                        .border(1.dp, JsvBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Simulated Top Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JsvDark)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(JsvGold, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("JSV", color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("STUDIO CANVAS", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("${panels.count { it.isVisible }} MÓDULOS ACTIVOS", color = JsvGreen, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }

                        // Simulated Modular Rows Flow
                        val visiblePanels = panels.filter { it.isVisible }.sortedWith(
                            compareByDescending<UiPanelModule> { it.isPriorityFirst }.thenBy { it.orderIndex }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            visiblePanels.forEach { panel ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (panel.isPriorityFirst) JsvDarkElevated else JsvDark)
                                        .border(1.dp, if (panel.isPriorityFirst) JsvGold else JsvBorder, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = getIconForName(panel.iconName),
                                            contentDescription = null,
                                            tint = if (panel.isPriorityFirst) JsvGold else JsvTextWhite,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = panel.title,
                                            color = if (panel.isPriorityFirst) JsvGold else JsvTextWhite,
                                            fontSize = 8.sp,
                                            fontWeight = if (panel.isPriorityFirst) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (panel.isPriorityFirst) {
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("★", color = JsvGold, fontSize = 8.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DETAILED MODULAR PANELS LIST (ORDER & VISIBILITY) ---
        Text(
            text = "2. GESTIÓN Y ACOMODO DETALLADO DE PANELES",
            color = JsvGold,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )

        panels.forEachIndexed { index, panel ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("panel_module_${panel.id}"),
                colors = CardDefaults.cardColors(containerColor = JsvDark),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (panel.isPriorityFirst) JsvGold else JsvBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (panel.isVisible) JsvDarkElevated else Color.Transparent)
                                .border(1.dp, if (panel.isPriorityFirst) JsvGold else JsvBorder, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForName(panel.iconName),
                                contentDescription = null,
                                tint = if (panel.isPriorityFirst) JsvGold else if (panel.isVisible) JsvTextWhite else JsvTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = panel.title,
                                    color = if (panel.isVisible) JsvTextWhite else JsvTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (panel.isPriorityFirst) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(JsvGold)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("PRIMERO", color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Text(
                                text = panel.subtitle,
                                color = JsvTextMuted,
                                fontSize = 8.5.sp
                            )
                        }
                    }

                    // Action Controls: Priority Star, Move Up/Down, Visibility Switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Priority First Toggle
                        IconButton(
                            onClick = { onSetShowFirst(panel.id, !panel.isPriorityFirst) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Mostrar Primero",
                                tint = if (panel.isPriorityFirst) JsvGold else JsvTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Move Up
                        IconButton(
                            onClick = { onMovePanel(panel.id, true) },
                            enabled = index > 0,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Subir",
                                tint = if (index > 0) JsvTextWhite else JsvBorder,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Move Down
                        IconButton(
                            onClick = { onMovePanel(panel.id, false) },
                            enabled = index < panels.size - 1,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Bajar",
                                tint = if (index < panels.size - 1) JsvTextWhite else JsvBorder,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Visibility Toggle
                        Switch(
                            checked = panel.isVisible,
                            onCheckedChange = { onToggleVisibility(panel.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = JsvGold,
                                checkedTrackColor = JsvGoldDark,
                                uncheckedThumbColor = JsvTextMuted,
                                uncheckedTrackColor = JsvDarkElevated
                            ),
                            modifier = Modifier.testTag("switch_panel_${panel.id}")
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SUBSECTION 2: DELIVERABLE FORMATS & BRAND
// ==========================================
@Composable
private fun DeliverableFormatSettingsSection(
    formatConfig: DeliverableFormatConfig,
    quoteState: com.example.data.model.QuoteState,
    onUpdateFormat: (DeliverableFormatConfig) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Separation Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(JsvDarkElevated)
                .border(1.dp, JsvGold, RoundedCornerShape(6.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = JsvGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SEPARACIÓN TOTAL: FORMATO ENTREGABLE VS INTERFAZ",
                        color = JsvGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Estos ajustes gobiernan los documentos impresos, cotizaciones en PDF, marcas de agua, sellos digitales y tipografía pesada Impact.",
                        color = JsvTextBody,
                        fontSize = 8.5.sp
                    )
                }
            }
        }

        // --- BRAND HEADER & IMPACT TYPOGRAPHY ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. IDENTIDAD DE MARCA & TIPOGRAFÍA IMPACT",
                    color = JsvGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                )

                OutlinedTextField(
                    value = formatConfig.brandTitle,
                    onValueChange = { onUpdateFormat(formatConfig.copy(brandTitle = it)) },
                    label = { Text("Nombre de Marca (Impact Display)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JsvGold,
                        unfocusedBorderColor = JsvBorder,
                        focusedTextColor = JsvTextWhite,
                        unfocusedTextColor = JsvTextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("brand_title_input")
                )

                // Impact Font Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = formatConfig.brandTitle.uppercase(),
                            color = JsvGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "PREVIEW TIPOGRAFÍA IMPACT GRUESA / CONDENSED DISPLAY",
                            color = Color(0xFF94A3B8),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = formatConfig.brandSub,
                    onValueChange = { onUpdateFormat(formatConfig.copy(brandSub = it)) },
                    label = { Text("Subtítulo / Especialidad") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JsvGold,
                        unfocusedBorderColor = JsvBorder,
                        focusedTextColor = JsvTextWhite,
                        unfocusedTextColor = JsvTextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("brand_sub_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = formatConfig.footerPhone,
                        onValueChange = { onUpdateFormat(formatConfig.copy(footerPhone = it)) },
                        label = { Text("WhatsApp Oficial") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedTextColor = JsvTextWhite,
                            unfocusedTextColor = JsvTextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formatConfig.footerAddress,
                        onValueChange = { onUpdateFormat(formatConfig.copy(footerAddress = it)) },
                        label = { Text("Ubicación") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedTextColor = JsvTextWhite,
                            unfocusedTextColor = JsvTextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1.3f)
                    )
                }
            }
        }

        // --- WATERMARK (MARCA DE AGUA) CONTROLS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "2. MARCA DE AGUA (WATERMARK)",
                            color = JsvGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "Sello de inviolabilidad sobre el canvas del documento",
                            color = JsvTextMuted,
                            fontSize = 8.5.sp
                        )
                    }

                    Switch(
                        checked = formatConfig.watermarkEnabled,
                        onCheckedChange = { onUpdateFormat(formatConfig.copy(watermarkEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = JsvGold, checkedTrackColor = JsvGoldDark),
                        modifier = Modifier.testTag("watermark_toggle_switch")
                    )
                }

                if (formatConfig.watermarkEnabled) {
                    OutlinedTextField(
                        value = formatConfig.watermarkText,
                        onValueChange = { onUpdateFormat(formatConfig.copy(watermarkText = it)) },
                        label = { Text("Texto de la Marca de Agua") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JsvGold,
                            unfocusedBorderColor = JsvBorder,
                            focusedTextColor = JsvTextWhite,
                            unfocusedTextColor = JsvTextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("watermark_text_input")
                    )

                    // Opacity Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Opacidad de Marca de Agua:", color = JsvTextBody, fontSize = 9.5.sp)
                            Text("${(formatConfig.watermarkOpacity * 100).toInt()}%", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                        }
                        Slider(
                            value = formatConfig.watermarkOpacity,
                            onValueChange = { onUpdateFormat(formatConfig.copy(watermarkOpacity = it)) },
                            valueRange = 0.05f..0.35f,
                            colors = SliderDefaults.colors(thumbColor = JsvGold, activeTrackColor = JsvGold),
                            modifier = Modifier.testTag("watermark_opacity_slider")
                        )
                    }

                    // Angle Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ángulo de Inclinación:", color = JsvTextBody, fontSize = 9.5.sp)
                            Text("${formatConfig.watermarkAngle.toInt()}°", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                        }
                        Slider(
                            value = formatConfig.watermarkAngle,
                            onValueChange = { onUpdateFormat(formatConfig.copy(watermarkAngle = it)) },
                            valueRange = -60f..60f,
                            colors = SliderDefaults.colors(thumbColor = JsvGold, activeTrackColor = JsvGold),
                            modifier = Modifier.testTag("watermark_angle_slider")
                        )
                    }
                }
            }
        }

        // --- DIGITAL SEALS & BADGES ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. SELLOS DIGITALES DE GARANTÍA",
                    color = JsvGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                )

                val seals = listOf(
                    Pair("SELLO_DIGITAL_DORADO", "Sello Dorado 30 Días"),
                    Pair("SELLO_OFICIAL_NEGRO", "Holograma Negro OEM"),
                    Pair("NINGUNO", "Sin Sello")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    seals.forEach { seal ->
                        val isSelected = formatConfig.sealType == seal.first
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) JsvDarkElevated else Color.Transparent)
                                .border(1.dp, if (isSelected) JsvGold else JsvBorder, RoundedCornerShape(6.dp))
                                .clickable { onUpdateFormat(formatConfig.copy(sealType = seal.first)) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = seal.second,
                                color = if (isSelected) JsvGold else JsvTextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // --- LIVE DELIVERABLE DOCUMENT PREVIEW ---
        Text(
            text = "4. VISTA PREVIA EN VIVO DEL ENTREGABLE",
            color = JsvGold,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )

        DocumentPreviewView(
            state = quoteState,
            formatConfig = formatConfig,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ==========================================
// SUBSECTION 3: APP THEME & PALETTE (YELLOW, DARK GRAY, BLACK, LIGHT GRAY, WHITE)
// ==========================================
@Composable
private fun AppThemeSettingsSection(
    themeConfig: AppThemeConfig,
    onUpdateTheme: (AppThemeConfig) -> Unit,
    onReintegratePalette: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Color Palette Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PALETA DE INTERFAZ OFICIAL JSV",
                        color = JsvGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.5.sp
                    )
                    Button(
                        onClick = onReintegratePalette,
                        colors = ButtonDefaults.buttonColors(containerColor = JsvGold, contentColor = JsvOnPrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("Reintegrar", fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                    }
                }

                Text(
                    text = "Combinación de alto contraste: Amarillo Industrial, Gris Oscuro Zinc, Negro Profundo, Gris Claro Slate y Blanco.",
                    color = JsvTextMuted,
                    fontSize = 8.5.sp
                )

                // Swatches Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColorSwatchItem("Amarillo", JsvGold, "#FFC700", Modifier.weight(1f))
                    ColorSwatchItem("Gris Oscuro", JsvDark, "#18181B", Modifier.weight(1f))
                    ColorSwatchItem("Negro", JsvDarker, "#09090B", Modifier.weight(1f))
                    ColorSwatchItem("Gris Claro", JsvGrayLight, "#CBD5E1", Modifier.weight(1f))
                    ColorSwatchItem("Blanco", JsvWhite, "#FFFFFF", Modifier.weight(1f))
                }
            }
        }

        // Density & Accessibility Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "DENSIDAD & RESPUESTA HÁPTICA",
                    color = JsvGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = JsvGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Vibración y Respuesta Táctil", color = JsvTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Micro-vibración al pulsar botones principales", color = JsvTextMuted, fontSize = 8.5.sp)
                        }
                    }
                    Switch(
                        checked = themeConfig.hapticFeedback,
                        onCheckedChange = { onUpdateTheme(themeConfig.copy(hapticFeedback = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = JsvGold, checkedTrackColor = JsvGoldDark)
                    )
                }

                HorizontalDivider(color = JsvBorder)

                // Font Scale
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Escala de Tipografía en App:", color = JsvTextBody, fontSize = 9.5.sp)
                        Text("${(themeConfig.fontScale * 100).toInt()}%", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                    }
                    Slider(
                        value = themeConfig.fontScale,
                        onValueChange = { onUpdateTheme(themeConfig.copy(fontScale = it)) },
                        valueRange = 0.85f..1.25f,
                        colors = SliderDefaults.colors(thumbColor = JsvGold, activeTrackColor = JsvGold)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatchItem(label: String, color: Color, hex: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color)
                .border(1.dp, JsvBorder, RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(label, color = JsvTextWhite, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
        Text(hex, color = JsvTextMuted, fontSize = 6.5.sp)
    }
}

// ==========================================
// SUBSECTION 4: EFFICIENCY & AUTOMATIONS
// ==========================================
@Composable
private fun EfficiencySettingsSection(
    efficiency: EfficiencyConfig,
    onUpdateEfficiency: (EfficiencyConfig) -> Unit,
    onResetDefaults: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "AUTOMATIZACIONES DE DESPACHO",
                    color = JsvGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Copiar formato para WhatsApp", color = JsvTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Copia el texto formateado al portapapeles al guardar la cotización", color = JsvTextMuted, fontSize = 8.5.sp)
                    }
                    Switch(
                        checked = efficiency.autoCopyWhatsAppOnGenerate,
                        onCheckedChange = { onUpdateEfficiency(efficiency.copy(autoCopyWhatsAppOnGenerate = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = JsvGold, checkedTrackColor = JsvGoldDark)
                    )
                }

                HorizontalDivider(color = JsvBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-guardado reactivo en SQLite", color = JsvTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Persistencia continua de borradores cada 5 segundos", color = JsvTextMuted, fontSize = 8.5.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(JsvDarkElevated)
                            .border(1.dp, JsvGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("5 SEG", color = JsvGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Factory Reset Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = JsvDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvRed.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ZONA DE RESTABLECIMIENTO",
                    color = JsvRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                )
                Text(
                    text = "Restaura todos los presets modulares, colores de entrega, logos y parámetros predeterminados de fábrica.",
                    color = JsvTextMuted,
                    fontSize = 8.5.sp
                )
                Button(
                    onClick = onResetDefaults,
                    colors = ButtonDefaults.buttonColors(containerColor = JsvRed, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().testTag("factory_reset_button")
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restablecer Valores de Fábrica", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

private fun getIconForName(name: String): ImageVector {
    return when (name) {
        "edit" -> Icons.Default.Edit
        "local_shipping" -> Icons.Default.LocalShipping
        "verified_user" -> Icons.Default.VerifiedUser
        "directions_car" -> Icons.Default.DirectionsCar
        "photo_camera" -> Icons.Default.PhotoCamera
        "public" -> Icons.Default.Public
        "document_scanner" -> Icons.Default.DocumentScanner
        "security" -> Icons.Default.Security
        "extension" -> Icons.Default.Extension
        "notifications" -> Icons.Default.Notifications
        "settings" -> Icons.Default.Settings
        "dashboard" -> Icons.Default.Dashboard
        "bolt" -> Icons.Default.Bolt
        "build" -> Icons.Default.Build
        "brush" -> Icons.Default.Brush
        else -> Icons.Default.Settings
    }
}
