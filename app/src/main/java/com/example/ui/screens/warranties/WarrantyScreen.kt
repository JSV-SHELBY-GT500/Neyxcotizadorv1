package com.example.ui.screens.warranties

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Warranty
import com.example.ui.components.JsvSectionHeader
import com.example.ui.components.JsvField
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.FugazHeavyBrandStyle
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
import com.example.ui.viewmodel.JsvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val warranties by viewModel.allWarranties.collectAsState()
    val activeQuote by viewModel.quoteState.collectAsState()

    var activePreset by remember { mutableStateOf("general") }
    var tiempoGarantia by remember { mutableStateOf("30 días naturales") }
    var terminos by remember {
        mutableStateOf("La garantía cubre defectos internos de funcionamiento y ensamble. No aplica en caso de alteración de sellos, negligencia en la instalación o falta de lubricación adecuada.")
    }
    var marcaAgua by remember { mutableStateOf("JSV-AUTOPARTES") }
    var marcaAguaOpacidad by remember { mutableStateOf(0.15f) }

    // Cliente
    var clienteNombre by remember { mutableStateOf("") }
    var clienteTelefono by remember { mutableStateOf("") }
    var clienteDireccion by remember { mutableStateOf("") }

    // Pieza
    var piezaNombre by remember { mutableStateOf(activeQuote.partName) }
    var piezaMarca by remember { mutableStateOf(activeQuote.donorVehicle.split(" ").firstOrNull() ?: "") }
    var piezaModelo by remember { mutableStateOf(activeQuote.donorVehicle) }
    var piezaNumeroParte by remember { mutableStateOf(activeQuote.partOem) }
    var piezaIdSerial by remember { mutableStateOf("JSV-SN-001") }

    // Pieza cambio
    var cambioNombre by remember { mutableStateOf("") }
    var cambioDescripcion by remember { mutableStateOf("") }

    var previewWarranty by remember { mutableStateOf<Warranty?>(null) }
    var warrantyToDelete by remember { mutableStateOf<Warranty?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            JsvSectionHeader("🛡️ Emisión de Garantía Oficial")
            Spacer(modifier = Modifier.height(8.dp))

            // Presets
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Preset de Garantía:", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PresetButton("General (30d)", activePreset == "general", Modifier.weight(1f)) {
                            activePreset = "general"
                            tiempoGarantia = "30 días naturales"
                            terminos = "Garantía estándar 30 días contra defectos de fabricación y funcionamiento interno. Sellos intactos."
                        }
                        PresetButton("Compresor (90d)", activePreset == "compresor", Modifier.weight(1f)) {
                            activePreset = "compresor"
                            tiempoGarantia = "90 días naturales"
                            terminos = "Garantía de compresor A/C. Requiere reemplazo obligatorio de condensador, válvula de expansión y lavado de sistema."
                        }
                        PresetButton("Transmisión (60d)", activePreset == "transmision", Modifier.weight(1f)) {
                            activePreset = "transmision"
                            tiempoGarantia = "60 días naturales"
                            terminos = "Garantía de transmisión y turbina. Requiere aceite nuevo especificación OEM y limpieza de serpentín."
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        JsvField(
                            label = "Tiempo de Garantía",
                            value = tiempoGarantia,
                            onValueChange = { tiempoGarantia = it },
                            modifier = Modifier.weight(1.2f)
                        )
                        JsvField(
                            label = "Marca de Agua",
                            value = marcaAgua,
                            onValueChange = { marcaAgua = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Opacidad Marca de Agua: ${String.format("%.2f", marcaAguaOpacidad)}", color = JsvTextMuted, fontSize = 9.sp)
                    Slider(
                        value = marcaAguaOpacidad,
                        onValueChange = { marcaAguaOpacidad = it },
                        valueRange = 0.05f..0.4f,
                        colors = SliderDefaults.colors(thumbColor = JsvGold, activeTrackColor = JsvGold)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    JsvField(
                        label = "Términos y Condiciones",
                        value = terminos,
                        onValueChange = { terminos = it },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Datos del Cliente
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("👤 Datos del Cliente", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        JsvField(label = "Nombre Cliente", value = clienteNombre, onValueChange = { clienteNombre = it }, modifier = Modifier.weight(1.2f))
                        JsvField(label = "Teléfono", value = clienteTelefono, onValueChange = { clienteTelefono = it }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    JsvField(label = "Dirección", value = clienteDireccion, onValueChange = { clienteDireccion = it }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Datos de la Pieza
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("⚙️ Datos de la Pieza Garantizada", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    JsvField(label = "Nombre de la Pieza", value = piezaNombre, onValueChange = { piezaNombre = it }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        JsvField(label = "Marca", value = piezaMarca, onValueChange = { piezaMarca = it }, modifier = Modifier.weight(1f))
                        JsvField(label = "Modelo", value = piezaModelo, onValueChange = { piezaModelo = it }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        JsvField(label = "No. Parte OEM", value = piezaNumeroParte, onValueChange = { piezaNumeroParte = it }, modifier = Modifier.weight(1f))
                        JsvField(label = "ID / Serial Interno", value = piezaIdSerial, onValueChange = { piezaIdSerial = it }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Pieza a cambio
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔄 Pieza a Cambio (Casco Entregado)", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    JsvField(label = "Nombre de Pieza Retirada", value = cambioNombre, onValueChange = { cambioNombre = it }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    JsvField(label = "Estado / Observaciones del Casco", value = cambioDescripcion, onValueChange = { cambioDescripcion = it }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Botón Emitir Garantía
        item {
            Button(
                onClick = {
                    val w = Warranty(
                        presetActivo = activePreset,
                        tiempoGarantia = tiempoGarantia,
                        terminos = terminos,
                        marcaAgua = marcaAgua,
                        marcaAguaOpacidad = marcaAguaOpacidad,
                        clienteNombre = clienteNombre,
                        clienteTelefono = clienteTelefono,
                        clienteDireccion = clienteDireccion,
                        piezaNombre = piezaNombre,
                        piezaMarca = piezaMarca,
                        piezaModelo = piezaModelo,
                        piezaNumeroParte = piezaNumeroParte,
                        piezaIdSerial = piezaIdSerial,
                        cambioNombre = cambioNombre,
                        cambioDescripcion = cambioDescripcion,
                        mostrarCambio = cambioNombre.isNotBlank()
                    )
                    viewModel.saveWarranty(w)
                    previewWarranty = w
                },
                colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = JsvOnPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GENERAR PÓLIZA DE GARANTÍA", color = JsvOnPrimary, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }

        // Historial de Garantías Guardadas
        item {
            Spacer(modifier = Modifier.height(10.dp))
            JsvSectionHeader("📋 Pólizas de Garantía Emitidas (${warranties.size})")
        }

        if (warranties.isEmpty()) {
            item {
                Text(
                    text = "Aún no hay pólizas de garantía registradas.",
                    color = JsvTextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(warranties, key = { it.id }) { w ->
                WarrantyItemCard(
                    warranty = w,
                    onPreview = { previewWarranty = w },
                    onShare = {
                        val shareText = buildWarrantyShareText(w)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Compartir Póliza de Garantía"))
                    },
                    onDelete = { warrantyToDelete = w }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Preview of Warranty Certificate
    previewWarranty?.let { w ->
        ModalBottomSheet(
            onDismissRequest = { previewWarranty = null },
            containerColor = JsvDarker
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Póliza de Garantía Oficial", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    IconButton(onClick = { previewWarranty = null }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = JsvTextWhite)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Certificate Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(2.dp, JsvGold, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JsvDark)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "JSV-AUTOPARTES • PÓLIZA DE GARANTÍA",
                                style = FugazHeavyBrandStyle.copy(fontSize = 12.sp, color = JsvGold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("FOLIO: ${w.id} | FECHA: ${w.fecha}", color = Color(0xFF6B7280), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.background(JsvGold).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("COBERTURA: ${w.tiempoGarantia.uppercase()}", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 9.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("CLIENTE: ${w.clienteNombre.ifBlank { "Venta en Mostrador" }}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                        if (w.clienteTelefono.isNotBlank()) Text("Tel: ${w.clienteTelefono}", color = Color(0xFF4B5563), fontSize = 8.sp)
                        if (w.clienteDireccion.isNotBlank()) Text("Dir: ${w.clienteDireccion}", color = Color(0xFF4B5563), fontSize = 8.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("PIEZA GARANTIZADA: ${w.piezaNombre}", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        Text("Vehículo: ${w.piezaMarca} ${w.piezaModelo}", color = Color(0xFF374151), fontSize = 8.5.sp)
                        if (w.piezaNumeroParte.isNotBlank()) Text("OEM: ${w.piezaNumeroParte}", color = Color(0xFF374151), fontSize = 8.sp)
                        Text("Serial / ID: ${w.piezaIdSerial}", color = Color(0xFF1E40AF), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)

                        if (w.cambioNombre.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("PIEZA RECIBIDA A CAMBIO: ${w.cambioNombre}", color = Color(0xFF991B1B), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("TÉRMINOS Y CONDICIONES:", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                        Text(w.terminos, color = Color(0xFF4B5563), fontSize = 7.5.sp, lineHeight = 11.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SELLO DE INVIOLABILIDAD Y CONTROL INTERNO\nJSV-AUTOPARTES • CALIDAD GARANTIZADA OEM",
                                color = Color(0xFF6B7280),
                                fontSize = 6.5.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val shareText = buildWarrantyShareText(w)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Enviar Póliza"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compartir Póliza de Garantía", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Delete confirmation
    warrantyToDelete?.let { w ->
        AlertDialog(
            onDismissRequest = { warrantyToDelete = null },
            containerColor = JsvSurface,
            title = { Text("Eliminar Póliza", color = JsvRed, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar la póliza ${w.id}?", color = JsvTextBody) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWarranty(w.id)
                        warrantyToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvRed)
                ) {
                    Text("ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { warrantyToDelete = null }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }
}

@Composable
fun PresetButton(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) JsvGold else JsvDarkElevated)
            .border(1.dp, if (selected) JsvGold else JsvBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.Black else JsvTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WarrantyItemCard(
    warranty: Warranty,
    onPreview: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = JsvSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(warranty.id, color = JsvGold, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    Text(warranty.fecha, color = JsvTextMuted, fontSize = 8.sp)
                }
                Box(modifier = Modifier.background(JsvBlue).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(warranty.tiempoGarantia.uppercase(), color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("${warranty.piezaNombre} • ${warranty.piezaMarca} ${warranty.piezaModelo}", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            if (warranty.clienteNombre.isNotBlank()) {
                Text("Cliente: ${warranty.clienteNombre}", color = JsvTextBody, fontSize = 9.5.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreview, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "Ver", tint = JsvGold, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir", tint = JsvBlue, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

fun buildWarrantyShareText(w: Warranty): String {
    return """
        🛡️ *PÓLIZA DE GARANTÍA OFICIAL — JSV-AUTOPARTES*
        🔖 Folio: ${w.id} | 📅 Fecha: ${w.fecha}
        
        ⏱️ *Tiempo de Cobertura:* ${w.tiempoGarantia}
        ⚙️ *Pieza:* ${w.piezaNombre}
        🚗 *Vehículo:* ${w.piezaMarca} ${w.piezaModelo}
        🏷️ *No. OEM / Serial:* ${w.piezaNumeroParte.ifBlank { "N/A" }} | ${w.piezaIdSerial}
        👤 *Cliente:* ${w.clienteNombre.ifBlank { "Venta Mostrador" }}
        
        📜 *Términos:*
        ${w.terminos}
        
        📍 JSV-Autopartes • Torreón - Gómez Palacio - Lerdo
        📞 Atención y Reclamos: 871 141 2774
    """.trimIndent()
}
