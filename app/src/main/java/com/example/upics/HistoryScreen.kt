package com.example.upics

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke // <--- AGGIUNTO: Risolve l'errore rosso
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current

    // Leggiamo lo stato globale dei crediti
    var hasCredit by remember { mutableStateOf(TransferState.hasCredit) }

    // Mock Data: Simuliamo 6 foto nella cronologia
    val historyPhotos = remember {
        listOf(
            "https://picsum.photos/300/300?random=1",
            "https://picsum.photos/300/300?random=2",
            "https://picsum.photos/300/300?random=3",
            "https://picsum.photos/300/300?random=4",
            "https://picsum.photos/300/300?random=5",
            "https://picsum.photos/300/300?random=6"
        )
    }

    // Selezioniamo la prima foto di default
    var selectedPhotoUri by remember { mutableStateOf(historyPhotos.firstOrNull()) }
    var showBuyDialog by remember { mutableStateOf(false) }

    // --- DIALOG ACQUISTO CREDITO ---
    if (showBuyDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = { CircularProgressIndicator(color = Color.Black) },
            title = { Text("Top Up Wallet") },
            text = { Text("Adding 1 Print Credit to your account...") },
            confirmButton = {},
            containerColor = Color.White
        )
        // Simulazione transazione
        LaunchedEffect(Unit) {
            delay(2000)
            TransferState.hasCredit = true
            hasCredit = true // Aggiorna la UI locale
            showBuyDialog = false
            Toast.makeText(context, "Credit Added! You can now print.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // 1. Header Comune
        CommonHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Titolo e Stato Crediti
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Gallery",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Badge Crediti
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (hasCredit) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                    border = if (hasCredit) BorderStroke(1.dp, Color(0xFF8BC34A)) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasCredit) Icons.Default.CheckCircle else Icons.Default.History,
                            contentDescription = null,
                            tint = if (hasCredit) Color(0xFF8BC34A) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasCredit) "1 Credit Active" else "No Credits",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasCredit) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select a photo to print or buy more credits.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Griglia Foto
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(historyPhotos) { uri ->
                    val isSelected = (selectedPhotoUri == uri)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 4.dp else 0.dp,
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedPhotoUri = uri }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "History Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Overlay scuro se non selezionato
                        if (!isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White.copy(alpha = 0.3f))
                            )
                        }

                        // Icona di selezione
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.White, RoundedCornerShape(50))
                            )
                        }
                    }
                }

                // Card "Aggiungi" finta
                item {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEEEEEE))
                            .clickable { Toast.makeText(context, "Upload new", Toast.LENGTH_SHORT).show() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. ACTION BAR DINAMICA
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasCredit) {
                        // --- CASO A: HAI I CREDITI -> STAMPA ---
                        Button(
                            onClick = {
                                if (selectedPhotoUri != null) {
                                    // 1. CONSUMA IL CREDITO
                                    TransferState.hasCredit = false // Globale
                                    hasCredit = false // Locale (aggiorna la UI subito)

                                    // 2. NAVIGA
                                    val encodedUri = Uri.encode(selectedPhotoUri)
                                    navController.navigate("audio_connect/$encodedUri")
                                } else {
                                    Toast.makeText(context, "Select a photo first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8BC34A), // VERDE
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Icon(Icons.Default.Print, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PRINT SELECTED", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    } else {
                        // --- CASO B: NON HAI CREDITI -> COMPRA ---
                        Button(
                            onClick = { showBuyDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black, // NERO
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Icon(Icons.Default.CreditCard, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("BUY CREDIT (1€)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}