package com.example.upics

import android.content.res.Configuration
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
        // SFONDO
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isLandscape) {
            // ==========================================
            // LAYOUT ORIZZONTALE (LANDSCAPE)
            // ==========================================
            Row(modifier = Modifier.fillMaxSize()) {

                // --- COLONNA SINISTRA: SOLO GIF ---
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = scrittaPainter,
                        contentDescription = "GIF",
                        modifier = Modifier.fillMaxWidth(0.8f).wrapContentHeight(), // La GIF ha tutto lo spazio che serve
                        contentScale = ContentScale.Fit
                    )
                }

                // --- COLONNA DESTRA: LOGO IN ALTO + AZIONI IN BASSO ---
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp),
                    shadowElevation = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo spostato in alto a destra (Senza Surface "a bottone")
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(bottom = 8.dp)
                        ) {
                            CommonHeader()
                        }

                        // Molla per spingere i bottoni in basso
                        Spacer(modifier = Modifier.weight(1f))

                        // Contenitore scrollabile per i bottoni (utile per schermi bassi)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            // Bottone Stampa Ora
                            if (hasCredit) {
                                Button(
                                    onClick = {
                                        TransferState.credits -= 1
                                        val dummyUri = Uri.encode("android.resource://com.example.upics/drawable/sample")
                                        navController?.navigate("audio_connect/$dummyUri")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth().height(60.dp)
                                ) {
                                    Icon(Icons.Default.Print, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("PRINT NOW ($creditCount)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            // Tasti Affiancati (New Magic e Gallery)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CompactActionCard(
                                    icon = Icons.Default.Image,
                                    title = "New Magic",
                                    modifier = Modifier.weight(1f),
                                    onClick = onOpenGallery
                                )
                                CompactActionCard(
                                    icon = Icons.Default.History,
                                    title = "Gallery",
                                    modifier = Modifier.weight(1f),
                                    onClick = { navController?.navigate("history") }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Footer
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextLink("Help") { navController?.navigate("help") }
                                Text(" • ", color = Color.LightGray)
                                TextLink("About") { navController?.navigate("about") }
                                Text(" • ", color = Color.LightGray)
                                TextLink("Terms") { navController?.navigate("terms") }
                            }
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // LAYOUT VERTICALE (PORTRAIT) COMPLETO (INTATTO)
            // ==========================================
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Logo Ripristinato)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.statusBarsPadding()) {
                        Box(modifier = Modifier.padding(bottom = 12.dp)) {
                            CommonHeader()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // GIF
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Image(
                        painter = scrittaPainter,
                        contentDescription = "Scritta Home",
                        modifier = Modifier.fillMaxWidth(0.8f).wrapContentHeight(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Spazio vuoto per non far sovrapporre la GIF ai bottoni inferiori
                Spacer(modifier = Modifier.height(260.dp))
            }

            // Bottone "Stampa Ora" (Se hai crediti)
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

            // MENU INFERIORE CON I DUE BOTTONI
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color(0xFFF5F5F5),
                shadowElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 32.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Riga con le 2 Azioni Principali (Cards)
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
                            subtitle = if (hasCredit) "$creditCount Credits" else "View past prints",
                            modifier = Modifier.weight(1f),
                            onClick = { navController?.navigate("history") }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextLink("Help") { navController?.navigate("help") }
                        Text("  •  ", color = Color.LightGray)
                        TextLink("About Us") { navController?.navigate("about") }
                        Text("  •  ", color = Color.LightGray)
                        TextLink("Terms") { navController?.navigate("terms") }
                    }
                }
            }
        }
    }
}

// --- COMPONENTI CARDS ---

// Card grande per il Portrait
@Composable
fun MainActionCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.Black,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

// Card compatta per il Landscape
@Composable
fun CompactActionCard(icon: ImageVector, title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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