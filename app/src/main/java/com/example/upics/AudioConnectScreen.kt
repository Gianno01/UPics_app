package com.example.upics

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun AudioConnectScreen(navController: NavController, encodedUri: String?, pinCode: String?) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // --- STATI DI CONTROLLO ---
    var isPlaying by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf("Ready to connect") }
    var connectionSuccess by remember { mutableStateOf(false) }

    // Intercetta il tasto indietro
    BackHandler {
        if (isPlaying) {
            Toast.makeText(context, "Stop transmission first!", Toast.LENGTH_SHORT).show()
        } else {
            navController.popBackStack()
        }
    }

    // --- LOGICA DI SIMULAZIONE AUDIO ---
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            connectionStatus = "Transmitting Audio Token..."
            // Simula 4 secondi di riproduzione audio
            delay(4000)

            // Finito!
            isPlaying = false
            connectionSuccess = true
            connectionStatus = "Print Successful! 🎉"

            // Aspetta un attimo e torna alla Home
            delay(2000)
            Toast.makeText(context, "Magic Delivered!", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true } // Pulisce la cronologia di navigazione
            }
        } else if (!connectionSuccess) {
            connectionStatus = "Ready to connect"
        }
    }

    // --- LAYOUT ---
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAFAFA)
    ) {
        if (isLandscape) {
            // ==========================================
            // LAYOUT ORIZZONTALE (Divide a metà per non schiacciare)
            // ==========================================
            Row(modifier = Modifier.fillMaxSize()) {
                // METÀ SINISTRA: Testi e Controlli
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Header(navController, isPlaying)
                    Spacer(Modifier.weight(1f))
                    InstructionText(connectionStatus, connectionSuccess)
                    Spacer(Modifier.weight(1f))
                }

                // METÀ DESTRA: Il Radar con il Bottone
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    RadarButton(isPlaying, connectionSuccess) {
                        if (!connectionSuccess) isPlaying = !isPlaying
                    }
                }
            }
        } else {
            // ==========================================
            // LAYOUT VERTICALE (Classico)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header(navController, isPlaying)

                Spacer(modifier = Modifier.height(32.dp))
                InstructionText(connectionStatus, connectionSuccess)

                // IL RADAR AL CENTRO
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    RadarButton(isPlaying, connectionSuccess) {
                        if (!connectionSuccess) isPlaying = !isPlaying
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // FOOTER INFORMATIVO
                Row(
                    modifier = Modifier
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Hearing, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Make sure your phone volume is up and hold it near the machine.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// --- COMPONENTI LOCALI ---

@Composable
fun Header(navController: NavController, isPlaying: Boolean) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (isPlaying) Toast.makeText(context, "Stop transmission first!", Toast.LENGTH_SHORT).show()
                else navController.popBackStack()
            },
            modifier = Modifier.clip(CircleShape).background(Color(0xFFF5F5F5)).size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = if (isPlaying) Color.LightGray else Color.Black)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text("Audio Connection", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InstructionText(status: String, isSuccess: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = status,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSuccess) Color(0xFF4CAF50) else Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!isSuccess) {
            Text(
                text = "Press the button when you are standing in front of the Upics machine.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun RadarButton(isPlaying: Boolean, isSuccess: Boolean, onClick: () -> Unit) {
    // Gestione Animazioni Infinite
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")

    // Le tre onde (si animano solo se isPlaying è true)
    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (isPlaying) 2.5f else 1f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "Wave1"
    )
    val wave1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = if (isPlaying) 0f else 0f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "Wave1Alpha"
    )

    val wave2Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (isPlaying) 2.5f else 1f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearOutSlowInEasing), initialStartOffset = StartOffset(500), repeatMode = RepeatMode.Restart), label = "Wave2"
    )
    val wave2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = if (isPlaying) 0f else 0f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearOutSlowInEasing), initialStartOffset = StartOffset(500), repeatMode = RepeatMode.Restart), label = "Wave2Alpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
        // Onda 1
        if (isPlaying) {
            Box(modifier = Modifier.size(100.dp).scale(wave1Scale).border(2.dp, Color(0xFF8BC34A).copy(alpha = wave1Alpha), CircleShape))
            Box(modifier = Modifier.size(100.dp).scale(wave1Scale).background(Color(0xFF8BC34A).copy(alpha = wave1Alpha * 0.3f), CircleShape))
        }

        // Onda 2
        if (isPlaying) {
            Box(modifier = Modifier.size(100.dp).scale(wave2Scale).border(2.dp, Color(0xFF8BC34A).copy(alpha = wave2Alpha), CircleShape))
            Box(modifier = Modifier.size(100.dp).scale(wave2Scale).background(Color(0xFF8BC34A).copy(alpha = wave2Alpha * 0.3f), CircleShape))
        }

        // Il Bottone Fisico al Centro
        Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = when {
                    isSuccess -> Color(0xFF4CAF50) // Verde Successo
                    isPlaying -> Color.Black // Nero se sta suonando (per fermarlo)
                    else -> Color(0xFF8BC34A) // Verde Upics se pronto
                },
                contentColor = Color.White
            ),
            modifier = Modifier.size(120.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when {
                        isSuccess -> Icons.Default.Check
                        isPlaying -> Icons.Default.Stop
                        else -> Icons.Default.VolumeUp
                    },
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        isSuccess -> "DONE"
                        isPlaying -> "STOP"
                        else -> "START"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}