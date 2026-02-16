package com.example.upics

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    // Caricatore speciale di Coil per supportare le GIF animate
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
        }
        .build()

    // --- IL TIMER DELLA MAGIA ---
    LaunchedEffect(key1 = true) {
        // La durata della schermata iniziale.
        // 3000 = 3 secondi. Aumenta o diminuisci a seconda di quanto dura la tua GIF!
        delay(3000)

        // Passato il tempo, andiamo alla schermata di Login (AuthScreen)
        navController.navigate("auth") {
            // Rimuoviamo la splash screen dalla memoria per impedire che l'utente ci torni cliccando "indietro"
            popUpTo("splash") { inclusive = true }
        }
    }

    // --- IL LAYOUT ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Se la tua gif ha uno sfondo nero, cambia in Color.Black!
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(R.raw.intro) // <--- CARICA ESATTAMENTE LA TUA intro.gif
                    .build(),
                imageLoader = imageLoader
            ),
            contentDescription = "Upics Intro Animation",
            modifier = Modifier.size(300.dp) // Modifica questo valore per farla più grande o più piccola
        )
    }
}