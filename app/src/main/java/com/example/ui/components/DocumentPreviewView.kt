package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliverableFormatConfig
import com.example.data.model.QuoteState
import com.example.ui.theme.FugazHeavyBrandStyle
import com.example.ui.theme.ImpactBrandStyle
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
import com.example.ui.theme.JsvDark
import com.example.ui.theme.JsvGold
import com.example.ui.theme.JsvGrayLight
import com.example.ui.theme.JsvTextWhite

@Composable
fun DocumentPreviewView(
    state: QuoteState,
    modifier: Modifier = Modifier,
    formatConfig: DeliverableFormatConfig? = null
) {
    val scrollState = rememberScrollState()

    // Config defaults or active custom deliverable format
    val brandTitle = formatConfig?.brandTitle ?: state.brandTitle
    val brandSub = formatConfig?.brandSub ?: state.brandSub
    val brandColor = formatConfig?.let { parseHexColor(it.brandColorHex, JsvGold) } ?: JsvGold
    val headerBg = formatConfig?.let { parseHexColor(it.headerBgHex, JsvDark) } ?: JsvDark
    val watermarkText = formatConfig?.watermarkText ?: "JSV-AUTOPARTES ORIGINAL"
    val watermarkOpacity = formatConfig?.watermarkOpacity ?: 0.10f
    val watermarkAngle = formatConfig?.watermarkAngle ?: -28f
    val watermarkEnabled = formatConfig?.watermarkEnabled ?: true
    val sealType = formatConfig?.sealType ?: "SELLO_DIGITAL_DORADO"
    val footerPhone = formatConfig?.footerPhone ?: "WHATSAPP: 871 141 2774"
    val footerAddress = formatConfig?.footerAddress ?: "CARRETERA A BERMEJILLO KM 16 • TORREÓN / GÓMEZ PALACIO"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(2.dp, brandColor, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // --- HEADER BAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        // Heavy Impact Brand Typography (Fugaz One Negrita Heavy style)
                        Text(
                            text = brandTitle.uppercase(),
                            style = FugazHeavyBrandStyle.copy(color = brandColor)
                        )
                        Text(
                            text = brandSub.uppercase(),
                            color = Color(0xFFF1F5F9),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(width = 3.dp, height = 38.dp)
                            .background(brandColor)
                    )

                    Column(
                        modifier = Modifier.weight(1.1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = state.docTitle,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        if (state.docSubtitle.isNotBlank()) {
                            Text(
                                text = state.docSubtitle,
                                color = brandColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "FECHA: ${state.dateStr}",
                            color = Color(0xFF94A3B8),
                            fontSize = 7.sp
                        )
                        Text(
                            text = "REF: ${state.folioRef}",
                            color = JsvBlue,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Accent dividing strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(brandColor)
            )

            // --- BODY CONTENT (WITH OPTIONAL WATERMARK OVERLAY) ---
            Box(modifier = Modifier.fillMaxWidth()) {

                // Watermark Layer
                if (watermarkEnabled) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                rotationZ = watermarkAngle
                                alpha = watermarkOpacity
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = watermarkText.uppercase(),
                                color = Color(0xFF0F172A),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                letterSpacing = 3.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "ORIGINAL CERTIFICADO • PIEZA OEM",
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Main Content Body
                Column(modifier = Modifier.padding(12.dp)) {

                    if (state.quoteType == "stock_local") {
                        // Section 1: Ficha del Vehículo
                        if (state.showSecTitle1) {
                            DocumentSectionTitle(state.secTitle1, brandColor)
                        }

                        // Meta Table
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MetaCellHeader("VEHÍCULO DONANTE", Modifier.weight(1f))
                                MetaCellValue(state.donorVehicle, Modifier.weight(1.3f))
                                MetaCellHeader("RANGO COMPATIBLE", Modifier.weight(1f))
                                MetaCellValue(state.yearRange, Modifier.weight(1.3f))
                            }
                            HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MetaCellHeader("MOTORES COMPATIBLES", Modifier.weight(1f))
                                MetaCellValue(state.engineRange, Modifier.weight(1.3f))
                                MetaCellHeader("UBICACIÓN / POSICIÓN", Modifier.weight(1f))
                                MetaCellValue(state.itemLocation, Modifier.weight(1.3f))
                            }
                        }

                        // Tech Verify Box
                        if (state.showTechVerify && state.techVerifyNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFFBEB))
                                    .border(1.dp, brandColor, RoundedCornerShape(2.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "RECOMENDACIÓN TÉCNICA: ${state.techVerifyNote}",
                                    color = Color(0xFF1E293B),
                                    fontSize = 7.5.sp,
                                    lineHeight = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Section 2: Inspección Visual y Especificaciones
                        if (state.showSecTitle2) {
                            DocumentSectionTitle(state.secTitle2, brandColor)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (state.showImageSection) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(brandColor)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "REGISTRO FOTOGRÁFICO",
                                            color = Color.Black,
                                            fontSize = 6.5.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.partName,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    if (state.showPlaceholderText) {
                                        Text(
                                            text = "[ INSPECCIÓN FÍSICA ]",
                                            color = Color(0xFF64748B),
                                            fontSize = 7.sp
                                        )
                                    }
                                    if (state.showImgCaption) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = state.imgCaptionText,
                                            color = Color(0xFF475569),
                                            fontSize = 6.5.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1.4f)
                                    .border(1.dp, Color(0xFFE2E8F0))
                                    .padding(6.dp)
                            ) {
                                SpecRow("PIEZA:", state.partName)
                                SpecRow("NO. OEM:", state.partOem)
                                SpecRow("INGENIERÍA:", state.partEng)
                                SpecRow("SISTEMA:", state.partSystem)
                                SpecRow("ESTADO:", state.partCondition)
                                SpecRow("ORIGEN:", state.partOrigin)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Section 3: Tabla de Opciones y Precios
                        if (state.showSecTitle3) {
                            DocumentSectionTitle(state.secTitle3, brandColor)
                        }

                        if (state.showBreakdownTable && state.breakdownItems.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFCBD5E1))
                            ) {
                                // Header Table
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(headerBg)
                                        .padding(vertical = 5.dp, horizontal = 6.dp)
                                ) {
                                    Text("COMPONENTE", color = Color.White, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                                    Text("CANT.", color = Color.White, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.6f))
                                    Text("CONDICIÓN", color = Color.White, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                    Text("AL CAMBIO", color = brandColor, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1.3f))
                                    Text("VENTA DIRECTA", color = brandColor, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1.4f))
                                }

                                state.breakdownItems.forEach { item ->
                                    HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(item.concept, color = Color(0xFF0F172A), fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                                        Text(item.qty, color = Color(0xFF0F172A), fontSize = 7.5.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.6f))
                                        Text(item.condition, color = Color(0xFF475569), fontSize = 7.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                        Column(modifier = Modifier.weight(1.3f), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(modifier = Modifier.background(Color.Black).padding(horizontal = 4.dp, vertical = 1.dp)) {
                                                Text("AL CAMBIO", color = Color.White, fontSize = 6.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text("$${String.format("%,.0f", item.priceCambio)} MXN", color = Color.Black, fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                                        }
                                        Column(modifier = Modifier.weight(1.4f), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(modifier = Modifier.background(brandColor).padding(horizontal = 4.dp, vertical = 1.dp)) {
                                                Text("VENTA DIRECTA", color = Color.Black, fontSize = 6.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text("$${String.format("%,.0f", item.priceVenta)} MXN", color = Color.Black, fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Section 4: Logística y Garantía + Digital Seal
                        if (state.showSecTitle4) {
                            DocumentSectionTitle(state.secTitle4, brandColor)
                        }

                        if (state.showLogistics) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(text = state.logisticsTitle, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = state.logisticsText, color = Color(0xFF334155), fontSize = 7.5.sp, lineHeight = 11.sp)
                                }
                            }
                        }

                        if (state.showGuarantee) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFFBEB))
                                    .border(1.dp, brandColor, RoundedCornerShape(2.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = state.guaranteeTitle, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = state.guaranteeText, color = Color(0xFF334155), fontSize = 7.5.sp, lineHeight = 11.sp)
                                    }

                                    // Digital Seal Stamp
                                    if (sealType != "NINGUNO") {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        DigitalSealBadge(sealType = sealType, brandColor = brandColor)
                                    }
                                }
                            }
                        }

                    } else {
                        // SOBRE PEDIDO MODE
                        DocumentSectionTitle("1. VEHÍCULO RECEPTOR, COMPATIBILIDAD Y SERVICIO SOBRE PEDIDO", brandColor)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFBEB))
                                .border(1.dp, brandColor, RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("VEHÍCULO RECEPTOR / DESTINO: ${state.recipientVehicle}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 8.sp)
                                Text("TIPO DE PIEZA: ${if (state.isNewPart) "90% Pieza Nueva OEM (Importación / Proveedor Directo)" else "Pieza Usada / Donante Recuperada"}", color = Color(0xFF334155), fontSize = 7.5.sp)
                                Text("MODALIDAD: ${if (state.purchaseMode == "gestion_jsv") "Gestión Integral JSV (Búsqueda, Verificación, Manejo y Garantías)" else "Compra en Línea Directa con Soporte JSV"}", color = Color(0xFF334155), fontSize = 7.5.sp)
                                Text("POLÍTICA DE PAGO: ${state.liquidationPolicy}", color = Color(0xFFB45309), fontWeight = FontWeight.Bold, fontSize = 7.5.sp)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text("• TRANSPARENCIA: Precio base de la pieza + Servicio de manejo y gestión logística (${state.porcentajeComision.toInt()}%).", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 7.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        DocumentSectionTitle("2. OPCIONES DE CONFIGURACIÓN Y PRECIOS", brandColor)

                        state.options.forEach { opt ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(opt.title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 9.sp)
                                        Box(modifier = Modifier.background(brandColor).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text("$${String.format("%,.0f", opt.price)} MXN", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 9.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(opt.description, color = Color(0xFF475569), fontSize = 7.5.sp)
                                    Text("Condición: ${opt.condition} | Entrega: ${opt.delivery} | Garantía: ${opt.warranty}", color = Color(0xFF1E293B), fontSize = 7.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Total Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFBEB))
                                .border(2.dp, brandColor, RoundedCornerShape(4.dp))
                                .padding(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                                Text("Subtotal base: $${String.format("%,.0f", state.calculateTotalSobrePedidoBase())} MXN", color = Color(0xFF334155), fontSize = 8.sp)
                                Text("Cargo gestión ${state.porcentajeComision.toInt()}%: $${String.format("%,.0f", state.calculateSobrePedidoComision())} MXN", color = Color(0xFF334155), fontSize = 8.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("TOTAL FINAL: ", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 10.sp)
                                    Box(modifier = Modifier.background(brandColor).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text("$${String.format("%,.0f", state.calculateTotalSobrePedidoFinal())} MXN", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Process Steps
                        if (state.procesoMostrar && state.procesoPasos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DocumentSectionTitle(state.procesoTituloSeccion, brandColor)
                            state.procesoPasos.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (step.marked) Color(0xFFFFFBEB) else Color(0xFFF8FAFC))
                                        .border(1.dp, if (step.marked) brandColor else Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(if (step.marked) brandColor else headerBg, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${index + 1}", color = if (step.marked) Color.Black else Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(step.title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 8.sp)
                                        Text(step.description, color = Color(0xFF475569), fontSize = 7.sp, lineHeight = 10.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }

            // --- FOOTER BANNER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = brandColor, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(footerPhone, color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = brandColor, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(footerAddress, color = Color(0xFFCBD5E1), fontSize = 6.5.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = brandTitle.uppercase(),
                            color = brandColor,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 9.sp
                        )
                        Text("TORREÓN • GÓMEZ PALACIO • LERDO", color = Color(0xFFCBD5E1), fontSize = 6.5.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DigitalSealBadge(sealType: String, brandColor: Color) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .border(2.dp, if (sealType == "SELLO_DIGITAL_DORADO") brandColor else Color.Black, CircleShape)
            .background(if (sealType == "SELLO_DIGITAL_DORADO") Color(0xFFFEF08A) else Color(0xFF0F172A), CircleShape)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Sello de Garantía",
                tint = if (sealType == "SELLO_DIGITAL_DORADO") Color(0xFFB45309) else brandColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "JSV SEAL",
                color = if (sealType == "SELLO_DIGITAL_DORADO") Color.Black else Color.White,
                fontSize = 5.5.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "30 DÍAS",
                color = if (sealType == "SELLO_DIGITAL_DORADO") Color(0xFFB45309) else brandColor,
                fontSize = 5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DocumentSectionTitle(title: String, accentColor: Color = JsvGold) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(accentColor)
            .border(width = 1.dp, color = Color(0xFFD97706))
            .padding(horizontal = 8.dp, vertical = 3.5.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = Color.Black,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.5.sp
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun MetaCellHeader(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(JsvDark)
            .padding(4.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 6.5.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MetaCellValue(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White)
            .padding(4.dp)
    ) {
        Text(text = text.ifBlank { "N/A" }, color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp)
    ) {
        Text(text = label, color = Color(0xFF475569), fontSize = 7.sp, modifier = Modifier.weight(1f))
        Text(text = value.ifBlank { "N/A" }, color = Color.Black, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        val cleanHex = hex.replace("#", "").trim()
        val colorInt = when (cleanHex.length) {
            6 -> ("FF$cleanHex").toLong(16)
            8 -> cleanHex.toLong(16)
            else -> return fallback
        }
        Color(colorInt)
    } catch (e: Exception) {
        fallback
    }
}
