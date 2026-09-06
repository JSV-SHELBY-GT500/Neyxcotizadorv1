package com.example.ui.screens.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FugazHeavyBrandStyle
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvDarkElevated
import com.example.ui.theme.JsvDarker
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGreen
import com.example.ui.theme.JsvOnPrimary
import com.example.ui.theme.JsvSurface
import com.example.ui.theme.JsvTextBody
import com.example.ui.theme.JsvTextMuted
import com.example.ui.theme.JsvTextWhite
import com.example.ui.viewmodel.JsvViewModel

data class AppFunctionDetail(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val instructions: String,
    val technicalLogic: String,
    val sourceCodeSnippet: String
)

@Composable
fun FunctionBreakdownScreen(viewModel: JsvViewModel) {
    val clipboardManager = LocalClipboardManager.current
    var copiedStatusId by remember { mutableStateOf<String?>(null) }

    val functionsList = remember {
        listOf(
            AppFunctionDetail(
                id = "FUNC_01",
                title = "Cotizador Inteligente & Wizard Dinámico",
                category = "Ventas",
                description = "Motor central de cotizaciones de autopartes con soporte para modos de llenado (Dinámico Expreso, Dinámico Completo, Completo), desglose de partidas, opciones de proveedor y previsualización de documentos oficiales con marca de agua y folio único.",
                instructions = "1. Seleccione el modo de cotización en la parte superior.\n2. Ingrese los datos del cliente, vehículo y observaciones.\n3. Agregue partidas con cantidades, precios al cambio y venta directa.\n4. Genere y previsualice el documento oficial JSV-AUTOPARTES.",
                technicalLogic = "ViewModel gestiona el StateFlow<QuoteState>. Almacena estado reactivo persistido en Room con validación de campos numéricos y cálculo automático de totales en USD/VES.",
                sourceCodeSnippet = """
@Composable
fun QuoteEditorScreen(viewModel: JsvViewModel) {
    val state by viewModel.quoteState.collectAsState()
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Cotizador JSV-AUTOPARTES", style = FugazHeavyBrandStyle)
        // Partidas y opciones dinámicas
    }
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_02",
                title = "Catálogo de Autopartes & Stock",
                category = "Ventas",
                description = "Gestión forense e inventario de autopartes originales de desguace y nuevas, con búsqueda en tiempo real, control de stock y compatibilidad de vehículos donantes.",
                instructions = "1. Utilice la barra de búsqueda para filtrar por nombre o número OEM.\n2. Revise la condición, precio y ubicación de anaquel.\n3. Añada nuevas piezas al inventario local con validación de esquema.",
                technicalLogic = "Consulta asíncrona sobre Room DAO (AutoPartDao) utilizando Flow con filtrado reactivo en ViewModel.",
                sourceCodeSnippet = """
@Composable
fun AutoPartsCatalogScreen(viewModel: JsvViewModel) {
    val parts by viewModel.autoPartsList.collectAsState()
    LazyColumn {
        items(parts) { part -> PartCard(part) }
    }
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_03",
                title = "Seguimiento de Pedidos & Logística",
                category = "Logística",
                description = "Tablero de estatus logísticos para órdenes de envío, control de guías de paquetería y trazabilidad desde la cotización hasta la entrega final al cliente.",
                instructions = "1. Monitoree las órdenes activas en las pestañas de estado.\n2. Actualice los pasos de procesamiento logístico (Preparación, Empaque, Despacho).\n3. Copie los datos de envío para compartir con el cliente.",
                technicalLogic = "Gestión de estados de orden con lista de pasos (OrderProcessStep) y persistencia en OrderEntity.",
                sourceCodeSnippet = """
@Composable
fun OrderTrackingScreen(viewModel: JsvViewModel) {
    val orders by viewModel.ordersList.collectAsState()
    // Trazabilidad de envíos y paquetería
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_04",
                title = "Pólizas de Garantía & Vales Oficiales",
                category = "Legal",
                description = "Emisión de certificados de garantía inviolables con sellos corporativos, códigos QR de validación y términos legales de protección para componentes mecánicos y eléctricos.",
                instructions = "1. Seleccione la cotización o venta asociada.\n2. Especifique el período de garantía y componentes cubiertos.\n3. Genere la póliza oficial lista para imprimir o exportar.",
                technicalLogic = "Generación de modelo Warranty vinculado al folio de cotización, con renderizado en Canvas y vista previa estructurada.",
                sourceCodeSnippet = """
@Composable
fun WarrantyScreen(viewModel: JsvViewModel) {
    val warranties by viewModel.warrantiesList.collectAsState()
    // Póliza de garantía con sellos OEM
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_05",
                title = "Catálogo de Vehículos Donantes",
                category = "Ventas",
                description = "Registro de vehículos de origen en despiece (donantes) para verificar compatibilidad exacta de motores, transmisiones y accesorios.",
                instructions = "1. Explore la lista de vehículos donantes registrados.\n2. Filtre por marca, modelo y año.\n3. Consulte las piezas recuperadas de cada unidad.",
                technicalLogic = "Entidad Vehicle en Room con relación 1:N hacia las autopartes despiezadas.",
                sourceCodeSnippet = """
@Composable
fun VehicleCatalogScreen(viewModel: JsvViewModel) {
    val vehicles by viewModel.vehiclesList.collectAsState()
    // Catálogo de donantes y compatibilidad
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_06",
                title = "Banco de Inspección & Fotos OEM",
                category = "Ventas",
                description = "Repositorio fotográfico forense para la inspección de piezas, verificación de números de serie y estado físico antes del despacho.",
                instructions = "1. Capture o seleccione la imagen de la autoparte.\n2. Asigne la categoría y número de serie.\n3. Visualice la evidencia en alta resolución.",
                technicalLogic = "Almacenamiento de referencias a archivos multimedia locales en PhotoItem DAO.",
                sourceCodeSnippet = """
@Composable
fun PhotoBankScreen(viewModel: JsvViewModel) {
    val photos by viewModel.photoItems.collectAsState()
    // Banco de fotos de inspección
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_07",
                title = "Smart Links & Comparador Multicanal",
                category = "Ventas",
                description = "Generación automática de enlaces de búsqueda inteligente para comparar precios de autopartes en MercadoLibre, Amazon, Google Imágenes y Web.",
                instructions = "1. Ingrese el término o número de parte de la autoparte.\n2. Pulse sobre cualquiera de los canales de búsqueda rápida para abrir el navegador.",
                technicalLogic = "Codificación de URLs con URLEncoder UTF-8 para apertura directa de intents externos.",
                sourceCodeSnippet = """
@Composable
fun SmartLinksScreen(viewModel: JsvViewModel) {
    // Generador de Smart Links de búsqueda multicanal
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_08",
                title = "Escáner OCR & Workflows Automáticos",
                category = "IA & Visión",
                description = "Extracción automática de datos de cotizaciones y facturas mediante presets de zonas clave y procesamiento de texto estructurado.",
                instructions = "1. Seleccione una captura o documento OCR de muestra.\n2. Ejecute la extracción automática por zonas clave.\n3. Transfiera los datos extraídos directamente al cotizador.",
                technicalLogic = "Simulación y procesamiento OCR basado en zonas clave (OcrKeyZone) con mapeo automático a QuoteState.",
                sourceCodeSnippet = """
@Composable
fun OcrScannerWorkflowScreen(viewModel: JsvViewModel) {
    // Escáner OCR y extracción de datos
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_09",
                title = "Centro de Auditoría & Logs Forenses",
                category = "Sistema",
                description = "Trazabilidad completa de acciones, cambios de estado, accesos a memoria local y eventos de seguridad registrados en la base de datos.",
                instructions = "1. Revise la lista cronológica de eventos de auditoría.\n2. Verifique la integridad de las operaciones y marcas de tiempo del sistema.",
                technicalLogic = "Registro asíncrono de eventos en AuditLog DAO y visualización en tiempo real.",
                sourceCodeSnippet = """
@Composable
fun AuditControlHubScreen(viewModel: JsvViewModel) {
    val logs by viewModel.auditLogs.collectAsState()
    // Centro de auditoría y logs de sistema
}
                """.trimIndent()
            ),
            AppFunctionDetail(
                id = "FUNC_10",
                title = "Gestor de Módulos Dinámicos & Canvas",
                category = "Sistema",
                description = "Administración centralizada de habilitación de módulos, instalación bajo demanda y preferencias de prioridad en la barra de navegación inferior.",
                instructions = "1. Active o desactive los módulos de la plataforma según sus necesidades.\n2. Cambie el orden de prioridad para visualización prioritaria en el menú.",
                technicalLogic = "Estado persistido en Room (DynamicModuleEntity) controlando la visibilidad en JsvMainApp.",
                sourceCodeSnippet = """
@Composable
fun DynamicModuleManagerScreen(viewModel: JsvViewModel) {
    val modules by viewModel.allDynamicModules.collectAsState()
    // Gestor de módulos y canvas dinámico
}
                """.trimIndent()
            )
        )
    }

    val workflowPromptText = """
[ROL] Eres el Agente de Automatización y Generador de Datasheets de Cotizaciones para JSV-AUTOPARTES CORE V3.0 (Android Kotlin / Jetpack Compose / Room Database).
[OBJETIVO] Automatizar la creación de data para el flujo de cotizaciones batch (lotes múltiples) o individuales asistidas por IA, generando el datasheet con el formato JSON exacto establecido para integrarse en la plataforma.
[FORMATO REQUERIDO DE DATASHEET JSON]:
{
  "folioRef": "JSV-2026-0830",
  "clientName": "Nombre del Cliente / Taller",
  "clientPhone": "+58 412 0000000",
  "vehicleInfo": "Marca Modelo Año Motor",
  "paymentMethod": "Zelle / Efectivo / Pago Móvil / Binance",
  "currency": "USD (${'$'})",
  "breakdownItems": [
    {
      "id": "item_1",
      "concept": "Descripción de la autoparte o servicio",
      "qty": "1",
      "condition": "Nuevo / Original / Garantizado",
      "priceCambio": 120.0,
      "priceVenta": 150.0
    }
  ],
  "quoteOptions": [
    {
      "id": "opt_1",
      "title": "Opción Principal Proveedor",
      "price": 150.0,
      "description": "Detalle de entrega y origen",
      "condition": "Importado USA",
      "delivery": "Inmediata / 24h",
      "warranty": "3 Meses"
    }
  ]
}
[INSTRUCCIONES DE USO PARA LA IA]:
1. Recibe cualquier texto bruto (mensaje de WhatsApp de cliente pidiendo repuestos, nota de voz transcrita, lista de precios en PDF o foto OCR).
2. Extrae y mapea cada ítem al esquema JSON anterior.
3. Si es batch (múltiples cotizaciones), devuelve un arreglo de objetos QuoteState válidos.
4. Devuelve únicamente el JSON estructurado limpio, listo para ser importado en la plataforma JSV-AUTOPARTES.
    """.trimIndent()

    var workflowCopied by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header & Master AI Workflow Prompt Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = "IA", tint = JsvGold, modifier = Modifier.size(24.dp))
                        Text(
                            text = "WORKFLOW PROMPT AI — COTIZACIONES BATCH & INDIVIDUALES",
                            style = FugazHeavyBrandStyle.copy(fontSize = 13.sp, color = JsvGold)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Automatiza la creación de data para el flujo de cotizaciones asistidas por IA. Copia el prompt maestro al portapapeles para alimentar a cualquier modelo de lenguaje externo y generar el datasheet JSON exacto con el formato establecido para integrarse en la plataforma JSV-AUTOPARTES.",
                        color = JsvTextBody,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(workflowPromptText))
                            workflowCopied = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (workflowCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copiar",
                            tint = JsvOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (workflowCopied) "¡Workflow Prompt Copiado al Portapapeles!" else "📋 Copiar Workflow Prompt IA (Batch / Individuales)",
                            color = JsvOnPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚙️ DESGLOSE TÉCNICO DE FUNCIONES Y MÓDULOS",
                    color = JsvTextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "${functionsList.size} Módulos",
                    color = JsvGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(functionsList, key = { it.id }) { func ->
            val isThisCopied = copiedStatusId == func.id

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvDarkElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Header row with ID & Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(JsvBlue)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(func.id, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(JsvSurface)
                                    .border(1.dp, JsvBorder, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(func.category, color = JsvGold, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val fullFunctionText = """
=== [JSV-AUTOPARTES FUNCTION BREAKDOWN] ===
ID: ${func.id}
Título: ${func.title}
Categoría: ${func.category}

[DESCRIPCIÓN FUNCIONAL]
${func.description}

[INSTRUCCIONES DE USO]
${func.instructions}

[LÓGICA TÉCNICA Y ARQUITECTURA]
${func.technicalLogic}

[CÓDIGO FUENTE KOTLIN / COMPOSE]
${func.sourceCodeSnippet}
                                """.trimIndent()
                                clipboardManager.setText(AnnotatedString(fullFunctionText))
                                copiedStatusId = func.id
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JsvGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JsvGold),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = if (isThisCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copiar",
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isThisCopied) "Copiado" else "Copiar AI Context", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = func.title,
                        color = JsvTextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "📖 Descripción:",
                        color = JsvGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Text(
                        text = func.description,
                        color = JsvTextBody,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "💡 Instrucciones:",
                        color = JsvGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Text(
                        text = func.instructions,
                        color = JsvTextBody,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "⚙️ Lógica Técnica & Estado:",
                        color = JsvBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Text(
                        text = func.technicalLogic,
                        color = JsvTextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Code snippet container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(JsvDarker)
                            .border(1.dp, JsvBorder, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Terminal, contentDescription = "Código", tint = JsvGold, modifier = Modifier.size(12.dp))
                                Text("Snippet Clave (Kotlin / Compose):", color = JsvGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = func.sourceCodeSnippet,
                                color = JsvTextWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
