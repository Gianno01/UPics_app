package com.example.upics

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlin.math.roundToInt

@Composable
fun MagicModeScreen(
    navController: NavController,
    photoUri: Uri,
    onSaveMoves: (PhotoEditState) -> Unit
) {
    // Stati
    var editState by remember { mutableStateOf(PhotoEditState()) }
    var activeTool by remember { mutableStateOf(EditorTool.NONE) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Rileva orientamento
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Intercetta il tasto "Indietro" fisico
    BackHandler { showExitDialog = true }

    // Dialog conferma uscita
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("If you go back now, you will lose your magic edits.") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Keep Editing") }
            }
        )
    }

    // --- LOGICA DI LAYOUT ADATTIVO ---
    if (isLandscape) {
        // === LANDSCAPE (ORIZZONTALE) ===
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
        ) {
            // 1. SINISTRA: Area Foto (Massimizzata)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Foto quadrata, ma che non esce dallo schermo in altezza
                Box(modifier = Modifier.fillMaxHeight(0.9f).aspectRatio(1f)) {
                    PolaroidEditorView(photoUri, editState) { editState = it }
                }
            }

            // 2. DESTRA: Barra Strumenti Laterale
            Surface(
                modifier = Modifier
                    .width(360.dp)
                    .fillMaxHeight(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // A. Intestazione + Area Opzioni (Scrollabile)
                    // Questa parte prende tutto lo spazio in alto
                    Column(
                        modifier = Modifier
                            .weight(1f) // Spinge i controlli in basso
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text("Magic Tools ✨", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

                        // Qui appaiono gli Slider, le Emoji, i Filtri quando selezioni uno strumento
                        AnimatedVisibility(visible = activeTool != EditorTool.NONE) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                ActiveToolOptions(
                                    activeTool = activeTool,
                                    editState = editState,
                                    photoUri = photoUri,
                                    onStateChange = { editState = it }
                                )
                            }
                        }
                    }

                    Divider(color = Color(0xFFEEEEEE))

                    // B. PLANCIA DI COMANDO (Fissa in Basso)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // 1. Le 4 Icone degli Strumenti (Sopra i tasti azione)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EditorToolButton(Icons.Default.AutoAwesome, "Filters", activeTool == EditorTool.FILTER) {
                                activeTool = if (activeTool == EditorTool.FILTER) EditorTool.NONE else EditorTool.FILTER
                            }
                            EditorToolButton(Icons.Default.TextFields, "Text", activeTool == EditorTool.TEXT) {
                                activeTool = if (activeTool == EditorTool.TEXT) EditorTool.NONE else EditorTool.TEXT
                            }
                            EditorToolButton(Icons.Default.EmojiEmotions, "Stickers", activeTool == EditorTool.EMOJI) {
                                activeTool = if (activeTool == EditorTool.EMOJI) EditorTool.NONE else EditorTool.EMOJI
                            }
                            EditorToolButton(Icons.Default.CropRotate, "Transform", activeTool == EditorTool.TRANSFORM) {
                                activeTool = if (activeTool == EditorTool.TRANSFORM) EditorTool.NONE else EditorTool.TRANSFORM
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. I Tasti Azione (Freccia e Salva)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tasto Indietro (Sinistra)
                            OutlinedIconButton(
                                onClick = { showExitDialog = true },
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.LightGray),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }

                            // Tasto Salva (Destra - Grande)
                            Button(
                                onClick = { onSaveMoves(editState) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8BC34A),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    } else {
        // === PORTRAIT (VERTICALE - CLASSICO) ===
        // Questo rimane invariato
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
        ) {
            CommonHeader()

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                PolaroidEditorView(photoUri, editState) { editState = it }
            }

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AnimatedVisibility(visible = activeTool != EditorTool.NONE) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F5F5))
                                    .padding(vertical = 8.dp)
                            ) {
                                ActiveToolOptions(activeTool, editState, photoUri) { editState = it }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EditorToolButton(Icons.Default.AutoAwesome, "Filters", activeTool == EditorTool.FILTER) { activeTool = if (activeTool == EditorTool.FILTER) EditorTool.NONE else EditorTool.FILTER }
                            EditorToolButton(Icons.Default.TextFields, "Text", activeTool == EditorTool.TEXT) { activeTool = if (activeTool == EditorTool.TEXT) EditorTool.NONE else EditorTool.TEXT }
                            EditorToolButton(Icons.Default.EmojiEmotions, "Stickers", activeTool == EditorTool.EMOJI) { activeTool = if (activeTool == EditorTool.EMOJI) EditorTool.NONE else EditorTool.EMOJI }
                            EditorToolButton(Icons.Default.CropRotate, "Transform", activeTool == EditorTool.TRANSFORM) { activeTool = if (activeTool == EditorTool.TRANSFORM) EditorTool.NONE else EditorTool.TRANSFORM }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            onClick = { showExitDialog = true },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }

                        Button(
                            onClick = { onSaveMoves(editState) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A), contentColor = Color.White),
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Magic", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- POLAROID EDITOR (Standard) ---
@Composable
fun PolaroidEditorView(
    photoUri: Uri,
    editState: PhotoEditState,
    onStateChange: (PhotoEditState) -> Unit
) {
    val context = LocalContext.current
    val currentMatrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix

    Surface(
        color = Color.White,
        modifier = Modifier
            .rotate(-2f)
            .shadow(10.dp, RoundedCornerShape(2.dp)),
        shape = RoundedCornerShape(2.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.padding(12.dp).zIndex(0f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFFEEEEEE))
                        .clip(RectangleShape)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(photoUri).build()),
                        contentDescription = "Editing Photo",
                        contentScale = ContentScale.Crop,
                        colorFilter = if (currentMatrix != null) ColorFilter.colorMatrix(currentMatrix) else null,
                        modifier = Modifier.fillMaxSize().graphicsLayer {
                            scaleX = editState.scaleX * editState.zoom
                            scaleY = editState.scaleY * editState.zoom
                            rotationZ = editState.rotation
                            translationX = editState.panX
                            translationY = editState.panY
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = editState.caption,
                        onValueChange = { if (it.length <= 30) onStateChange(editState.copy(caption = it)) },
                        textStyle = TextStyle(color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontFamily = FontFamily.Cursive),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.Center) {
                                if (editState.caption.isEmpty()) {
                                    Text("Tap to write...", style = TextStyle(color = Color.LightGray, fontSize = 24.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Cursive))
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            editState.stickers.forEach { sticker ->
                key(sticker.id) {
                    MovableStickerItem(
                        sticker = sticker,
                        onUpdate = { updatedSticker ->
                            val newList = editState.stickers.toMutableList()
                            val index = newList.indexOfFirst { it.id == sticker.id }
                            if (index != -1) { newList[index] = updatedSticker; onStateChange(editState.copy(stickers = newList)) }
                        },
                        onDelete = {
                            val newList = editState.stickers.toMutableList()
                            newList.removeAll { it.id == sticker.id }
                            onStateChange(editState.copy(stickers = newList))
                        }
                    )
                }
            }
        }
    }
}

// --- LOGICA STICKER ---
@Composable
fun MovableStickerItem(sticker: StickerLayer, onUpdate: (StickerLayer) -> Unit, onDelete: () -> Unit) {
    val currentOnUpdate by rememberUpdatedState(onUpdate)
    var offset by remember(sticker.id) { mutableStateOf(IntOffset(sticker.offsetX.roundToInt(), sticker.offsetY.roundToInt())) }
    var scale by remember(sticker.id) { mutableFloatStateOf(sticker.scale) }

    LaunchedEffect(sticker) {
        offset = IntOffset(sticker.offsetX.roundToInt(), sticker.offsetY.roundToInt())
        scale = sticker.scale
    }

    Box(
        modifier = Modifier
            .offset { offset }
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(sticker.id) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(0.5f, 5f)
                    val newX = offset.x + pan.x
                    val newY = offset.y + pan.y
                    scale = newScale
                    offset = IntOffset(newX.roundToInt(), newY.roundToInt())
                    currentOnUpdate(sticker.copy(offsetX = newX, offsetY = newY, scale = newScale))
                }
            }
            .pointerInput(sticker.id) { detectTapGestures(onDoubleTap = { onDelete() }) }
    ) { Text(text = sticker.emoji, fontSize = 50.sp) }
}

// --- OPZIONI STRUMENTI (COMPATTE) ---
@Composable
fun ActiveToolOptions(activeTool: EditorTool, editState: PhotoEditState, photoUri: Uri, onStateChange: (PhotoEditState) -> Unit) {
    when (activeTool) {
        EditorTool.FILTER -> {
            LazyRow(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(FilterUtils.filters) { filter ->
                    FilterThumbnailItem(filter, photoUri, filter.name == editState.filterName) { onStateChange(editState.copy(filterName = filter.name)) }
                }
            }
        }
        EditorTool.EMOJI -> {
            val emojis = listOf("😎", "😍", "🎉", "🔥", "❤️", "⭐", "🍕", "🚀", "🐶", "🐱", "🌈", "🇮🇹")
            LazyRow(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(emojis) { emoji ->
                    Text(emoji, fontSize = 32.sp, modifier = Modifier.clickable {
                        if (editState.stickers.size < 5) {
                            val newList = editState.stickers.toMutableList()
                            newList.add(StickerLayer(emoji = emoji))
                            onStateChange(editState.copy(stickers = newList))
                        }
                    })
                }
            }
        }
        EditorTool.TRANSFORM -> {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                CompactSliderRow("Zoom", editState.zoom, 1f..4f) { onStateChange(editState.copy(zoom = it)) }
                CompactSliderRow("X", editState.panX, -500f..500f) { onStateChange(editState.copy(panX = it)) }
                CompactSliderRow("Y", editState.panY, -500f..500f) { onStateChange(editState.copy(panY = it)) }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TransformActionButton(Icons.AutoMirrored.Filled.RotateLeft, "-90") { onStateChange(editState.copy(rotation = editState.rotation - 90f)) }
                    TransformActionButton(Icons.AutoMirrored.Filled.RotateRight, "+90") { onStateChange(editState.copy(rotation = editState.rotation + 90f)) }
                    TransformActionButton(Icons.Default.SwapHoriz, "Flip") { onStateChange(editState.copy(scaleX = editState.scaleX * -1f)) }
                }
            }
        }
        EditorTool.TEXT -> {
            Text("Tap photo to write", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.Gray)
        }
        else -> {}
    }
}

// --- HELPER COMPONENTS ---
@Composable
fun CompactSliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().height(32.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
        Slider(value, onValueChange, valueRange = range, colors = SliderDefaults.colors(thumbColor = Color.Black, activeTrackColor = Color.Black, inactiveTrackColor = Color.LightGray), modifier = Modifier.weight(1f))
    }
}

@Composable
fun FilterThumbnailItem(filterItem: FilterItem, photoUri: Uri, isSelected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp).clickable { onClick() }) {
        Box(Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)).border(if (isSelected) 3.dp else 0.dp, if (isSelected) Color.Black else Color.Transparent, RoundedCornerShape(8.dp))) {
            Image(rememberAsyncImagePainter(ImageRequest.Builder(context).data(photoUri).size(200).crossfade(true).build()), filterItem.name, contentScale = ContentScale.Crop, colorFilter = if (filterItem.colorMatrix != null) ColorFilter.colorMatrix(filterItem.colorMatrix) else null, modifier = Modifier.fillMaxSize())
            if (isSelected) Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), Alignment.Center) { Icon(Icons.Default.Check, null, tint = Color.White) }
        }
        Spacer(Modifier.height(4.dp))
        Text(filterItem.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, maxLines = 1, textAlign = TextAlign.Center)
    }
}

@Composable
fun EditorToolButton(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = RoundedCornerShape(16.dp), color = if (isActive) Color.Black else Color.White, border = if (isActive) null else BorderStroke(1.dp, Color.LightGray), modifier = Modifier.size(60.dp).clickable { onClick() }) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, label, tint = if (isActive) Color.White else Color.Black, modifier = Modifier.size(28.dp)) }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun TransformActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onClick() }.padding(4.dp)) {
        Surface(shape = CircleShape, color = Color(0xFFEEEEEE), modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, label, tint = Color.Black, modifier = Modifier.size(20.dp)) }
        }
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}