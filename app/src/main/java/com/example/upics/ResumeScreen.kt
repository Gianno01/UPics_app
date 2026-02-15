package com.example.upics

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
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
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

@Composable
fun ResumeScreen(
    navController: NavController,
    photoUri: Uri,
    editState: PhotoEditState
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var quantity by remember { mutableIntStateOf(1) }
    var termsAccepted by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    // Multi-Crediti
    var creditCount by remember { mutableIntStateOf(TransferState.credits) }
    val hasCredit = creditCount > 0

    var actionAfterExport by remember { mutableStateOf("") }

    val pricePerPhoto = 1.00
    val totalPrice = quantity * pricePerPhoto

    BackHandler { navController.popBackStack() }

    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = { CircularProgressIndicator(color = Color(0xFF8BC34A)) },
            title = { Text("Buying Print Credit") },
            text = { Text("Adding 1 print credit to your wallet...") },
            confirmButton = {},
            containerColor = Color.White
        )

        LaunchedEffect(Unit) {
            delay(2000)
            showPaymentDialog = false
            TransferState.credits += 1
            creditCount = TransferState.credits
            Toast.makeText(context, "Credit Added! You have $creditCount credits.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    HeroPolaroidCapturable(photoUri, editState, actionAfterExport) { bmp ->
                        val savedUri = saveJpegToGallery(context, bmp)
                        if (savedUri != null) {
                            // MAGIA: Salviamo il link della foto nella Galleria dell'App!
                            if (!TransferState.savedPhotos.contains(savedUri.toString())) {
                                TransferState.savedPhotos = listOf(savedUri.toString()) + TransferState.savedPhotos
                            }
                            if (actionAfterExport == "SAVE") {
                                Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                            } else if (actionAfterExport == "PRINT") {
                                TransferState.credits -= 1
                                creditCount = TransferState.credits
                                navController.navigate("audio_connect/${Uri.encode(savedUri.toString())}")
                            }
                        }
                        actionAfterExport = ""
                    }
                }
                Surface(modifier = Modifier.width(360.dp).fillMaxHeight(), color = Color.White, shadowElevation = 16.dp) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                        ResumeContent(quantity, totalPrice, termsAccepted, showValidationError, hasCredit,
                            { quantity = it }, { termsAccepted = it; if (it) showValidationError = false },
                            { navController.popBackStack() }, { actionAfterExport = "SAVE" },
                            { if (termsAccepted) showPaymentDialog = true else showValidationError = true },
                            { if (termsAccepted) actionAfterExport = "PRINT" else showValidationError = true })
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                CommonHeader()
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {
                    HeroPolaroidCapturable(photoUri, editState, actionAfterExport) { bmp ->
                        val savedUri = saveJpegToGallery(context, bmp)
                        if (savedUri != null) {
                            // MAGIA: Salviamo il link della foto nella Galleria dell'App!
                            if (!TransferState.savedPhotos.contains(savedUri.toString())) {
                                TransferState.savedPhotos = listOf(savedUri.toString()) + TransferState.savedPhotos
                            }
                            if (actionAfterExport == "SAVE") {
                                Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                            } else if (actionAfterExport == "PRINT") {
                                TransferState.credits -= 1
                                creditCount = TransferState.credits
                                navController.navigate("audio_connect/${Uri.encode(savedUri.toString())}")
                            }
                        }
                        actionAfterExport = ""
                    }
                }
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = Color.White, shadowElevation = 16.dp) {
                    Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                        ResumeContent(quantity, totalPrice, termsAccepted, showValidationError, hasCredit,
                            { quantity = it }, { termsAccepted = it; if (it) showValidationError = false },
                            { navController.popBackStack() }, { actionAfterExport = "SAVE" },
                            { if (termsAccepted) showPaymentDialog = true else showValidationError = true },
                            { if (termsAccepted) actionAfterExport = "PRINT" else showValidationError = true })
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.ResumeContent(
    qty: Int, price: Double, terms: Boolean, error: Boolean, hasCredit: Boolean,
    onQty: (Int) -> Unit, onTerms: (Boolean) -> Unit, onBack: () -> Unit,
    onSaveToGallery: () -> Unit, onBuyCredit: () -> Unit, onPrint: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val spacing = if (isLandscape) 12.dp else 24.dp

    Text("Order Summary", fontSize = if (isLandscape) 18.sp else 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = spacing))
    QuantitySelector(qty, onQty)
    Spacer(Modifier.height(spacing))
    Text("Total: € ${String.format("%.2f", price)}", fontSize = if (isLandscape) 24.sp else 32.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(Modifier.height(spacing))
    TermsCheckbox(terms, onTerms, error)

    if (isLandscape) Spacer(Modifier.weight(1f)) else Spacer(Modifier.height(32.dp))

    OutlinedButton(
        onClick = onSaveToGallery,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Text("Save to Gallery (Free)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }

    Spacer(Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = onBack, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.LightGray), modifier = Modifier.size(56.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
        }
        if (hasCredit) {
            Button(onClick = onPrint, shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A), contentColor = Color.White), modifier = Modifier.weight(1f).height(56.dp)) {
                Icon(Icons.Default.Print, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Print", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(onClick = onBuyCredit, shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White), modifier = Modifier.weight(1f).height(56.dp)) {
                Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buy Credit", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
fun HeroPolaroidCapturable(photoUri: Uri, editState: PhotoEditState, actionAfterExport: String, onExported: (Bitmap) -> Unit) {
    val graphicsLayer = rememberGraphicsLayer()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Box(
        modifier = Modifier.then(if (isLandscape) Modifier.fillMaxHeight(0.9f) else Modifier.fillMaxWidth()).aspectRatio(0.85f).rotate(0f).drawWithContent {
            graphicsLayer.record { this@drawWithContent.drawContent() }
            drawLayer(graphicsLayer)
        },
        contentAlignment = Alignment.Center
    ) { Surface(color = Color.White, shape = RoundedCornerShape(2.dp)) { PolaroidFinalPreviewNoShadow(photoUri, editState) } }

    LaunchedEffect(actionAfterExport) {
        if (actionAfterExport.isNotEmpty()) {
            val hardwareBmp = graphicsLayer.toImageBitmap().asAndroidBitmap()
            onExported(hardwareBmp)
        }
    }
}

private fun Bitmap.toGrayscale(): Bitmap {
    val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(grayscaleBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return grayscaleBitmap
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
            Checkbox(checked = checked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = Color.Black))
            Text("I agree to Terms & Conditions", fontSize = 14.sp, color = Color.Gray)
        }
        if (showError) Text("Please accept terms to buy/print", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
fun PolaroidFinalPreviewNoShadow(photoUri: Uri, editState: PhotoEditState) {
    val context = LocalContext.current
    val matrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix
    Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 28.dp)) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(Color(0xFFEEEEEE)).clip(RectangleShape)) {
            Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(photoUri).build()), contentDescription = null, contentScale = ContentScale.Crop, colorFilter = if (matrix != null) ColorFilter.colorMatrix(matrix) else null, modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = editState.scaleX * editState.zoom, scaleY = editState.scaleY * editState.zoom, rotationZ = editState.rotation, translationX = editState.panX, translationY = editState.panY))
            editState.stickers.forEach { sticker -> Box(modifier = Modifier.offset { IntOffset(sticker.offsetX.roundToInt(), sticker.offsetY.roundToInt()) }.graphicsLayer(scaleX = sticker.scale, scaleY = sticker.scale)) { Text(text = sticker.emoji, fontSize = 40.sp) } }
        }
        Box(modifier = Modifier.fillMaxWidth().height(46.dp), contentAlignment = Alignment.Center) {
            val caption = editState.caption.orEmpty()
            Text(text = if (caption.isBlank()) "" else caption, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Cursive, color = Color.Black, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}