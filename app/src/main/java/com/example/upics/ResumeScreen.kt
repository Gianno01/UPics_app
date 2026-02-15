package com.example.upics

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.firebase.Firebase
import com.google.firebase.database.database
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

    // Trigger: quando premi PRINT, esportiamo e salviamo in galleria
    var exportRequest by remember { mutableStateOf(false) }

    val pricePerPhoto = 1.00
    val totalPrice = quantity * pricePerPhoto

    BackHandler { navController.popBackStack() }

    // Dialog finto pagamento (come nel tuo file originale) [file:5]
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
            delay(2000)
            showPaymentDialog = false
            TransferState.hasCredit = true
            Toast.makeText(
                context,
                "Credit Added! You can print anytime from Home.",
                Toast.LENGTH_LONG
            ).show()
            navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFAFAFA))) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    HeroPolaroidCapturable(
                        photoUri = photoUri,
                        editState = editState,
                        exportRequest = exportRequest,
                        onExported = { bmp ->
                            val savedUri = saveJpegToGallery(context, bmp)
                            if (savedUri != null) {
                                Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()

                                val encoded = Uri.encode(savedUri.toString())
                                navController.navigate("printing$encoded")
                            } else {
                                Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                            }
                            exportRequest = false

                        }
                    )
                }

                Surface(
                    modifier = Modifier
                        .width(420.dp)
                        .fillMaxHeight(),
                    color = Color.White,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        ResumeContent(
                            qty = quantity,
                            price = totalPrice,
                            terms = termsAccepted,
                            error = showValidationError,
                            onQty = { quantity = it },
                            onTerms = {
                                termsAccepted = it
                                if (it) showValidationError = false
                            },
                            onBack = { navController.popBackStack() },
                            onPay = {
                                if (termsAccepted) showPaymentDialog = true
                                else showValidationError = true
                            },
                            onPrint = { if (termsAccepted) exportRequest = true
                            else showValidationError = true } // PRINT = salva
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Se CommonHeader() non esiste nel tuo progetto, commenta questa riga [file:5]
                CommonHeader()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HeroPolaroidCapturable(
                        photoUri = photoUri,
                        editState = editState,
                        exportRequest = exportRequest,
                        onExported = { bmp ->
                            val savedUri = saveJpegToGallery(context, bmp)
                            if (savedUri != null) {
                                Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                                // Qui puoi proseguire col tuo flusso di stampa usando savedUri, se vuoi.
                            } else {
                                Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                            }
                            exportRequest = false
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuantitySelector(quantity) { quantity = it }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Total: € ${String.format("%.2f", totalPrice)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Color.White,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .navigationBarsPadding()
                    ) {
                        TermsCheckbox(
                            checked = termsAccepted,
                            onCheckedChange = {
                                termsAccepted = it
                                if (it) showValidationError = false
                            },
                            showError = showValidationError
                        )
                        Spacer(Modifier.height(16.dp))

                        PrintButtons(
                            onBack = { navController.popBackStack() },
                            onPrint = { if (termsAccepted) exportRequest = true
                            else showValidationError = true },
                            isEnabled = termsAccepted
                        )

                        Spacer(Modifier.height(10.dp))
                        // Se vuoi tenere anche "Buy credit" puoi riaggiungerlo; qui l'ho lasciato minimal.
                    }
                }
            }
        }
    }
}

@Composable
fun ResumeContent(
    qty: Int,
    price: Double,
    terms: Boolean,
    error: Boolean,
    onQty: (Int) -> Unit,
    onTerms: (Boolean) -> Unit,
    onBack: () -> Unit,
    onPay: () -> Unit,
    onPrint: () -> Unit
) {
    Text(
        "Order Summary",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    QuantitySelector(qty, onQty)
    Spacer(Modifier.height(24.dp))

    Text(
        "Total: € ${String.format("%.2f", price)}",
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold
    )

    Spacer(Modifier.height(24.dp))
    TermsCheckbox(terms, onTerms, error)
    Spacer(Modifier.height(32.dp))

    // Se vuoi mantenere anche il tasto credito:
    Button(
        onClick = onPay,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text("Buy Print Credit (Save for later)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(Modifier.height(12.dp))

    PrintButtons(
        onBack = onBack,
        onPrint = onPrint,
        isEnabled = terms
    )
}

@Composable
fun PrintButtons(
    onBack: () -> Unit,
    onPrint: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedIconButton(
            onClick = onBack,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }

        Button(
            onClick = onPrint,
            enabled = isEnabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8BC34A),
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray
            ),
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        ) {
            Icon(Icons.Default.Print, null, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Print", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HeroPolaroidCapturable(
    photoUri: Uri,
    editState: PhotoEditState,
    exportRequest: Boolean,
    onExported: (Bitmap) -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()

    // Niente ombre: solo formato polaroid; qui catturiamo TUTTO quello che viene disegnato [web:10]
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .rotate(0f) // metti -2f se vuoi anche nel JPG
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(2.dp)
        ) {
            PolaroidFinalPreviewNoShadow(photoUri, editState)
        }
    }

    LaunchedEffect(exportRequest) {
        if (exportRequest) {
            val hardwareBmp = graphicsLayer.toImageBitmap().asAndroidBitmap()
            onExported(hardwareBmp)

            val softwareBmp = hardwareBmp.copy(Bitmap.Config.ARGB_8888, true)

            val grayscaleBmp = softwareBmp.toGrayscale()

            val byteArrayOutputStream = ByteArrayOutputStream()
            grayscaleBmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val encoded: String? = Base64.encodeToString(byteArray, Base64.DEFAULT)

            //Firebase.database.getReference("VendingMachines/VM001/image/data").setValue(encoded)
            //Firebase.database.getReference("VendingMachines/VM001/image/height").setValue(softwareBmp.height)
        }
    }
}
private fun Bitmap.toGrayscale(): Bitmap {
    // Crea un nuovo bitmap mutabile per disegnarci sopra
    val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(grayscaleBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix().apply {
        setSaturation(0f) // Imposta la saturazione a 0 per la scala di grigi
    }
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    // Disegna il bitmap originale sul nuovo canvas con il filtro applicato
    canvas.drawBitmap(this, 0f, 0f, paint)
    return grayscaleBitmap
}
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier.height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
                Icon(Icons.Default.Remove, "Decrease", tint = Color.Gray)
            }
            Text(
                "$quantity Copy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(onClick = { if (quantity < 10) onQuantityChange(quantity + 1) }) {
                Icon(Icons.Default.Add, "Increase", tint = Color.Black)
            }
        }
    }
}

@Composable
fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showError: Boolean
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color.Black)
            )
            Text("I agree to Terms & Conditions", fontSize = 14.sp, color = Color.Gray)
        }

        if (showError) {
            Text(
                "Please accept terms",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
fun PolaroidFinalPreviewNoShadow(
    photoUri: Uri,
    editState: PhotoEditState
) {
    val context = LocalContext.current
    val matrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix

    // Cornice polaroid: padding maggiore sotto, nessuna ombra [file:5]
    Column(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 28.dp)
    ) {
        // Foto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFEEEEEE))
                .clip(RectangleShape)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context).data(photoUri).build()
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = if (matrix != null) ColorFilter.colorMatrix(matrix) else null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = editState.scaleX * editState.zoom,
                        scaleY = editState.scaleY * editState.zoom,
                        rotationZ = editState.rotation,
                        translationX = editState.panX,
                        translationY = editState.panY
                    )
            )

            // Stickers [file:5]
            editState.stickers.forEach { sticker ->
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                sticker.offsetX.roundToInt(),
                                sticker.offsetY.roundToInt()
                            )
                        }
                        .graphicsLayer(scaleX = sticker.scale, scaleY = sticker.scale)
                ) {
                    Text(text = sticker.emoji, fontSize = 40.sp)
                }
            }
        }

        // Spazio bianco polaroid + TESTO (caption) [file:2]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            contentAlignment = Alignment.Center
        ) {
            val caption = editState.caption.orEmpty()
            Text(
                text = if (caption.isBlank()) "" else caption,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
