package com.example.upics

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun PrintingScreen(
    navController: NavController,
    photoUri: Uri,
    editState: PhotoEditState
) {
    // Impedisci di tornare indietro durante la stampa
    BackHandler { }

    // Stati per l'animazione del testo
    var progress by remember { mutableFloatStateOf(0f) }
    // Modifica: Testo iniziale diretto
    var statusText by remember { mutableStateOf("Sending your photo...") }

    // Animazione di pulsazione
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Logica di avanzamento
    LaunchedEffect(Unit) {
        // Fase 1: Invio immediato (siamo già connessi)
        delay(1000)
        progress = 0.4f
        statusText = "Printing in progress..."

        // Fase 2: Stampa
        delay(2000)
        progress = 0.8f
        statusText = "Finalizing..."

        // Fase 3: Completato
        delay(1000)
        progress = 1f

        // Naviga alla schermata di successo
        navController.navigate("print_success") {
            popUpTo("printing") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Icona Pulsante al Centro
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(Color(0xFFE8F5E9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = "Printing",
                tint = Color(0xFF8BC34A),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 2. Titolo e Stato
        Text(
            text = "Hold on tight!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = statusText,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Barra di Progresso
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF8BC34A),
            trackColor = Color(0xFFE0E0E0),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8BC34A)
        )
    }
}