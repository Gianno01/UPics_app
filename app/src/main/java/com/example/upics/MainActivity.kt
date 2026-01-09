package com.example.upics

import android.Manifest
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Upload
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

// --- Navigazione Principale e Gestione Dati ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Variabile di stato per conservare le foto selezionate dalla galleria
    var selectedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Launcher per il Photo Picker di Android (Galleria)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        // Se l'utente ha selezionato qualcosa...
        if (uris.isNotEmpty()) {
            selectedPhotos = uris // 1. Salva le foto
            navController.navigate("preview") // 2. Vai alla schermata di anteprima
        }
    }

    NavHost(navController = navController, startDestination = "login") {

        // Rotta 1: Login
        composable("login") { LoginScreen(navController) }

        // Rotta 2: Home
        composable("home") {
            HomeScreen(
                navController = navController,
                // Passiamo la funzione che apre la galleria
                onOpenGallery = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }

        // Rotta 3: Anteprima (Usa il file PreviewScreen.kt esterno)
        composable("preview") {
            // Passiamo le foto vere salvate nella variabile di stato
            PreviewScreen(navController = navController, photos = selectedPhotos)
        }
    }
}

// --- Schermata Login (QR Code Simulato) ---
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
            Toast.makeText(context, "Permesso fotocamera necessario", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
            Text("Scansiona QR per Connetterti")
        }
    }
}

// --- Schermata Home ---
@Composable
fun HomeScreen(
    navController: NavController? = null,
    onOpenGallery: () -> Unit = {} // Funzione per aprire la galleria
) {
    val context = LocalContext.current

    // Configurazione ImageLoader (per GIF e SVG)
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
        }
        .build()

    // Risorse Immagini
    val backgroundPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.bho).build(),
        imageLoader = imageLoader
    )
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
            // 2. HEADER TOP BAR
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
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            // 3. ELEMENTO DECORATIVO CENTRALE
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = " ",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Image(
                    // Sostituisci 'frame_home' con il nome esatto del tuo file in res/drawable
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.scritta),
                    contentDescription = "Polaroid Frame",
                    contentScale = ContentScale.Fit, // Adatta l'immagine senza tagliarla
                    modifier = Modifier
                        .size(320.dp) // Imposta la grandezza che preferisci
                    // .padding(8.dp) // Rimuovi o aggiungi padding se serve
                )
                // ---------------------
            }
        }

        // 4. PANNELLO INFERIORE (Menu)
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
                // Riga Icone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // PULSANTE UPLOAD (Apre Galleria)
                    ActionButton(
                        icon = Icons.Default.Upload,
                        text = "Upload\nphotos",
                        onClick = { onOpenGallery() }
                    )
                    // PULSANTE TAKE PHOTO (Simulato per ora)
                    ActionButton(
                        icon = Icons.Default.PhotoCamera,
                        text = "Take\nphotos",
                        onClick = { Toast.makeText(context, "Usa Upload per ora", Toast.LENGTH_SHORT).show() }
                    )
                    // PULSANTE HISTORY
                    ActionButton(
                        icon = Icons.Default.DateRange,
                        text = "History",
                        onClick = { Toast.makeText(context, "History", Toast.LENGTH_SHORT).show() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsanti Testuali
                MenuButton(text = "Need Help?", color = Color(0xFFE0E0E0), textColor = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(text = "About Us", color = Color(0xFFE0E0E0), textColor = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(text = "Terms and Condictions", color = Color.Black, textColor = Color.White)
            }
        }
    }
}

// --- Componenti UI Riutilizzabili ---

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
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = textColor),
        border = if (color == Color.Black) null else BorderStroke(1.dp, Color.Gray)
    ) {
        Text(text, fontSize = 16.sp)
    }
}