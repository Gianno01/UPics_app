package com.example.upics

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun MagicModeScreen(navController: NavController, photoUri: Uri) {
    val context = LocalContext.current

    // --- STATO ---
    // Mantiene il filtro selezionato corrente (Inizia con "Normal")
    var currentFilter by remember { mutableStateOf(FilterUtils.filters[0]) }

    // Gestisce se mostrare o meno il menu dei filtri
    var isFilterMenuVisible by remember { mutableStateOf(true) } // Di default aperto per comodità

    // --- LOGICA DI SALVATAGGIO E USCITA ---
    // Questa funzione salva i dati per la PreviewScreen prima di uscire
    val saveAndExit = {
        // 1. Salviamo il nome del filtro nel "pacchetto" da spedire indietro
        navController.previousBackStackEntry?.savedStateHandle?.set("result_filter", currentFilter.name)
        // 2. Salviamo l'URI (utile per identificare quale foto è stata modificata)
        navController.previousBackStackEntry?.savedStateHandle?.set("result_uri", photoUri.toString())
        // 3. Torniamo indietro
        navController.popBackStack()
    }

    // Gestisce il tasto fisico "Indietro" di Android
    BackHandler {
        saveAndExit()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. HEADER
        // Passiamo la nostra funzione di salvataggio al tasto indietro
        MagicModeHeader(onBackClick = { saveAndExit() })

        Spacer(modifier = Modifier.height(16.dp))

        // 2. AREA FOTO (POLAROID)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .border(1.dp, Color.LightGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Immagine con Filtro Applicato
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context).data(photoUri).build()
                        ),
                        contentDescription = "Editing Photo",
                        contentScale = ContentScale.Crop,
                        // QUI AVVIENE LA MAGIA DEL FILTRO VISIVO:
                        colorFilter = ColorFilter.colorMatrix(currentFilter.colorMatrix),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .clip(RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // 3. TOOLBAR INFERIORE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {

            // Sezione Dinamica: Mostra i filtri se il pulsante è attivo
            if (isFilterMenuVisible) {
                // Questo componente viene preso da CommonComponents.kt
                FilterSelectionMenu(
                    currentFilterName = currentFilter.name,
                    onFilterSelected = { newFilter ->
                        currentFilter = newFilter // Aggiorna il filtro
                    },
                    onClose = { isFilterMenuVisible = false } // Chiude il menu
                )
            }

            Divider()

            // Pulsanti Strumenti
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tasto TESTO (Placeholder)
                EditorToolButton(
                    icon = Icons.Default.TextFields,
                    label = "Text",
                    isActive = false,
                    onClick = { Toast.makeText(context, "Text coming soon", Toast.LENGTH_SHORT).show() }
                )

                // Tasto FILTRO (ATTIVO)
                EditorToolButton(
                    icon = Icons.Default.FormatColorFill,
                    label = "Filter",
                    isActive = isFilterMenuVisible, // Si illumina se il menu è aperto
                    onClick = { isFilterMenuVisible = !isFilterMenuVisible } // Apre/Chiude menu
                )

                // Tasto EMOJI (Placeholder)
                EditorToolButton(
                    icon = Icons.Default.EmojiEmotions,
                    label = "Emoji",
                    isActive = false,
                    onClick = { Toast.makeText(context, "Emoji coming soon", Toast.LENGTH_SHORT).show() }
                )

                // Tasto CROP (Placeholder)
                EditorToolButton(
                    icon = Icons.Default.Crop,
                    label = "Crop",
                    isActive = false,
                    onClick = { Toast.makeText(context, "Crop coming soon", Toast.LENGTH_SHORT).show() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tasto Stampa (Esempio azione)
            Button(
                onClick = { Toast.makeText(context, "Printing with ${currentFilter.name} filter", Toast.LENGTH_SHORT).show() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8BC34A),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Componenti Locali per UI ---

@Composable
fun MagicModeHeader(onBackClick: () -> Unit) {
    Column {
        CommonHeader() // Header comune
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Magic mode",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun EditorToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isActive) Color(0xFFFFF176) else Color.Transparent)
                .border(if (isActive) 2.dp else 1.dp, if (isActive) Color(0xFFFBC02D) else Color.Gray, RoundedCornerShape(12.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = Color.Black)
    }
}