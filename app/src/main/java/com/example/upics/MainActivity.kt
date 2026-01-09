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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

// --- GESTIONE NAVIGAZIONE ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedPhotos = uris
            navController.navigate("preview")
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }

        composable("home") {
            HomeScreen(
                navController = navController,
                onOpenGallery = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }

        composable("preview") {
            PreviewScreen(navController = navController, photos = selectedPhotos)
        }

        composable(
            route = "magic_mode/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("photoUri")
            if (uriString != null) {
                val uri = Uri.parse(uriString)
                MagicModeScreen(navController = navController, photoUri = uri)
            }
        }
    }
}

// --- SCHERMATA LOGIN ---
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

// --- SCHERMATA HOME (MODIFICATA) ---
@Composable
fun HomeScreen(
    navController: NavController? = null,
    onOpenGallery: () -> Unit = {}
) {
    val context = LocalContext.current

    // Configurazione Loader per tutte le immagini (Gif, Svg, Png)
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

    // Painter per la nuova immagine "scritta.png"
    // Assicurati che il file sia in res/raw/scritta.png
    val scrittaPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.scritta).build(),
        imageLoader = imageLoader
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Sfondo GIF
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // HEADER
            CommonHeader()

            Spacer(modifier = Modifier.height(20.dp))

            // --- NUOVA SEZIONE CENTRALE ---
            // Al posto del rettangolo Polaroid, mostriamo l'immagine "scritta"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Occupa lo spazio disponibile al centro
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = scrittaPainter,
                    contentDescription = "Scritta Home",
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // Occupa l'80% della larghezza
                        .wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // MENU INFERIORE
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActionButton(
                        icon = Icons.Default.Upload,
                        text = "Upload\nphotos",
                        onClick = { onOpenGallery() }
                    )
                    ActionButton(
                        icon = Icons.Default.PhotoCamera,
                        text = "Take\nphotos",
                        onClick = { Toast.makeText(context, "Usa Upload per ora", Toast.LENGTH_SHORT).show() }
                    )
                    ActionButton(
                        icon = Icons.Default.DateRange,
                        text = "History",
                        onClick = { Toast.makeText(context, "History", Toast.LENGTH_SHORT).show() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                MenuButton(text = "Need Help?", color = Color(0xFFE0E0E0), textColor = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(text = "About Us", color = Color(0xFFE0E0E0), textColor = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                MenuButton(text = "Terms and Condictions", color = Color.Black, textColor = Color.White)
            }
        }
    }
}

// --- UI COMPONENTI RIUTILIZZABILI ---

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