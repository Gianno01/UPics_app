package com.example.upics

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

// --- Navigazione ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen() }
    }
}

// --- Schermata Login (semplificata per il test) ---
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Connesso!", Toast.LENGTH_SHORT).show()
            navController.navigate("home") { popUpTo("login") { inclusive = true } }
        } else {
            Toast.makeText(context, "Permesso necessario", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
            Text("Scansiona QR per Connetterti")
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
        }
        .build()

    // Painter per lo sfondo GIF
    val backgroundPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.bho).build(),
        imageLoader = imageLoader
    )
    // Painter per il logo SVG
    val logoPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.logo).build(),
        imageLoader = imageLoader
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. SFONDO GIF
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // 2. TOP BAR (Logo e Stato Connessione)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = logoPainter,
                    contentDescription = "Logo Upics",
                    modifier = Modifier.size(60.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Connected - Turin(IT)",
                        color = Color.White, // Testo bianco per contrasto sullo sfondo scuro
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. PANNELLO INFERIORE (Contenitore bianco arrotondato)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Riga dei 3 Pulsanti Icona
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActionButton(icon = Icons.Default.Upload, text = "Upload\nphotos")
                    ActionButton(icon = Icons.Default.CameraAlt, text = "Take\nphotos")
                    ActionButton(icon = Icons.Default.History, text = "History")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsanti Rettangolari Inferiori
                MenuButton(text = "Need Help?", color = Color(0xFFE0E0E0), textColor = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(text = "About Us", color = Color(0xFFE0E0E0), textColor = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(text = "Terms and Condictions", color = Color.Black, textColor = Color.White)
            }
        }
    }
}

// --- Componenti Riutilizzabili ---

// Pulsante Quadrato con Icona e Testo
@Composable
fun ActionButton(icon: ImageVector, text: String) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = { Toast.makeText(context, text.replace("\n", " "), Toast.LENGTH_SHORT).show() },
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

// Pulsante Rettangolare Largo
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