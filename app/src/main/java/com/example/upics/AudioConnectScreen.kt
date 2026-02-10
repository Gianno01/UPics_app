package com.example.upics

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// VERSIONE UI-ONLY (Nessuna logica reale, solo grafica e navigazione)
@Composable
fun AudioConnectScreen(
    navController: NavController,
    photoUri: String
) {
    // Simulazione Timer: dopo 3 secondi vai avanti automaticamente
    LaunchedEffect(Unit) {
        delay(3000) // Aspetta 3 secondi fingendo di ascoltare

        // --- FIX CRASH: CODIFICA L'URI PRIMA DI SPEDIRLO ---
        val encodedUri = Uri.encode(photoUri)

        navController.navigate("printing/$encodedUri") {
            popUpTo("audio_connect") { inclusive = true }
        }
    }

    // Animazione Onda Sonora (Pulse)
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Testo Istruzioni
        Text(
            text = "Connecting to Printer...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please wait while we listen for the\nmachine signal.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Visualizzatore Audio (Cerchi concentrici animati)
        Box(contentAlignment = Alignment.Center) {
            // Cerchio esterno che si espande e svanisce
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .background(Color(0xFF8BC34A).copy(alpha = alpha), CircleShape)
            )

            // Cerchio interno fisso
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF8BC34A).copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Listening",
                    tint = Color(0xFF8BC34A),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}