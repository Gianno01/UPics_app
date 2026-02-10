package com.example.upics

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun PreviewScreen(
    navController: NavController,
    photoUri: Uri?,
    onEditClick: () -> Unit,
    onPrintClick: () -> Unit
) {
    val context = LocalContext.current

    if (photoUri == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // 1. Header Comune
        CommonHeader()

        Spacer(modifier = Modifier.weight(1f))

        // 2. LA POLAROID
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            PolaroidFrame(photoUri = photoUri)
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. AREA COMANDI (PULITA E FUNZIONALE)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            shadowElevation = 16.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp), // Spazio tra i bottoni
                verticalAlignment = Alignment.CenterVertically
            ) {

                // TASTO INDIETRO (Piccolo e rotondo a sinistra)
                OutlinedIconButton(
                    onClick = { navController.popBackStack() },
                    shape = CircleShape, // Rotondo
                    border = BorderStroke(1.dp, Color.LightGray), // Bordo sottile
                    modifier = Modifier.size(56.dp) // Stessa altezza degli altri bottoni
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                // Tasto FILTERS (Si espande)
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    modifier = Modifier
                        .weight(1f) // Prende lo spazio disponibile
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Filters", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                // Tasto PRINT (Si espande e colorato)
                Button(
                    onClick = onPrintClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8BC34A),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f) // Prende lo spazio disponibile
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Print", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PolaroidFrame(photoUri: Uri) {
    val context = LocalContext.current

    Surface(
        color = Color.White,
        modifier = Modifier
            .rotate(-2f)
            .shadow(10.dp, RoundedCornerShape(2.dp)),
        shape = RoundedCornerShape(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(photoUri)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Print Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFEEEEEE))
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}