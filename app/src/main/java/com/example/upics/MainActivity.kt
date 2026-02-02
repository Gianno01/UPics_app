package com.example.upics

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // --- STATO CONDIVISO ---
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var finalEditState by remember { mutableStateOf(PhotoEditState()) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri
            finalEditState = PhotoEditState()
            navController.navigate("preview")
        }
    }

    NavHost(navController = navController, startDestination = "login") {

        // ... (Login, Auth, Home, Preview, MagicMode restano uguali) ...
        composable("login") { LoginScreen(navController) }
        composable("auth") { AuthScreen(navController) }
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
            PreviewScreen(
                navController = navController,
                photoUri = selectedPhotoUri,
                onEditClick = {
                    if (selectedPhotoUri != null) {
                        val encodedUri = Uri.encode(selectedPhotoUri.toString())
                        navController.navigate("magic_mode/$encodedUri")
                    }
                },
                onPrintClick = {
                    finalEditState = PhotoEditState() // Reset o Default
                    navController.navigate("resume")
                }
            )
        }
        composable(
            route = "magic_mode/{photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("photoUri")
            if (uriString != null) {
                val uri = Uri.parse(uriString)
                MagicModeScreen(
                    navController = navController,
                    photoUri = uri,
                    onSaveMoves = { newState ->
                        finalEditState = newState
                        navController.navigate("resume")
                    }
                )
            }
        }

        // --- MODIFICHE QUI SOTTO ---

        // 1. RESUME SCREEN (Conferma Pagamento)
        composable("resume") {
            if (selectedPhotoUri != null) {
                ResumeScreen(
                    navController = navController,
                    photoUri = selectedPhotoUri!!,
                    editState = finalEditState
                )
            }
        }

        // 2. PRINTING SCREEN (Stampa in corso)
        composable("printing") {
            if (selectedPhotoUri != null) {
                PrintingScreen(
                    navController = navController,
                    photoUri = selectedPhotoUri!!,
                    editState = finalEditState
                )
            }
        }

        // 3. SUCCESS SCREEN (Stampa completata)
        composable("print_success") {
            PrintSuccessScreen(navController = navController)
        }
    }
}