package com.example.upics

import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.isActive

@Composable
fun AudioConnectScreen(navController: NavController, encodedUri: String?, pinCode: String?) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // --- STATI DI CONTROLLO ---
    var isPlaying by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf("Ready to connect") }
    var connectionSuccess by remember { mutableStateOf(false) }
    var hasPlayedOnce by remember { mutableStateOf(false) }

    // Mantiene traccia del Job audio per poterlo cancellare se l'utente esce dalla schermata
    var audioJob by remember { mutableStateOf<Job?>(null) }

    // Intercetta il tasto indietro fisico del telefono
    // Ora interrompe e chiude direttamente la schermata
    BackHandler {
        audioJob?.cancel()
        navController.popBackStack()
    }

    val isDoneEnabled = hasPlayedOnce && !isPlaying

    // --- LAYOUT PRINCIPALE ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // --- HEADER CENTRATO COERENTE ---
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Audio Connection",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // --- CORPO CENTRALE ---
        if (isLandscape) {
            // Layout Orizzontale (diviso a metà)
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sinistra: Testi
                Column(
                    modifier = Modifier.weight(1f).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f)) // Molla superiore
                    InstructionText(connectionStatus, connectionSuccess)
                    Spacer(modifier = Modifier.weight(1f)) // Molla inferiore che spinge giù il box
                    InfoBox()
                }
                // Destra: Radar
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    RadarButton(isPlaying) {
                        if (!isPlaying) {
                            audioJob = coroutineScope.launch {
                                isPlaying = true
                                connectionSuccess = false
                                connectionStatus = "Transmitting Audio Token..."

                                // --- INIZIO LOGICA DTMF ---
                                if (pinCode != null) {
                                    withContext(Dispatchers.IO) {
                                        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                                        pinCode.forEach { char ->
                                            // Controllo di cancellazione nel caso l'utente esca
                                            if (!isActive) return@forEach

                                            val toneType = when (char) {
                                                '1' -> ToneGenerator.TONE_DTMF_1
                                                '2' -> ToneGenerator.TONE_DTMF_2
                                                '3' -> ToneGenerator.TONE_DTMF_3
                                                '4' -> ToneGenerator.TONE_DTMF_4
                                                '5' -> ToneGenerator.TONE_DTMF_5
                                                '6' -> ToneGenerator.TONE_DTMF_6
                                                '7' -> ToneGenerator.TONE_DTMF_7
                                                '8' -> ToneGenerator.TONE_DTMF_8
                                                '9' -> ToneGenerator.TONE_DTMF_9
                                                '0' -> ToneGenerator.TONE_DTMF_0
                                                else -> -1
                                            }

                                            if (toneType != -1) {
                                                toneGenerator.startTone(toneType, 300)
                                                delay(400)
                                            }
                                        }
                                        toneGenerator.release()
                                    }
                                } else {
                                    // Fallback simulazione 4 secondi se il PIN non c'è
                                    delay(4000)
                                }
                                // --- FINE LOGICA DTMF ---

                                isPlaying = false
                                connectionSuccess = true
                                hasPlayedOnce = true
                                connectionStatus = "Wait for the yellow light!"
                            }
                        }
                    }
                }
            }
        } else {
            // Layout Verticale Standard
            Column(
                modifier = Modifier.weight(1f).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f)) // Molla superiore (bilancia il Radar al centro)
                InstructionText(connectionStatus, connectionSuccess)
                Spacer(modifier = Modifier.height(48.dp))

                RadarButton(isPlaying) {
                    if (!isPlaying) {
                        audioJob = coroutineScope.launch {
                            isPlaying = true
                            connectionSuccess = false
                            connectionStatus = "Transmitting Audio Token..."

                            // --- INIZIO LOGICA DTMF ---
                            if (pinCode != null) {
                                withContext(Dispatchers.IO) {
                                    val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                                    pinCode.forEach { char ->
                                        if (!isActive) return@forEach

                                        val toneType = when (char) {
                                            '1' -> ToneGenerator.TONE_DTMF_1
                                            '2' -> ToneGenerator.TONE_DTMF_2
                                            '3' -> ToneGenerator.TONE_DTMF_3
                                            '4' -> ToneGenerator.TONE_DTMF_4
                                            '5' -> ToneGenerator.TONE_DTMF_5
                                            '6' -> ToneGenerator.TONE_DTMF_6
                                            '7' -> ToneGenerator.TONE_DTMF_7
                                            '8' -> ToneGenerator.TONE_DTMF_8
                                            '9' -> ToneGenerator.TONE_DTMF_9
                                            '0' -> ToneGenerator.TONE_DTMF_0
                                            else -> -1
                                        }

                                        if (toneType != -1) {
                                            toneGenerator.startTone(toneType, 300)
                                            delay(400)
                                        }
                                    }
                                    toneGenerator.release()
                                }
                            } else {
                                // Fallback simulazione
                                delay(4000)
                            }
                            // --- FINE LOGICA DTMF ---

                            isPlaying = false
                            connectionSuccess = true
                            hasPlayedOnce = true
                            connectionStatus = "Print Successful! 🎉"
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // Molla inferiore (spinge InfoBox tutto in basso)
                InfoBox()
            }
        }

        // --- BARRA INFERIORE COERENTE ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tasto Indietro a Sinistra
                OutlinedIconButton(
                    onClick = {
                        audioJob?.cancel()
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                // Tasto Done a Destra
                DoneButton(navController, isDoneEnabled)
            }
        }
    }
}

// --- COMPONENTI LOCALI ---

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
        Text(
            text = if (!isSuccess) "Press START when you are standing in front of the Upics machine."
            else "If the print didn't start, you can press START to try again.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun InfoBox() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Hearing, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Volume up and hold near machine.",
            fontSize = 12.sp,
            color = Color.DarkGray,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun RadarButton(isPlaying: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")

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
        if (isPlaying) {
            Box(modifier = Modifier.size(100.dp).scale(wave1Scale).border(2.dp, Color(0xFF8BC34A).copy(alpha = wave1Alpha), CircleShape))
            Box(modifier = Modifier.size(100.dp).scale(wave1Scale).background(Color(0xFF8BC34A).copy(alpha = wave1Alpha * 0.3f), CircleShape))
            Box(modifier = Modifier.size(100.dp).scale(wave2Scale).border(2.dp, Color(0xFF8BC34A).copy(alpha = wave2Alpha), CircleShape))
            Box(modifier = Modifier.size(100.dp).scale(wave2Scale).background(Color(0xFF8BC34A).copy(alpha = wave2Alpha * 0.3f), CircleShape))
        }

        // Il Bottone Fisico al Centro
        Button(
            onClick = onClick,
            enabled = !isPlaying,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8BC34A),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color.Gray
            ),
            modifier = Modifier.size(120.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 0.dp, disabledElevation = 0.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isPlaying) "PLAYING" else "START",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DoneButton(navController: NavController, isEnabled: Boolean) {
    Button(
        onClick = {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        },
        enabled = isEnabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFE0E0E0),
            disabledContentColor = Color.Gray
        ),
        modifier = Modifier.height(56.dp)
    ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}