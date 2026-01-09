package com.example.upics

import androidx.compose.ui.graphics.Color
import java.util.UUID

// Rappresenta un elemento di testo (o emoji) aggiunto sull'immagine
data class TextLayer(
    val id: String = UUID.randomUUID().toString(), // ID univoco
    var text: String,
    var color: Color = Color.Black,
    // Posizione X e Y rispetto al centro dell'immagine
    var offsetX: Float = 0f,
    var offsetY: Float = 0f
)

// Gli strumenti disponibili nella toolbar inferiore
enum class EditorTool {
    NONE, TEXT, EMOJI, FILTER, CROP
}

