package com.example.ui.screens.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun ShippingLabelScreen(viewModel: JsvViewModel) {
    val clipboardManager = LocalClipboardManager.current
    var clientName by remember { mutableStateOf("Taller Mecánico El Águila C.A.") }
    var destinationCity by remember { mutableStateOf("Caracas, D.C. - Agencia MRW") }
    var partDescription by remember { mutableStateOf("Kit de Embrague Original Toyota Hilux 2.7") }
    var trackingCode by remember { mutableStateOf("JSV-SHP-2026-8942") }
    var isCopied by remember { mutableStateOf(false) }

    val labelText = """
========================================
[JSV-AUTOPARTES] GUÍA DE DESPACHO & ENVÍO
========================================
Nº Guía / Tracking: $trackingCode
Destinatario: $clientName
Destino / Ciudad: $destinationCity
Contenido: $partDescription
Condición: Original Garantizado / Frágil
Sello: JSV-AUTOPARTES • CONTROL DE CALIDAD OEM
========================================
    """.trimIndent()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JsvDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = BorderStroke(1.dp, JsvGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LocalShipping, contentDescription = "Envíos", tint = JsvGold, modifier = Modifier.size(24.dp))
                        Text(text = "GENERADOR DE ETIQUETAS Y GUÍAS — JSV", style = FugazHeavyBrandStyle.copy(fontSize = 13.sp, color = JsvGold))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Genera etiquetas de envío estandarizadas para paquetes de autopartes, con códigos de barras simulados, datos del cliente y alertas de manejo delicado.",
                        color = JsvTextBody,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvDarkElevated),
                border = BorderStroke(1.dp, JsvBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Datos del Envío", color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Cliente / Taller Destino") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JsvGold, unfocusedBorderColor = JsvBorder, focusedLabelColor = JsvGold, cursorColor = JsvGold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = destinationCity,
                        onValueChange = { destinationCity = it },
                        label = { Text("Ciudad / Agencia de Encomienda") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JsvGold, unfocusedBorderColor = JsvBorder, focusedLabelColor = JsvGold, cursorColor = JsvGold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = partDescription,
                        onValueChange = { partDescription = it },
                        label = { Text("Pieza / Contenido del Paquete") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JsvGold, unfocusedBorderColor = JsvBorder, focusedLabelColor = JsvGold, cursorColor = JsvGold)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JsvSurface),
                border = BorderStroke(1.dp, JsvGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.QrCode, contentDescription = "QR", tint = JsvGold, modifier = Modifier.size(20.dp))
                        Text("VISTA PREVIA DE ETIQUETA OFICIAL", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("📦 JSV-AUTOPARTES • DESPACHO NACIONAL", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Guía: $trackingCode", color = Color.DarkGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color.LightGray)
                            Text("DESTINO: $destinationCity", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("CLIENTE: $clientName", color = Color.DarkGray, fontSize = 10.sp)
                            Text("CONTENIDO: $partDescription", color = Color.DarkGray, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black)
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("||| | |||| || | |||||| || | |||", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(labelText))
                            isCopied = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copiar",
                            tint = JsvOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isCopied) "¡Etiqueta Copiada al Portapapeles!" else "📋 Copiar Formato de Etiqueta", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
