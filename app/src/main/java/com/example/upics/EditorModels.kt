package com.example.upics

import androidx.compose.ui.graphics.Color
import java.util.UUID

// Rappresenta uno Sticker
data class StickerLayer(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var scale: Float = 1f
)

// Stato completo delle modifiche (Persistente)
data class PhotoEditState(
    var filterName: String = "Normal",
    var caption: String = "",
    var rotation: Float = 0f,
    var scaleX: Float = 1f, // Specchio orizzontale
    var scaleY: Float = 1f, // Specchio verticale
    var zoom: Float = 1f,
    val stickers: MutableList<StickerLayer> = mutableListOf()
)

enum class EditorTool {
    NONE, TEXT, EMOJI, FILTER, TRANSFORM
}