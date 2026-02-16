package com.example.upics

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.upics.ui.theme.*

// --- OGGETTO PONTE (SALVA I DATI ANCHE SE RUOTI) --- //
object TransferState {
    var lastEditState: PhotoEditState = PhotoEditState()

    // 1. IL PORTAFOGLIO: Ora è un numero (Int) così puoi accumulare crediti!
    var credits: Int = 0

    // 2. LA GALLERIA: Qui l'app si ricorderà le foto create in questa sessione
    var savedPhotos: List<String> = emptyList()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Launcher per la Galleria (usato dalla Home)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            TransferState.lastEditState = PhotoEditState()
            val encodedUri = Uri.encode(uri.toString())
            navController.navigate("magic_mode/$encodedUri")
        }
    }

    // --- START DESTINATION ORA È "splash" ---
    NavHost(navController = navController, startDestination = "splash") {

        // 0. SPLASH SCREEN (Animazione Iniziale)
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // 1. AUTH SCREEN (Login/Registrazione)
        composable("auth") {
            AuthScreen(navController)
        }

        // 2. HOME SCREEN
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

        // 3. MAGIC MODE (EDITOR)
        composable(
            route = "magic_mode/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("photoUri") ?: ""
            val uri = Uri.parse(Uri.decode(uriString))

            MagicModeScreen(
                navController = navController,
                photoUri = uri,
                onSaveMoves = { finalState ->
                    TransferState.lastEditState = finalState
                    val encodedUri = Uri.encode(uri.toString())
                    navController.navigate("resume/$encodedUri")
                }
            )
        }

        // 4. RESUME SCREEN (RIEPILOGO & PAGAMENTO)
        composable(
            route = "resume/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("photoUri") ?: ""
            val uri = Uri.parse(Uri.decode(uriString))

            ResumeScreen(
                navController = navController,
                photoUri = uri,
                editState = TransferState.lastEditState
            )
        }

        // 5. AUDIO CONNECT SCREEN
        composable(
            route = "audio_connect/{photoUri}/{pinCode}",
            arguments = listOf(
                navArgument("photoUri") { type = NavType.StringType },
                navArgument("pinCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoUriString = backStackEntry.arguments?.getString("photoUri")
            val pinCodeString = backStackEntry.arguments?.getString("pinCode")

            AudioConnectScreen(
                navController = navController,
                encodedUri = photoUriString,
                pinCode = pinCodeString // Passa il PIN come stringa
            )
        }

        // 6. PRINTING SCREEN (STAMPA)
        composable(
            route = "printing/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("photoUri") ?: ""
            val uri = Uri.parse(Uri.decode(uriString))

            PrintingScreen(
                navController = navController,
                photoUri = uri,
                editState = TransferState.lastEditState
            )
        }

        // 7. SUCCESS SCREEN
        composable("print_success") {
            PrintSuccessScreen(navController = navController)
        }

        // 8. ALTRE SCHERMATE DI SERVIZIO
        composable("history") {
            HistoryScreen(navController = navController)
        }

        composable("terms") {
            TermsScreen(navController = navController)
        }

        composable("about") {
            AboutScreen(navController = navController)
        }

        composable("help") {
            HelpScreen(navController = navController)
        }
    }
}