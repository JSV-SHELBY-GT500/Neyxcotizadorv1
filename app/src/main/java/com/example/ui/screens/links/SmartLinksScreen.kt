package com.example.ui.screens.links

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JsvSectionHeader
import com.example.ui.theme.JsvBlue
import com.example.ui.theme.JsvBorder
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SmartLinksScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val smartLinks by viewModel.smartLinks.collectAsState()
    val quoteState by viewModel.quoteState.collectAsState()

    var customSearch by remember { mutableStateOf("") }

    val activeQuery = if (customSearch.isNotBlank()) customSearch else smartLinks.queryDisplay

    val encodedQuery = try {
        URLEncoder.encode(activeQuery, StandardCharsets.UTF_8.toString())
    } catch (e: Exception) {
        activeQuery.replace(" ", "+")
    }

    val googleImagesUrl = "https://www.google.com/search?q=$encodedQuery&tbm=isch"
    val googleWebUrl = "https://www.google.com/search?q=$encodedQuery"
    val mlUrl = "https://listado.mercadolibre.com.mx/$encodedQuery#D[A:$encodedQuery]"
    val amazonUrl = "https://www.amazon.com.mx/s?k=$encodedQuery"
    val ytUrl = "https://www.youtube.com/results?search_query=$encodedQuery+autoparte"
    val fbUrl = "https://www.facebook.com/search/top?q=$encodedQuery"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        JsvSectionHeader("🔍 Enlaces Inteligentes de Búsqueda y Cotización")
        Spacer(modifier = Modifier.height(8.dp))

        // Search Input
        OutlinedTextField(
            value = customSearch,
            onValueChange = { customSearch = it },
            label = { Text("Término de búsqueda activo (o usa la pieza del editor)", fontSize = 10.sp) },
            placeholder = { Text(smartLinks.queryDisplay, fontSize = 10.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JsvGold) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JsvGold,
                unfocusedBorderColor = JsvBorder,
                focusedTextColor = JsvTextWhite,
                unfocusedTextColor = JsvTextWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = JsvSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Query Actual: ", color = JsvGold, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                Text(activeQuery, color = JsvTextWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                LinkEngineCard(
                    title = "Google Imágenes (Diagramas & Piezas OEM)",
                    desc = "Compara fotos reales, diagramas de despiece y etiquetas del fabricante.",
                    tag = "INSPECCIÓN",
                    tagColor = JsvBlue,
                    icon = Icons.Default.Image,
                    onClick = { openUrl(context, googleImagesUrl) }
                )
            }

            item {
                LinkEngineCard(
                    title = "Mercado Libre México",
                    desc = "Revisa precios de mercado nacional, piezas usadas, refacciones de colisión.",
                    tag = "PRECIOS MX",
                    tagColor = JsvGold,
                    icon = Icons.Default.ShoppingCart,
                    onClick = { openUrl(context, mlUrl) }
                )
            }

            item {
                LinkEngineCard(
                    title = "Amazon México Autopartes",
                    desc = "Herramientas de instalación, kits de empaques y repuestos nuevos directos.",
                    tag = "REFACCIONES",
                    tagColor = JsvGreen,
                    icon = Icons.Default.Store,
                    onClick = { openUrl(context, amazonUrl) }
                )
            }

            item {
                LinkEngineCard(
                    title = "YouTube Video Tutoriales",
                    desc = "Guías de instalación, diagnóstico de falla y desmontaje del vehículo.",
                    tag = "TUTORIALES",
                    tagColor = JsvRed,
                    icon = Icons.Default.PlayCircle,
                    onClick = { openUrl(context, ytUrl) }
                )
            }

            item {
                LinkEngineCard(
                    title = "Google Búsqueda Web Completa",
                    desc = "Catálogos de despiece OEM, números de parte cruzados y foros mecánicos.",
                    tag = "TÉCNICO",
                    tagColor = JsvBlue,
                    icon = Icons.Default.Language,
                    onClick = { openUrl(context, googleWebUrl) }
                )
            }

            item {
                LinkEngineCard(
                    title = "Facebook Marketplace",
                    desc = "Despieces locales y disponibilidad en la Comarca Lagunera.",
                    tag = "LOCAL",
                    tagColor = JsvGold,
                    icon = Icons.Default.OpenInBrowser,
                    onClick = { openUrl(context, fbUrl) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LinkEngineCard(
    title: String,
    desc: String,
    tag: String,
    tagColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = JsvSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(JsvDarkElevated)
                    .border(1.dp, JsvBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tagColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = JsvTextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tagColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(tag, color = JsvOnPrimary, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, color = JsvTextMuted, fontSize = 9.sp, lineHeight = 12.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.OpenInBrowser, contentDescription = "Abrir", tint = JsvGold, modifier = Modifier.size(18.dp))
        }
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback or ignore
    }
}
