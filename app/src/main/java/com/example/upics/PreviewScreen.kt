package com.example.upics

import android.net.Uri // Importante per gestire le foto vere
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

// Modifica: Ora la funzione accetta una lista di Uri (foto vere)
@Composable
fun PreviewScreen(navController: NavController, photos: List<Uri>) {
    val context = LocalContext.current

    // Se la lista è vuota, torniamo indietro per evitare errori
    if (photos.isEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Nessuna foto selezionata", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    // Stato: Foto selezionata (di default la prima della lista)
    var selectedPhoto by remember { mutableStateOf(photos[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CommonHeader() // Usa l'header definito in precedenza o copialo qui se serve

        Spacer(modifier = Modifier.height(8.dp))

        // Barra Navigazione
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(48.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Text(text = "Select", fontSize = 20.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ANTEPRIMA POLAROID GRANDE
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .border(1.dp, Color.LightGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context).data(selectedPhoto).build()
                        ),
                        contentDescription = "Selected Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // LISTA MINIATURE
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(photos) { photo ->
                val isSelected = (photo == selectedPhoto)
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { selectedPhoto = photo }
                        .then(
                            if (isSelected) Modifier.border(3.dp, Color(0xFF4285F4), RoundedCornerShape(8.dp))
                            else Modifier
                        )
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = photo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PULSANTI AZIONE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { /* Filtri */ },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Filter", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { Toast.makeText(context, "Printing...", Toast.LENGTH_SHORT).show() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A), contentColor = Color.White),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(28.dp))
            }
        }
    }
}