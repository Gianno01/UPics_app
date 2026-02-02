package com.example.upics

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay

// --- SCHERMATA 1: STAMPA IN CORSO ---
@Composable
fun PrintingScreen(
    navController: NavController,
    photoUri: Uri,
    editState: PhotoEditState
) {
    val context = LocalContext.current
    val matrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix

    LaunchedEffect(Unit) {
        delay(5000)
        navController.navigate("print_success") {
            popUpTo("printing") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text("Print", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth().height(140.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .border(1.dp, Color.LightGray)
                                .padding(4.dp)
                                .clipToBounds()
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = photoUri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                colorFilter = if (matrix != null) ColorFilter.colorMatrix(matrix) else null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(0.6f).padding(16.dp)) {
                        Text("Resume", fontWeight = FontWeight.Bold)
                        Text("photos 1.00€", fontSize = 12.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("tot 1.00€", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.End))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Payment Successful", color = Color(0xFF8BC34A), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))
            Icon(Icons.Default.CameraAlt, "Printing", Modifier.size(100.dp), tint = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.width(120.dp), color = Color(0xFF8BC34A))
            Spacer(modifier = Modifier.height(40.dp))
            Text("We are printing your\nmemories...", textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Icon(Icons.Default.Favorite, "Love", tint = Color.Red, modifier = Modifier.size(40.dp))
        }
    }
}


// --- SCHERMATA 2: STAMPA COMPLETATA ---
@Composable
fun PrintSuccessScreen(navController: NavController) {
    val context = LocalContext.current

    // Carichiamo il logo (assicurati che R.raw.scritta esista, altrimenti usa R.raw.logo)
    val logoPainter = rememberAsyncImagePainter(model = R.raw.scritta)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End // Allinea tutto a destra
        ) {
            // QUI HO RIMOSSO IL CERCHIO NERO CHE C'ERA PRIMA

            // Rimane solo lo stato "Connected"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(Color(0xFF8BC34A), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connected - Turin(IT)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(0.2f))

        // QUI HO SOSTITUITO IL TESTO "UPICS BY POLAROID" CON L'IMMAGINE
        Image(
            painter = logoPainter,
            contentDescription = "Logo Upics",
            modifier = Modifier
                .fillMaxWidth(0.6f) // Regola la larghezza come preferisci
                .height(80.dp),       // Regola l'altezza
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ICONA CHECK
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
            Box(modifier = Modifier.matchParentSize().clip(CircleShape).background(Color(0xFF8BC34A)))
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Printing Complete!", fontSize = 18.sp, color = Color.Gray)
        Text("THANKS!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))
        Text("check the machine, and pick up your photo", fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.weight(0.5f))

        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        // BOTTONE START AGAIN
        OutlinedButton(
            onClick = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = false }
                    launchSingleTop = true
                }
            },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text("Start again", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // FOOTER
        Text("upics", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("any issue? ", fontSize = 12.sp)
            Text("Call us.", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}