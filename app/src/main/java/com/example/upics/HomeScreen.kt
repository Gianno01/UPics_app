package com.example.upics

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    // I NUOVI CONTATORI
    val creditCount = TransferState.credits
    val hasCredit = creditCount > 0

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
        // 1. Sfondo
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CommonHeader()
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Image(
                    painter = scrittaPainter,
                    contentDescription = "Scritta Home",
                    modifier = Modifier.fillMaxWidth(0.8f).wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // 2. Bottone "Stampa Ora" (Se hai crediti)
        if (hasCredit) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 250.dp)) {
                ExtendedFloatingActionButton(
                    onClick = {
                        TransferState.credits -= 1
                        val dummyUri = Uri.encode("android.resource://com.example.upics/drawable/sample")
                        navController?.navigate("audio_connect/$dummyUri")
                    },
                    containerColor = Color(0xFF8BC34A),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Print, "Print Now", modifier = Modifier.size(24.dp)) },
                    text = { Text("PRINT NOW ($creditCount)", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // 3. MENU INFERIORE (Stile Polaroid Moderno)
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color(0xFFF5F5F5), // Sfondo grigio chiaro per far risaltare le carte bianche
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 32.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Riga con le 2 Azioni Principali (Cards stile Polaroid)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card Upload
                    MainActionCard(
                        icon = Icons.Default.Image,
                        title = "New Magic",
                        subtitle = "Upload a photo",
                        modifier = Modifier.weight(1f),
                        onClick = onOpenGallery
                    )

                    // Card History
                    MainActionCard(
                        icon = Icons.Default.History,
                        title = "Gallery",
                        subtitle = if (hasCredit) "$creditCount Credits Available" else "View past prints",
                        modifier = Modifier.weight(1f),
                        onClick = { navController?.navigate("history") }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer Minimalista
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextLink("Help") { navController?.navigate("help") }
                    Text("  •  ", color = Color.LightGray)
                    TextLink("About Us") { navController?.navigate("about") }
                    Text("  •  ", color = Color.LightGray)
                    TextLink("Terms") { navController?.navigate("terms") }                }
            }
        }
    }
}

// --- NUOVI COMPONENTI LOCALI CON STILE "POLAROID VIBE" ---

@Composable
fun MainActionCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(160.dp), // Più alte per dare respiro
        shape = RoundedCornerShape(20.dp),
        color = Color.White, // BIANCO PURO per massimo contrasto (effetto carta foto)
        shadowElevation = 12.dp, // Ombra profonda per effetto "oggetto fisico"
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)) // Bordino sottile
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // L'icona in alto (la "zona foto")
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.Black, // Contrasto massimo su bianco
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Il testo in basso (la "zona scritta a mano")
            // Testo molto più grande e leggibile
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun TextLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(8.dp)
    )
}