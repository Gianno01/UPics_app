package com.example.upics

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun CommonHeader() {
    val context = LocalContext.current

    // Configurazione Loader per supportare SVG (per il logo)
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
            add(SvgDecoder.Factory())
        }
        .build()

    // Caricamento Logo
    val logoPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(R.raw.logo).build(),
        imageLoader = imageLoader
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Image(
            painter = logoPainter,
            contentDescription = "Logo Upics",
            modifier = Modifier.size(60.dp)
        )

        // Stato Connessione
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.Green)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Nota: Qui uso il colore nero o grigio scuro perché CommonHeader
            // viene usato spesso su sfondo bianco (es. Preview).
            // Se lo usi su sfondo scuro, potresti dover passare il colore come parametro.
            Text(
                text = "Connected - Turin(IT)",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}