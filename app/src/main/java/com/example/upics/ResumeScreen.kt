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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlin.math.roundToInt

@Composable
fun ResumeScreen(
    navController: NavController,
    photoUri: Uri,
    editState: PhotoEditState // Riceve lo stato con sticker, testo e filtri
) {
    val context = LocalContext.current

    // --- DEFINIZIONE STATI (Qui mancava showValidationError) ---
    var copies by remember { mutableIntStateOf(1) }
    var termsAccepted by remember { mutableStateOf(false) }
    // Variabile per gestire l'errore se non si accettano i termini
    var showValidationError by remember { mutableStateOf(false) }

    val matrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        // Header con Tasto Indietro
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { navController.popBackStack() }, shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp), modifier = Modifier.size(48.dp), border = BorderStroke(1.dp, Color.Black), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Print", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.weight(1f).padding(16.dp)) {

            // --- CARD RIEPILOGO ---
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(220.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // LATO SX: FOTO MODIFICATA
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxHeight(0.9f)
                                .aspectRatio(0.80f)
                                .border(1.dp, Color.LightGray)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .clipToBounds()
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(photoUri).build()),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        colorFilter = if (matrix != null) ColorFilter.colorMatrix(matrix) else null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                rotationZ = editState.rotation
                                                scaleX = editState.scaleX * editState.zoom
                                                scaleY = editState.scaleY * editState.zoom
                                            }
                                    )

                                    editState.stickers.forEach { sticker ->
                                        Box(
                                            modifier = Modifier
                                                .offset { IntOffset(sticker.offsetX.roundToInt(), sticker.offsetY.roundToInt()) }
                                                .graphicsLayer(scaleX = sticker.scale, scaleY = sticker.scale)
                                        ) {
                                            Text(text = sticker.emoji, fontSize = 40.sp)
                                        }
                                    }
                                }

                                if (editState.caption.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = editState.caption,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // LATO DX: PREZZO
                    Column(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                            .background(Color(0xFFE0E0E0))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Order Summary", fontWeight = FontWeight.Bold)
                        Text("1 photo x $copies", fontSize = 14.sp)
                        Text("Total: ${String.format("%.2f", copies * 1.0)}€", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.align(Alignment.End))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONTATORE COPIE
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (copies > 1) copies-- }, modifier = Modifier.background(Color.LightGray)) { Icon(Icons.Default.Remove, null) }
                Text("$copies", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
                IconButton(onClick = { copies++ }, modifier = Modifier.background(Color.LightGray)) { Icon(Icons.Default.Add, null) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CHECKBOX TERMINI
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = {
                        termsAccepted = it
                        if(it) showValidationError = false // Resetta errore se accetta
                    }
                )
                Text("I agree to Terms & Conditions", fontSize = 12.sp)
            }

            // MESSAGGIO ERRORE ROSSO
            if (showValidationError) {
                Text(
                    text = "Please accept terms to continue.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // TASTO PAGA (Naviga a Printing se ok)
        Button(
            onClick = {
                if (termsAccepted) {
                    navController.navigate("printing")
                } else {
                    showValidationError = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Pay & Print")
        }
    }
}