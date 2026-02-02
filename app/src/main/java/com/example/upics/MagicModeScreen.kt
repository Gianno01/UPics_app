package com.example.upics

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun MagicModeScreen(navController: NavController, photoUri: Uri) {
    // --- STATO ---
    val context = LocalContext.current

    // Stato completo delle modifiche (Filtri, Trasformazioni, Testo, Sticker)
    val editState = remember { mutableStateOf(PhotoEditState()) }

    // Strumento attivo corrente
    var activeTool by remember { mutableStateOf(EditorTool.NONE) }

    // Rileva orientamento schermo (Configuration Change)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // --- LOGICA DI SALVATAGGIO ---
    val saveAndExit = {
        navController.previousBackStackEntry?.savedStateHandle?.set("result_filter", editState.value.filterName)
        navController.previousBackStackEntry?.savedStateHandle?.set("result_uri", photoUri.toString())
        // Nota: In un'app reale, qui dovresti renderizzare la bitmap finale con sticker e testo
        navController.popBackStack()
    }

    BackHandler { saveAndExit() }

    // --- LAYOUT RESPONSIVE (BoxWithConstraints non strettamente necessario se usiamo Configuration) ---
    Scaffold(
        topBar = {
            if (!isLandscape) { // In Landscape nascondiamo l'header standard per spazio
                MagicModeHeader(onBackClick = { saveAndExit() })
            }
        },
        bottomBar = {
            // In Portrait la toolbar è sotto, in Landscape la gestiamo lateralmente
            if (!isLandscape) {
                BottomToolbar(
                    activeTool = activeTool,
                    onToolSelected = { activeTool = if (activeTool == it) EditorTool.NONE else it },
                    onSave = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("result_filter", editState.value.filterName)
                        navController.previousBackStackEntry?.savedStateHandle?.set("result_uri", photoUri.toString())
                        navController.navigate("resume")
                    },
                    content = {
                        // Pannello opzioni dinamico (Filtri, Sticker, Transform)
                        ActiveToolPanel(activeTool, editState)
                    }
                )
            }
        }
    ) { paddingValues ->

        if (isLandscape) {
            // --- LAYOUT ORIZZONTALE ---
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFEEEEEE))
            ) {
                // 1. Anteprima a Sinistra (Grande)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PolaroidView(photoUri, editState)
                }

                // 2. Controlli a Destra
                Column(
                    modifier = Modifier
                        .width(320.dp) // Larghezza fissa pannello laterale
                        .fillMaxHeight()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Header mini per Landscape
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { saveAndExit() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Text("Magic Mode", fontWeight = FontWeight.Bold)
                    }
                    Divider()

                    // Contenuto Pannello
                    Column(modifier = Modifier.weight(1f)) {
                        ActiveToolPanel(activeTool, editState)
                    }

                    Divider()
                    // Toolbar Verticale o Griglia
                    BottomToolbarLandscape(
                        activeTool = activeTool,
                        onToolSelected = { activeTool = if (activeTool == it) EditorTool.NONE else it },
                        onSave = {
                            navController.previousBackStackEntry?.savedStateHandle?.set("result_filter", editState.value.filterName)
                            navController.previousBackStackEntry?.savedStateHandle?.set("result_uri", photoUri.toString())
                            navController.navigate("resume")
                        }
                    )
                }
            }
        } else {
            // --- LAYOUT VERTICALE (Portrait) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFEEEEEE))
            ) {
                // Area Foto
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PolaroidView(photoUri, editState)
                }
            }
        }
    }
}

// --- COMPONENTE: POLAROID (FOTO + STICKERS + TESTO) ---
@Composable
fun PolaroidView(photoUri: Uri, editState: MutableState<PhotoEditState>) {
    val context = LocalContext.current
    val state = editState.value
    val currentMatrix = FilterUtils.filters.find { it.name == state.filterName }?.colorMatrix

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxHeight(0.85f) // Adatta proporzioni
            .aspectRatio(0.80f)   // Proporzione simil-polaroid
            .border(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. AREA IMMAGINE (con trasformazioni e sticker)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.LightGray)
            ) {
                // Immagine Base
                Image(
                    painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(photoUri).build()),
                    contentDescription = "Editing Photo",
                    contentScale = ContentScale.Crop, // Crop visivo base
                    colorFilter = if (currentMatrix != null) ColorFilter.colorMatrix(currentMatrix) else null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = state.rotation
                            scaleX = state.scaleX * state.zoom // Applica specchio e zoom
                            scaleY = state.scaleY * state.zoom
                        }
                )

                // Overlay Stickers
                state.stickers.forEach { sticker ->
                    MovableSticker(
                        sticker = sticker,
                        onUpdate = { updated ->
                            // Forza aggiornamento stato per ricomposizione
                            editState.value = state.copy(stickers = state.stickers.toMutableList().apply {
                                set(indexOf(sticker), updated)
                            })
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. AREA TESTO (Bordo Bianco)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.caption.isEmpty()) {
                    Text(
                        "Tap to add text...",
                        color = Color.LightGray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                BasicTextField(
                    value = state.caption,
                    onValueChange = {
                        if (it.length <= 20) { // MAX 20 CARATTERI
                            editState.value = state.copy(caption = it)
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp, // Scritta a mano simulata
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Contatore caratteri piccolo
            Text(
                text = "${state.caption.length}/20",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// --- COMPONENTE: STICKER TRASCINABILE ---
@Composable
fun MovableSticker(sticker: StickerLayer, onUpdate: (StickerLayer) -> Unit) {
    Box(
        modifier = Modifier
            .offset(x = sticker.offsetX.dp, y = sticker.offsetY.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (sticker.scale * zoom).coerceIn(0.5f, 3f)
                    onUpdate(sticker.copy(
                        offsetX = sticker.offsetX + pan.x / density, // Convert px to dp approx
                        offsetY = sticker.offsetY + pan.y / density,
                        scale = newScale
                    ))
                }
            }
            .graphicsLayer(scaleX = sticker.scale, scaleY = sticker.scale)
    ) {
        Text(text = sticker.emoji, fontSize = 40.sp)
    }
}


// --- PANNELLO STRUMENTI ATTIVO ---
@Composable
fun ActiveToolPanel(activeTool: EditorTool, editState: MutableState<PhotoEditState>) {
    val state = editState.value

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        when (activeTool) {
            EditorTool.FILTER -> {
                Text("Select Filter", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(8.dp)) {
                    items(FilterUtils.filters) { filter ->
                        FilterChip(
                            selected = filter.name == state.filterName,
                            onClick = { editState.value = state.copy(filterName = filter.name) },
                            label = { Text(filter.name) }
                        )
                    }
                }
            }
            EditorTool.TRANSFORM -> {
                Text("Transform", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Ruota
                    IconButton(onClick = { editState.value = state.copy(rotation = state.rotation - 90f) }) {
                        Icon(Icons.Default.RotateLeft, "Rotate Left")
                    }
                    IconButton(onClick = { editState.value = state.copy(rotation = state.rotation + 90f) }) {
                        Icon(Icons.Default.RotateRight, "Rotate Right")
                    }
                    // Specchia Orizzontale
                    IconButton(onClick = { editState.value = state.copy(scaleX = state.scaleX * -1f) }) {
                        Icon(Icons.Default.Flip, "Flip Horizontal")
                    }
                    // Specchia Verticale
                    IconButton(onClick = { editState.value = state.copy(scaleY = state.scaleY * -1f) }) {
                        Icon(Icons.Default.FlipCameraAndroid, "Flip Vertical") // Icona approx
                    }
                }

                // Slider Crop (Zoom)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(Icons.Default.Crop, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = state.zoom,
                        onValueChange = { editState.value = state.copy(zoom = it) },
                        valueRange = 1f..3f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            EditorTool.EMOJI -> {
                Text("Add Sticker", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                val emojis = listOf("😎", "😍", "🎉", "🔥", "❤️", "⭐", "🍕", "🚀", "🐶", "🐱", "🌈", "🇮🇹")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(16.dp)) {
                    items(emojis) { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 32.sp,
                            modifier = Modifier.clickable {
                                // Aggiungi sticker al centro
                                val newSticker = StickerLayer(emoji = emoji)
                                editState.value = state.copy(stickers = (state.stickers + newSticker).toMutableList())
                            }
                        )
                    }
                }
                Text("Drag stickers to move, pinch to resize", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 16.dp))
            }
            EditorTool.TEXT -> {
                Text("Tap on the white border of the photo to type.", modifier = Modifier.padding(16.dp), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
            else -> {}
        }
    }
}


// --- TOOLBAR VERTICAL (STANDARD PORTRAIT) ---
@Composable
fun BottomToolbar(
    activeTool: EditorTool,
    onToolSelected: (EditorTool) -> Unit,
    onSave: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(8.dp)
    ) {
        // Pannello contenuto dinamico (si apre sopra i bottoni)
        if (activeTool != EditorTool.NONE) {
            Box(modifier = Modifier.background(Color(0xFFF5F5F5))) {
                content()
            }
            Divider()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EditorToolButton(Icons.Default.FormatColorFill, "Filter", activeTool == EditorTool.FILTER) { onToolSelected(EditorTool.FILTER) }
            EditorToolButton(Icons.Default.CropRotate, "Edit", activeTool == EditorTool.TRANSFORM) { onToolSelected(EditorTool.TRANSFORM) }
            EditorToolButton(Icons.Default.EmojiEmotions, "Sticker", activeTool == EditorTool.EMOJI) { onToolSelected(EditorTool.EMOJI) }
            EditorToolButton(Icons.Default.TextFields, "Text", activeTool == EditorTool.TEXT) { onToolSelected(EditorTool.TEXT) }
        }

        Button(
            onClick = onSave,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .height(50.dp)
        ) {
            Icon(Icons.Default.Print, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Done")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- TOOLBAR HORIZONTAL (LANDSCAPE) ---
@Composable
fun BottomToolbarLandscape(
    activeTool: EditorTool,
    onToolSelected: (EditorTool) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EditorToolButton(Icons.Default.FormatColorFill, "Filter", activeTool == EditorTool.FILTER) { onToolSelected(EditorTool.FILTER) }
            EditorToolButton(Icons.Default.CropRotate, "Edit", activeTool == EditorTool.TRANSFORM) { onToolSelected(EditorTool.TRANSFORM) }
            EditorToolButton(Icons.Default.EmojiEmotions, "Sticker", activeTool == EditorTool.EMOJI) { onToolSelected(EditorTool.EMOJI) }
            EditorToolButton(Icons.Default.TextFields, "Text", activeTool == EditorTool.TEXT) { onToolSelected(EditorTool.TEXT) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.Print, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Done")
        }
    }
}

// --- UTILS UI ---
@Composable
fun MagicModeHeader(onBackClick: () -> Unit) {
    Column {
        CommonHeader()
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Magic mode", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun EditorToolButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isActive) Color(0xFFFFF176) else Color.Transparent)
                .border(if (isActive) 2.dp else 1.dp, if (isActive) Color(0xFFFBC02D) else Color.LightGray, RoundedCornerShape(12.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.Black)
        }
        Text(label, fontSize = 10.sp, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
    }
}