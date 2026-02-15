package com.example.upics

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun HomeScreen(navController: NavController? = null, onOpenGallery: () -> Unit = {}) {
    val context = LocalContext.current

    // LEGGIAMO LO STATO GLOBALE: L'utente ha pagato?
    val hasCredit = TransferState.hasCredit

    // Loader Immagini (come prima)
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
        }
        .build()

    val backgroundPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.bho).build(),
        imageLoader = imageLoader
    )

    val scrittaPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.scritta).build(),
        imageLoader = imageLoader
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. SFONDO
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CommonHeader()

            Spacer(modifier = Modifier.height(20.dp))

            // 2. LOGO CENTRALE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = scrittaPainter,
                    contentDescription = "Scritta Home",
                    modifier = Modifier.fillMaxWidth(0.8f).wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // 3. SEZIONE CREDITI ATTIVI (Nuova!)
        // Se l'utente ha pagato, appare questo bottone flottante sopra il menu
        if (hasCredit) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 220.dp) // Posizionato sopra il menu bianco
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        // 1. CONSUMA IL CREDITO
                        TransferState.hasCredit = false

                        // 2. VAI ALLA STAMPA (Usiamo un placeholder per la demo)
                        val dummyUri = Uri.encode("android.resource://com.example.upics/drawable/sample")
                        navController?.navigate("audio_connect/$dummyUri")
                    },
                    containerColor = Color(0xFF8BC34A), // Verde Upics
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Print, "Print Now") },
                    text = { Text("CREDIT AVAILABLE: PRINT NOW") }
                )
            }
        }

        // 4. MENU INFERIORE
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Riga pulsanti Azione
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActionButton(Icons.Default.Upload, "Upload\nphotos") { onOpenGallery() }
                    ActionButton(Icons.Default.PhotoCamera, "Take\nphotos") { Toast.makeText(context, "Usa Upload", Toast.LENGTH_SHORT).show() }

                    // Il pulsante History punta alla nuova schermata
                    ActionButton(Icons.Default.DateRange, "History\n${if(hasCredit) "(1)" else ""}") {
                        navController?.navigate("history")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                MenuButton("Need Help?", Color(0xFFE0E0E0), Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton("About Us", Color(0xFFE0E0E0), Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton("Terms and Condictions", Color.Black, Color.White)
            }
        }
    }
}

// --- COMPONENTI LOCALI ---
@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MenuButton(text: String, color: Color, textColor: Color) {
    val context = LocalContext.current
    Button(
        onClick = { Toast.makeText(context, text, Toast.LENGTH_SHORT).show() },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = textColor),
        border = if (color == Color.Black) null else BorderStroke(1.dp, Color.Gray)
    ) {
        Text(text, fontSize = 16.sp)
    }
}