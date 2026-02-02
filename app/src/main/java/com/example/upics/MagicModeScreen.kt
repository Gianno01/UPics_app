package com.example.upics

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current

    // Stato locale delle modifiche (si resetta se esci e rientri, come richiesto dall'alert)
    var editState by remember { mutableStateOf(PhotoEditState()) }
    var activeTool by remember { mutableStateOf(EditorTool.NONE) }
    var showExitDialog by remember { mutableStateOf(false) } // Per il dialog di conferma

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Gestione tasto fisico Indietro
    BackHandler {
        showExitDialog = true
    }

    // --- ALERT DIALOG ---
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Attenzione") },
            text = { Text("Se torni indietro perderai le modifiche. Continuare?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    navController.popBackStack() // Torna indietro perdendo tutto
                }) {
                    Text("Sì, esci", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            if (!isLandscape) {
                MagicModeHeader(onBackClick = { showExitDialog = true })
            }
        },
        bottomBar = {
            if (!isLandscape) {
                BottomToolbar(
                    activeTool = activeTool,
                    onToolSelected = { activeTool = if (activeTool == it) EditorTool.NONE else it },
                    onSave = { onSaveMoves(editState) }, // SALVA E VAI A STAMPA
                    content = {
                        ActiveToolPanel(activeTool, editState, onStateChange = { newState -> editState = newState }, context = context)
                    }
                )
            }
        }
    ) { paddingValues ->

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp), contentAlignment = Alignment.Center) {
                    PolaroidView(photoUri, editState, onStateChange = { editState = it })
                }
                Column(
                    modifier = Modifier.width(320.dp).fillMaxHeight().background(Color.White).border(1.dp, Color(0xFFEEEEEE)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showExitDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Text("Magic Mode", fontWeight = FontWeight.Bold)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ActiveToolPanel(activeTool, editState, onStateChange = { editState = it }, context = context)
                    }
                    Divider()
                    BottomToolbarLandscape(
                        activeTool = activeTool,
                        onToolSelected = { activeTool = if (activeTool == it) EditorTool.NONE else it },
                        onSave = { onSaveMoves(editState) }
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    PolaroidView(photoUri, editState, onStateChange = { editState = it })
                }
            }
        }
    }
}

// --- POLAROID INTERATTIVA ---
@Composable
fun PolaroidView(
    photoUri: Uri,
    editState: PhotoEditState,
    onStateChange: (PhotoEditState) -> Unit
) {
    val context = LocalContext.current
    val currentMatrix = FilterUtils.filters.find { it.name == editState.filterName }?.colorMatrix

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxHeight(0.85f)
            .aspectRatio(0.80f)
            .border(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
                    .clip(RoundedCornerShape(2.dp))
                    .clipToBounds()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(photoUri).build()),
                    contentDescription = "Editing Photo",
                    contentScale = ContentScale.Crop,
                    colorFilter = if (currentMatrix != null) ColorFilter.colorMatrix(currentMatrix) else null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = editState.rotation
                            scaleX = editState.scaleX * editState.zoom
                            scaleY = editState.scaleY * editState.zoom
                        }
                )

                editState.stickers.forEach { sticker ->
                    MovableSticker(
                        sticker = sticker,
                        onUpdate = { updatedSticker ->
                            val newList = editState.stickers.toMutableList()
                            val index = newList.indexOfFirst { it.id == sticker.id }
                            if (index != -1) {
                                newList[index] = updatedSticker
                                onStateChange(editState.copy(stickers = newList))
                            }
                        },
                        onDelete = {
                            val newList = editState.stickers.toMutableList()
                            newList.removeAll { it.id == sticker.id }
                            onStateChange(editState.copy(stickers = newList))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) {
                if (editState.caption.isEmpty()) {
                    Text("Tap to add text...", color = Color.LightGray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
                BasicTextField(
                    value = editState.caption,
                    onValueChange = { if (it.length <= 20) onStateChange(editState.copy(caption = it)) },
                    textStyle = TextStyle(
                        color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(text = "${editState.caption.length}/20", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
        }
    }
}

// STICKER CON FIX POSIZIONAMENTO
@Composable
fun MovableSticker(sticker: StickerLayer, onUpdate: (StickerLayer) -> Unit, onDelete: () -> Unit) {
    val currentSticker by rememberUpdatedState(sticker)

    Box(
        modifier = Modifier
            .offset { IntOffset(sticker.offsetX.roundToInt(), sticker.offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onDelete() })
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val s = currentSticker
                    onUpdate(s.copy(
                        offsetX = s.offsetX + pan.x,
                        offsetY = s.offsetY + pan.y,
                        scale = (s.scale * zoom).coerceIn(0.5f, 3f)
                    ))
                }
            }
            .graphicsLayer(scaleX = sticker.scale, scaleY = sticker.scale)
    ) {
        Text(text = sticker.emoji, fontSize = 40.sp)
    }
}

// (Le funzioni ActiveToolPanel, BottomToolbar, MagicModeHeader, EditorToolButton rimangono uguali a prima)
// Assicurati di includerle qui sotto per completezza se copi/incolli l'intero file.
// Per brevità, se le hai già nel progetto, usa quelle. Altrimenti dimmelo e le rimetto.
// --- INCLUDERE IL RESTO DELLE FUNZIONI UI QUI ---
@Composable
fun ActiveToolPanel(activeTool: EditorTool, editState: PhotoEditState, onStateChange: (PhotoEditState) -> Unit, context: android.content.Context) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        when (activeTool) {
            EditorTool.FILTER -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(8.dp)) {
                    items(FilterUtils.filters) { filter ->
                        FilterChip(selected = filter.name == editState.filterName, onClick = { onStateChange(editState.copy(filterName = filter.name)) }, label = { Text(filter.name) })
                    }
                }
            }
            EditorTool.TRANSFORM -> {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    IconButton(onClick = { onStateChange(editState.copy(rotation = editState.rotation - 90f)) }) { Icon(Icons.Default.RotateLeft, "Rotate") }
                    IconButton(onClick = { onStateChange(editState.copy(scaleX = editState.scaleX * -1f)) }) { Icon(Icons.Default.Flip, "Flip") }
                }
                Slider(value = editState.zoom, onValueChange = { onStateChange(editState.copy(zoom = it)) }, valueRange = 1f..3f, modifier = Modifier.padding(horizontal = 16.dp))
            }
            EditorTool.EMOJI -> {
                val emojis = listOf("😎", "😍", "🎉", "🔥", "❤️", "⭐", "🍕", "🚀", "🐶", "🐱", "🌈", "🇮🇹")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(16.dp)) {
                    items(emojis) { emoji ->
                        Text(text = emoji, fontSize = 32.sp, modifier = Modifier.clickable {
                            if (editState.stickers.size < 3) {
                                val newList = editState.stickers.toMutableList().apply { add(StickerLayer(emoji = emoji)) }
                                onStateChange(editState.copy(stickers = newList))
                            }
                        })
                    }
                }
            }
            EditorTool.TEXT -> Text("Tap photo border to type.", modifier = Modifier.padding(16.dp), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            else -> {}
        }
    }
}

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
            .background(Color.White) // Sfondo Bianco
        // .shadow(8.dp) <-- RIMOSSO: Questo causava l'alone grigio
    ) {
        // Pannello strumenti (Filtri, Slider, etc.)
        if (activeTool != EditorTool.NONE) {
            // Anche qui cambiamo lo sfondo da F9F9F9 a White per uniformità
            Box(modifier = Modifier.background(Color.White)) {
                content()
            }
            // Mettiamo un divisore sottilissimo se vuoi separazione, o rimuovilo per total white
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
        }

        // Riga Icone
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

        // Bottone Done
        Button(
            onClick = onSave,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp) // Padding aggiustato
                .height(50.dp)
        ) {
            Text("Done", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Spazio finale per staccare dal bordo inferiore dello schermo (safe area)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BottomToolbarLandscape(activeTool: EditorTool, onToolSelected: (EditorTool) -> Unit, onSave: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            EditorToolButton(Icons.Default.FormatColorFill, "Filter", activeTool == EditorTool.FILTER) { onToolSelected(EditorTool.FILTER) }
            EditorToolButton(Icons.Default.EmojiEmotions, "Sticker", activeTool == EditorTool.EMOJI) { onToolSelected(EditorTool.EMOJI) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSave, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)), modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Done") }
    }
}

@Composable
fun MagicModeHeader(onBackClick: () -> Unit) {
    Column {
        CommonHeader()
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Magic mode", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Divider()
    }
}

@Composable
fun EditorToolButton(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(45.dp).clip(RoundedCornerShape(12.dp)).background(if (isActive) Color(0xFFFFF176) else Color.Transparent).border(if (isActive) 2.dp else 1.dp, if (isActive) Color(0xFFFBC02D) else Color.LightGray, RoundedCornerShape(12.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) { Icon(icon, contentDescription = label, tint = Color.Black) }
        Text(label, fontSize = 10.sp, color = Color.Black)
    }
}