package com.example.upics

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Permette di usare il tasto indietro fisico del telefono
    BackHandler { navController.popBackStack() }

    // Leggiamo il numero di crediti e le foto salvate
    var creditCount by remember { mutableIntStateOf(TransferState.credits) }
    val historyPhotos = TransferState.savedPhotos

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
            TransferState.credits += 1
            creditCount = TransferState.credits // Aggiorna la UI locale
            showBuyDialog = false
            Toast.makeText(context, "Credit Added! You have $creditCount credits.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        CommonHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Titolo (Tornato pulito) e Stato Crediti
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Gallery",
                    fontSize = if (isLandscape) 24.sp else 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Badge Crediti Dinamico
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (creditCount > 0) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                    border = if (creditCount > 0) BorderStroke(1.dp, Color(0xFF8BC34A)) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (creditCount > 0) Icons.Default.CheckCircle else Icons.Default.History,
                            contentDescription = null,
                            tint = if (creditCount > 0) Color(0xFF8BC34A) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (creditCount > 0) "$creditCount Credits" else "No Credits",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (creditCount > 0) Color(0xFF2E7D32) else Color.Gray
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

            // Griglia Foto Reale
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isLandscape) 4 else 2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEEEEEE))
                            .clickable { navController.popBackStack() }, // Premere su NEW ti riporta alla Home
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(4.dp))
                            Text("New", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }

                items(historyPhotos) { uriString ->
                    val isSelected = (selectedPhotoUri == uriString)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 4.dp else 0.dp,
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedPhotoUri = uriString }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.parse(uriString))
                                .crossfade(true)
                                .build(),
                            contentDescription = "History Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (!isSelected) {
                            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.3f)))
                        }

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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ACTION BAR: Tasto Indietro + Tasti Azione coerenti col resto dell'app
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isLandscape) 16.dp else 32.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 1. TASTO INDIETRO (IDENTICO A RESUMESCREEN)
                    OutlinedIconButton(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
                    }

                    // 2. TASTI AZIONE
                    if (creditCount > 0) {
                        val canPrint = selectedPhotoUri != null

                        Button(
                            onClick = { showBuyDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(56.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp) // Evita che il testo si tagli su schermi stretti
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BUY (+1)", fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Button(
                            onClick = {
                                if (canPrint) {
                                    TransferState.credits -= 1
                                    creditCount = TransferState.credits
                                    val encodedUri = Uri.encode(selectedPhotoUri)
                                    navController.navigate("audio_connect/$encodedUri")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canPrint) Color(0xFF8BC34A) else Color(0xFFE0E0E0),
                                contentColor = if (canPrint) Color.White else Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(56.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Print, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (canPrint) "PRINT" else "SELECT", fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    } else {
                        Button(
                            onClick = { showBuyDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("BUY CREDIT (1€)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}