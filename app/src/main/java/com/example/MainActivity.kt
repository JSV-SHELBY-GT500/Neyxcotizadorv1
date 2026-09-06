package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DocumentPreviewView
import com.example.ui.screens.audit.AuditControlHubScreen
import com.example.ui.screens.autoparts.AutoPartsCatalogScreen
import com.example.ui.screens.editor.QuoteEditorScreen
import com.example.ui.screens.links.SmartLinksScreen
import com.example.ui.screens.modules.AnalyticsDashboardScreen
import com.example.ui.screens.modules.DynamicModuleManagerScreen
import com.example.ui.screens.modules.FinanceCalculatorScreen
import com.example.ui.screens.modules.FunctionBreakdownScreen
import com.example.ui.screens.modules.Obd2DiagnosticScreen
import com.example.ui.screens.modules.SalvageValuationScreen
import com.example.ui.screens.modules.ShippingLabelScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.ocr.OcrScannerWorkflowScreen
import com.example.ui.screens.orders.OrderTrackingScreen
import com.example.ui.screens.photos.PhotoBankScreen
import com.example.ui.screens.system.GeneralSettingsScreen
import com.example.ui.screens.vehicles.VehicleCatalogScreen
import com.example.ui.screens.warranties.WarrantyScreen
import com.example.ui.theme.FugazHeavyBrandStyle
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvDarker
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvOnPrimary
import com.example.ui.theme.JsvRed
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.JsvTab
import com.example.ui.viewmodel.JsvViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                JsvMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsvMainApp(viewModel: JsvViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val quoteState by viewModel.quoteState.collectAsState()
    val deliverableFormat by viewModel.deliverableFormatConfig.collectAsState()
    val modularPanels by viewModel.modularPanels.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val enabledModules by viewModel.enabledDynamicModules.collectAsState()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showQuickPreviewModal by remember { mutableStateOf(false) }

    // Compute visible tabs based on active dynamic modules & modular canvas preferences
    val visibleTabs = remember(enabledModules, modularPanels) {
        val enabledModuleIds = enabledModules.map { it.id }.toSet()
        val visiblePanelIds = modularPanels.filter { it.isVisible }.map { it.id }.toSet()

        val filtered = JsvTab.entries.filter { tab ->
            tab == JsvTab.AJUSTES || tab == JsvTab.MODULOS || tab == JsvTab.NOTIFICACIONES ||
                    (enabledModuleIds.contains(tab.moduleId) && (visiblePanelIds.isEmpty() || visiblePanelIds.contains(tab.moduleId)))
        }

        // Prioritize tabs marked with showFirst in modularPanels
        filtered.sortedByDescending { tab ->
            modularPanels.find { it.id == tab.moduleId }?.isPriorityFirst == true
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Signature Elegant Dark Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(JsvGold)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "JSV",
                                color = JsvOnPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "STUDIO",
                                    color = JsvTextWhite,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(JsvDarkElevated)
                                        .border(1.dp, JsvBorder, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "CORE V3.0",
                                        color = JsvGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "AUDITORIA & MODULOS DINAMICOS",
                                color = JsvTextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    // Notification Bell with live unread badge
                    Box(modifier = Modifier.padding(end = 4.dp)) {
                        IconButton(
                            onClick = { viewModel.selectTab(JsvTab.NOTIFICACIONES) },
                            modifier = Modifier.testTag("notification_bell_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Centro de Notificaciones",
                                tint = if (currentTab == JsvTab.NOTIFICACIONES) JsvGold else JsvTextWhite
                            )
                        }
                        if (unreadNotificationsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(JsvRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotificationsCount > 9) "9+" else "$unreadNotificationsCount",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Synced status pill matching design HTML
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(JsvDark)
                            .border(1.dp, JsvBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(JsvGreen)
                        )
                        Text(
                            text = "ROOM OK",
                            color = JsvTextWhite,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Quick Folio Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(JsvDarkElevated)
                            .border(1.dp, JsvBorder, RoundedCornerShape(6.dp))
                            .clickable { showQuickPreviewModal = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = quoteState.folioRef,
                            color = JsvBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { showQuickPreviewModal = true },
                        modifier = Modifier.testTag("preview_quick_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Previsualizar Documento",
                            tint = JsvGold
                        )
                    }

                    // Settings & Canvas button in top bar
                    IconButton(
                        onClick = { viewModel.selectTab(JsvTab.AJUSTES) },
                        modifier = Modifier.testTag("top_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes y Canvas",
                            tint = if (currentTab == JsvTab.AJUSTES) JsvGold else JsvTextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = JsvDarker,
                    titleContentColor = JsvTextWhite
                ),
                modifier = Modifier.testTag("top_bar")
            )
        },
        bottomBar = {
            // Elegant horizontal tab navigation bar adapting dynamically to enabled modules
            val selectedIndex = visibleTabs.indexOf(currentTab).coerceAtLeast(0)

            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                containerColor = JsvSurface,
                contentColor = JsvGold,
                edgePadding = 8.dp,
                indicator = { tabPositions ->
                    if (selectedIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                            color = JsvGold,
                            height = 3.dp
                        )
                    }
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(JsvBorder)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tab_row")
            ) {
                visibleTabs.forEach { tab ->
                    val selected = currentTab == tab
                    val (icon, testTag) = getTabMetadata(tab)

                    Tab(
                        selected = selected,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = tab.label,
                                color = if (selected) JsvGold else JsvTextMuted,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.label,
                                tint = if (selected) JsvGold else JsvTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag(testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                JsvTab.AUTOPARTES -> AutoPartsCatalogScreen(viewModel = viewModel)
                JsvTab.EDITOR -> QuoteEditorScreen(viewModel = viewModel)
                JsvTab.SEGUIMIENTO -> OrderTrackingScreen(viewModel = viewModel)
                JsvTab.GARANTIA -> WarrantyScreen(viewModel = viewModel)
                JsvTab.VEHICULOS -> VehicleCatalogScreen(viewModel = viewModel)
                JsvTab.FOTOS -> PhotoBankScreen(viewModel = viewModel)
                JsvTab.LINKS -> SmartLinksScreen(viewModel = viewModel)
                JsvTab.SCANNER_OCR -> OcrScannerWorkflowScreen(viewModel = viewModel)
                JsvTab.FINANZAS -> FinanceCalculatorScreen(viewModel = viewModel)
                JsvTab.ETIQUETAS -> ShippingLabelScreen(viewModel = viewModel)
                JsvTab.DIAGNOSTICO -> Obd2DiagnosticScreen(viewModel = viewModel)
                JsvTab.VALUACION -> SalvageValuationScreen(viewModel = viewModel)
                JsvTab.ESTADISTICAS -> AnalyticsDashboardScreen(viewModel = viewModel)
                JsvTab.AUDITORIA -> AuditControlHubScreen(viewModel = viewModel)
                JsvTab.MODULOS -> DynamicModuleManagerScreen(viewModel = viewModel)
                JsvTab.DESGLOSE -> FunctionBreakdownScreen(viewModel = viewModel)
                JsvTab.NOTIFICACIONES -> NotificationsScreen(viewModel = viewModel)
                JsvTab.AJUSTES -> GeneralSettingsScreen(viewModel = viewModel)
            }
        }
    }

    // Quick Preview Modal
    if (showQuickPreviewModal) {
        ModalBottomSheet(
            onDismissRequest = { showQuickPreviewModal = false },
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
                        text = "Documento Oficial JSV-AUTOPARTES",
                        style = FugazHeavyBrandStyle.copy(fontSize = 14.sp, color = JsvGold)
                    )
                    IconButton(onClick = { showQuickPreviewModal = false }) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Cerrar",
                            tint = JsvTextWhite
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                DocumentPreviewView(state = quoteState, formatConfig = deliverableFormat)
            }
        }
    }
}

private fun getTabMetadata(tab: JsvTab): Pair<ImageVector, String> {
    return when (tab) {
        JsvTab.AUTOPARTES -> Pair(Icons.Default.Build, "tab_autopartes")
        JsvTab.EDITOR -> Pair(Icons.Default.Edit, "tab_editor")
        JsvTab.SEGUIMIENTO -> Pair(Icons.Default.LocalShipping, "tab_pedidos")
        JsvTab.GARANTIA -> Pair(Icons.Default.VerifiedUser, "tab_garantias")
        JsvTab.VEHICULOS -> Pair(Icons.Default.DirectionsCar, "tab_vehiculos")
        JsvTab.FOTOS -> Pair(Icons.Default.PhotoCamera, "tab_fotos")
        JsvTab.LINKS -> Pair(Icons.Default.Public, "tab_links")
        JsvTab.SCANNER_OCR -> Pair(Icons.Default.DocumentScanner, "tab_scanner_ocr")
        JsvTab.FINANZAS -> Pair(Icons.Default.AttachMoney, "tab_finanzas")
        JsvTab.ETIQUETAS -> Pair(Icons.Default.LocalActivity, "tab_etiquetas")
        JsvTab.DIAGNOSTICO -> Pair(Icons.Default.Healing, "tab_diagnostico")
        JsvTab.VALUACION -> Pair(Icons.Default.Calculate, "tab_valuacion")
        JsvTab.ESTADISTICAS -> Pair(Icons.Default.BarChart, "tab_estadisticas")
        JsvTab.AUDITORIA -> Pair(Icons.Default.Security, "tab_auditoria")
        JsvTab.MODULOS -> Pair(Icons.Default.Extension, "tab_modulos")
        JsvTab.DESGLOSE -> Pair(Icons.Default.Code, "tab_desglose")
        JsvTab.NOTIFICACIONES -> Pair(Icons.Default.Notifications, "tab_notificaciones")
        JsvTab.AJUSTES -> Pair(Icons.Default.Settings, "tab_ajustes")
    }
}
