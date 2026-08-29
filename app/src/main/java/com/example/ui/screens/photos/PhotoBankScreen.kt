package com.example.ui.screens.photos

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhotoItem
import com.example.ui.components.JsvSectionHeader
import com.example.ui.components.JsvField
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
import com.example.ui.viewmodel.JsvTab
import com.example.ui.viewmodel.JsvViewModel

@Composable
fun PhotoBankScreen(
    viewModel: JsvViewModel,
    modifier: Modifier = Modifier
) {
    val photos by viewModel.allPhotos.collectAsState()

    var selectedCategory by remember { mutableStateOf("Todos") }
    var showAddDialog by remember { mutableStateOf(false) }
    var photoToDelete by remember { mutableStateOf<PhotoItem?>(null) }

    val categories = listOf("Todos", "Motor", "Transmisión", "Compresor", "Frenos", "Suspensión", "General")

    val filteredPhotos = photos.filter {
        selectedCategory == "Todos" || it.category.equals(selectedCategory, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JsvDarker)
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        JsvSectionHeader(
            title = "🖼️ Banco de Fotos e Inspección",
            trailingContent = {
                Text("${photos.size} fotos", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JsvGold,
                            selectedLabelColor = Color.Black,
                            containerColor = JsvSurface,
                            labelColor = JsvTextMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = JsvGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = JsvOnPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SUBIR", color = JsvOnPrimary, fontWeight = FontWeight.Black, fontSize = 9.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPhotos, key = { it.id }) { photo ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = JsvSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JsvBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(JsvDarkElevated)
                                .border(1.dp, JsvBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = JsvGold,
                                modifier = Modifier
                                    .size(42.dp)
                                    .rotate(photo.rotationDegrees.toFloat())
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = photo.title,
                            color = JsvTextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "${photo.category} • ${photo.rotationDegrees}°",
                            color = JsvTextMuted,
                            fontSize = 8.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.rotatePhoto(photo) }, modifier = Modifier.size(26.dp)) {
                                Icon(Icons.Default.RotateRight, contentDescription = "Rotar", tint = JsvBlue, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { photoToDelete = photo }, modifier = Modifier.size(26.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = JsvRed, modifier = Modifier.size(16.dp))
                            }
                            Button(
                                onClick = {
                                    viewModel.applyPhotoToQuote(photo)
                                    viewModel.selectTab(JsvTab.EDITOR)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = JsvGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("USAR", color = JsvOnPrimary, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Photo Dialog
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var filename by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("General") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = JsvSurface,
            title = { Text("Registrar Foto en Banco Local", color = JsvGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    JsvField(label = "Título descriptivo (ej. Bomba Hidráulica OEM)", value = title, onValueChange = { title = it })
                    JsvField(label = "Nombre de archivo (ej. bomba_nissan.jpg)", value = filename, onValueChange = { filename = it })
                    JsvField(label = "Categoría (Motor, Transmisión, Suspensión, etc.)", value = category, onValueChange = { category = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addPhoto(
                                filename = filename.ifBlank { "foto_${System.currentTimeMillis()}.jpg" },
                                title = title,
                                category = category.ifBlank { "General" }
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvGold)
                ) {
                    Text("GUARDAR FOTO", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }

    // Delete photo confirmation
    photoToDelete?.let { p ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            containerColor = JsvSurface,
            title = { Text("Eliminar Foto", color = JsvRed, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar '${p.title}' del banco?", color = JsvTextBody) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePhoto(p)
                        photoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JsvRed)
                ) {
                    Text("ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text("CANCELAR", color = JsvTextMuted)
                }
            }
        )
    }
}
