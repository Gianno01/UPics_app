package com.example.upics

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ResumeScreen(
    navController: NavController,
    photoUri: Uri,
    editState: PhotoEditState
) {
    val context = LocalContext.current

    var quantity by remember { mutableIntStateOf(1) }
    var termsAccepted by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf(false) }

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    val pricePerPhoto = 1.00
    val totalPrice = quantity * pricePerPhoto

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    BackHandler { navController.popBackStack() }

    // --- DIALOG DI PAGAMENTO (Ora aggiunge solo il credito) ---
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = { CircularProgressIndicator(color = Color(0xFF8BC34A)) },
            title = { Text("Buying Print Credit") },
            text = { Text("Adding credits to your wallet...") },
            confirmButton = {},
            containerColor = Color.White
        )
        LaunchedEffect(Unit) {
            delay(2000) // Simula pagamento
            showPaymentDialog = false

            // 1. SALVIAMO IL CREDITO NELLO STATO GLOBALE
            TransferState.hasCredit = true

            Toast.makeText(context, "Credit Added! You can print anytime from Home.", Toast.LENGTH_LONG).show()

            // 2. TORNIAMO ALLA HOME (Invece di andare all'audio)
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    // --- DIALOG DI SALVATAGGIO (Galleria) ---
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = { CircularProgressIndicator(color = Color.Black) },
            title = { Text("Saving to Gallery") },
            confirmButton = {},
            containerColor = Color.White
        )
        LaunchedEffect(Unit) {
            delay(1500)
            showSaveDialog = false
            Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
            navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA))
    ) {
        if (isLandscape) {
            // Layout Landscape (omesso per brevità, usa la stessa logica del Portrait)
            Row(modifier = Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) { HeroPolaroid(photoUri, editState) }
                Surface(Modifier.width(420.dp).fillMaxHeight(), color = Color.White, shadowElevation = 16.dp) {
                    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
                        ResumeContent(quantity, totalPrice, termsAccepted, showValidationError,
                            { quantity = it }, { termsAccepted = it; if(it) showValidationError=false },
                            { navController.popBackStack() },
                            { if(termsAccepted) showPaymentDialog=true else showValidationError=true },
                            { showSaveDialog=true }
                        )
                    }
                }
            }
        } else {
            // Layout Portrait
            Column(modifier = Modifier.fillMaxSize()) {
                CommonHeader()
                Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {
                    HeroPolaroid(photoUri, editState)
                }

                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    QuantitySelector(quantity) { quantity = it }
                    Spacer(Modifier.height(16.dp))
                    Text("Total: € ${String.format("%.2f", totalPrice)}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(16.dp))

                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = Color.White, shadowElevation = 16.dp) {
                    Column(Modifier.padding(24.dp).navigationBarsPadding()) {
                        TermsCheckbox(termsAccepted, { termsAccepted = it; if(it) showValidationError=false }, showValidationError)
                        Spacer(Modifier.height(16.dp))

                        // Passiamo le azioni
                        ActionButtons(
                            onBack = { navController.popBackStack() },
                            onPay = {
                                if (termsAccepted) showPaymentDialog = true
                                else showValidationError = true
                            },
                            onSave = { showSaveDialog = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResumeContent(qty: Int, price: Double, terms: Boolean, error: Boolean, onQty: (Int)->Unit, onTerms: (Boolean)->Unit, onBack: ()->Unit, onPay: ()->Unit, onSave: ()->Unit) {
    Text("Order Summary", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))
    QuantitySelector(qty, onQty)
    Spacer(Modifier.height(24.dp))
    Text("Total: € ${String.format("%.2f", price)}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(24.dp))
    TermsCheckbox(terms, onTerms, error)
    Spacer(Modifier.height(32.dp))
    ActionButtons(onBack, onPay, onSave)
}

@Composable
fun ActionButtons(onBack: () -> Unit, onPay: () -> Unit, onSave: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Tasto Acquisto
        Button(
            onClick = onPay,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            // TESTO CAMBIATO:
            Text("Buy Print Credit (Save for later)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedIconButton(onClick = onBack, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.LightGray), modifier = Modifier.size(56.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            OutlinedButton(
                onClick = onSave,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to Gallery", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Componenti HeroPolaroid, QuantitySelector, TermsCheckbox, PolaroidFinalPreview sono identici a prima (omessi per brevità, mantieni quelli che hai).
@Composable
fun HeroPolaroid(photoUri: Uri, editState: PhotoEditState) {
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.85f).rotate(-2f).shadow(20.dp, RoundedCornerShape(2.dp))) {
        PolaroidFinalPreview(photoUri, editState)
    }
}
@Composable
fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Surface(shape = RoundedCornerShape(50), color = Color.White, border = BorderStroke(1.dp, Color(0xFFE0E0E0)), modifier = Modifier.height(56.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
            IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) { Icon(Icons.Default.Remove, "Decrease", tint = Color.Gray) }
            Text("$quantity Copy", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            IconButton(onClick = { if (quantity < 10) onQuantityChange(quantity + 1) }) { Icon(Icons.Default.Add, "Increase", tint = Color.Black) }
        }
    }
}
@Composable
fun TermsCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, showError: Boolean) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked, onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = Color.Black))
            Text("I agree to Terms & Conditions", fontSize = 14.sp, color = Color.Gray)
        }
        if (showError) Text("Please accept terms", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
    }
}
@Composable
fun PolaroidFinalPreview(photoUri: Uri, editState: PhotoEditState) {
    val context = LocalContext.current
    val matrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix
    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(Color(0xFFEEEEEE)).clip(RectangleShape)) {
                Image(rememberAsyncImagePainter(ImageRequest.Builder(context).data(photoUri).build()), null, contentScale = ContentScale.Crop, colorFilter = if (matrix != null) ColorFilter.colorMatrix(matrix) else null, modifier = Modifier.fillMaxSize().graphicsLayer {
                    scaleX = editState.scaleX * editState.zoom; scaleY = editState.scaleY * editState.zoom
                    rotationZ = editState.rotation; translationX = editState.panX; translationY = editState.panY
                })
                editState.stickers.forEach { sticker ->
                    Box(modifier = Modifier.offset { IntOffset(sticker.offsetX.roundToInt(), sticker.offsetY.roundToInt()) }.graphicsLayer(scaleX = sticker.scale, scaleY = sticker.scale)) { Text(sticker.emoji, fontSize = 40.sp) }
                }
            }
        }
    }
}