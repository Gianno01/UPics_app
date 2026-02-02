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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

// --- SCHERMATA 1: STAMPA IN CORSO (Payment Successful) ---
@Composable
fun PrintingScreen(
    navController: NavController,
    photoUri: Uri,
    editState: PhotoEditState
) {
    val context = LocalContext.current
    val matrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix

    // LOGICA TIMER: 5 secondi poi va alla schermata di successo
    LaunchedEffect(Unit) {
        delay(5000) // 5 secondi di attesa
        navController.navigate("print_success") {
            // Rimuove la schermata di stampa dal backstack così non si può tornare indietro
            popUpTo("printing") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header semplificato (solo tasto indietro disabilitato o nascosto e titolo)
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tasto indietro finto/disabilitato per layout
            Box(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.weight(1f))
            // Riquadro "Print" come da design
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
            // MINIATURA RESUME (Replica visiva statica)
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth().height(140.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Foto a sinistra (Bianco)
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Piccola anteprima polaroid
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
                    // Testo a destra (Grigio)
                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .padding(16.dp)
                    ) {
                        Text("Resume", fontWeight = FontWeight.Bold)
                        Text("photos 1.00€", fontSize = 12.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("tot 1.00€", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.End))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // TESTO VERDE PAGAMENTO OK
            Text(
                "Payment Successful",
                color = Color(0xFF8BC34A), // Verde Brand
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ICONA FOTOCAMERA (Simulazione grafica)
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Printing",
                modifier = Modifier.size(100.dp),
                tint = Color.Black // O grigio scuro
            )

            // Barra di caricamento sotto la camera (simulata)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(120.dp),
                color = Color(0xFF8BC34A)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "We are printing your\nmemories...",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Love",
                tint = Color.Red,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


// --- SCHERMATA 2: STAMPA COMPLETATA (Printing Complete) ---
@Composable
fun PrintSuccessScreen(
    navController: NavController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con Logo e stato
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Piccolo logo in alto a sx (simulato)
            Box(modifier = Modifier.size(40.dp).background(Color.Black, CircleShape))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(Color(0xFF8BC34A), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connected - Turin(IT)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(0.2f))

        // LOGO GRANDE UPICS (Simulato col testo se non c'è risorsa, o usa R.raw.logo)
        Text(
            "upics",
            fontSize = 60.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
        Text("by polaroid", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))

        // ICONA CHECK ANIMATA/STATICA
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            // Cerchio verde sfondo
            Box(modifier = Modifier.matchParentSize().clip(CircleShape).background(Color(0xFF8BC34A)))
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))

            // Decorazioni attorno (coriandoli simulati)
            // (Omessi per semplicità, ma puoi usare Canvas se vuoi)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Printing Complete!", fontSize = 18.sp, color = Color.Gray)
        Text("THANKS!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "check the machine, and pick up your photo",
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.5f))

        Divider()

        Spacer(modifier = Modifier.height(24.dp))

        // BOTTONE START AGAIN
        OutlinedButton(
            onClick = {
                // Torna alla Home e pulisce TUTTO lo stack
                navController.navigate("home") {
                    popUpTo("login") { inclusive = false } // Mantiene login ma resetta il resto
                    launchSingleTop = true
                }
            },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
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