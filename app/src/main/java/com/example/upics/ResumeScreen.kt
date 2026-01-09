package com.example.upics

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun ResumeScreen(
    navController: NavController,
    photos: List<Uri>,
    filterMap: Map<Uri, String>
) {
    val context = LocalContext.current

    // --- STATI ---
    var copiesPerPhoto by remember { mutableIntStateOf(1) }
    var termsAccepted by remember { mutableStateOf(false) }
    var newsletterAccepted by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf(false) }

    // --- CALCOLI ---
    val pricePerPhoto = 1.00
    val totalPhotos = photos.size
    val totalCopies = totalPhotos * copiesPerPhoto
    val totalPrice = totalCopies * pricePerPhoto

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. HEADER
        ResumeHeader(onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            // 2. CARD RIEPILOGO (Stile Immagine Allegata)
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // LATO SINISTRO: Collage Foto
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Mostra una pila di foto (fino a 3)
                        photos.take(3).forEachIndexed { index, uri ->
                            val filterName = filterMap[uri] ?: "Normal"
                            val matrix = FilterUtils.filters.find { it.name == filterName }?.colorMatrix

                            // Ruotiamo leggermente le foto per effetto "pila"
                            val rotation = when(index) {
                                0 -> -10f
                                1 -> 5f
                                else -> 0f
                            }

                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                colorFilter = if (matrix != null) ColorFilter.colorMatrix(matrix) else null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .rotate(rotation)
                                    .border(1.dp, Color.White)

                            )
                        }
                    }

                    // LATO DESTRO: Testi e Prezzi (Sfondo Grigio)
                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                            .background(Color(0xFFE0E0E0)) // Grigio chiaro
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Resume", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${copiesPerPhoto}x.................... photos ${String.format("%.2f", pricePerPhoto)}€",
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }

                        Text(
                            text = "tot ${String.format("%.2f", totalPrice)}€",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. SELETTORE COPIE
            Text("Copies per photo:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { if (copiesPerPhoto > 1) copiesPerPhoto-- },
                    modifier = Modifier.background(Color.LightGray).size(36.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.Black)
                }

                Text(
                    text = "$copiesPerPhoto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                IconButton(
                    onClick = { copiesPerPhoto++ },
                    modifier = Modifier.background(Color.LightGray).size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. CHECKBOXES
            // Termini e Condizioni
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = {
                        termsAccepted = it
                        if(it) showValidationError = false
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Black)
                )
                Text(
                    buildAnnotatedString {
                        append("I ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("agree")
                        }
                        append(" to Terms and Conditions and Privacy Policy*")
                    },
                    fontSize = 12.sp,
                    color = if (showValidationError) Color.Red else Color.Black
                )
            }

            // Newsletter
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = newsletterAccepted,
                    onCheckedChange = { newsletterAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Black)
                )
                Text(
                    text = "Send me information about new products and service.",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }

            // Messaggio Errore Rosso
            if (showValidationError) {
                Text(
                    text = "Please Read the licensing terms and check the box to accept them.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // 5. BOTTONE PAY & PRINT
        Button(
            onClick = {
                if (!termsAccepted) {
                    showValidationError = true
                } else {
                    // Qui implementeremo la schermata di pagamento
                    Toast.makeText(context, "Proceeding to Payment...", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8BC34A), // Verde
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
        ) {
            Text("Pay & Print", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Componente Header Semplice
@Composable
fun ResumeHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tasto Indietro Quadrato
        OutlinedButton(
            onClick = onBackClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(48.dp),
            border = BorderStroke(1.dp, Color.Black),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Linea verticale decorativa e Testo
        Row(modifier = Modifier.height(40.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.Black))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Print", fontSize = 24.sp, color = Color.Black)
        }
    }
}



