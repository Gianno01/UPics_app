package com.example.upics

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PrintSuccessScreen(navController: NavController) {
    // Rileva orientamento
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Animazione di ingresso (Scale + Bounce)
    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = true

    val scale by animateFloatAsState(
        targetValue = if (transitionState.targetState) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce"
    )

    // Layout Principale (Sfondo Verde Pieno)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8BC34A)) // Verde Brand Full Screen
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLandscape) {
            // === LANDSCAPE (Orizzontale) ===
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Sinistra: Icona Gigante
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    SuccessIcon(scale)
                }

                // Destra: Testo e Bottoni
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SuccessTextContent()
                    Spacer(modifier = Modifier.height(32.dp))
                    HomeButton(navController)
                }
            }
        } else {
            // === PORTRAIT (Verticale) ===
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                SuccessIcon(scale)

                Spacer(modifier = Modifier.height(40.dp))

                SuccessTextContent()

                Spacer(modifier = Modifier.weight(1f))

                HomeButton(navController)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- COMPONENTI ---

@Composable
fun SuccessIcon(scale: Float) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 10.dp,
        modifier = Modifier
            .size(140.dp)
            .scale(scale) // Applica l'animazione di rimbalzo
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color(0xFF8BC34A),
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun SuccessTextContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Awesome!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your photo has been printed.",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please collect it from the tray below ↓",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HomeButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF8BC34A)
        ),
        shape = RoundedCornerShape(30.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f) // Non troppo largo
            .height(60.dp)
    ) {
        Icon(Icons.Default.Home, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "Back to Home",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}