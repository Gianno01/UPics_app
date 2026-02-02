package com.example.upics

import androidx.compose.ui.graphics.Color
import java.util.UUID

// Rappresenta un elemento Sticker (Emoji)
data class StickerLayer(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var scale: Float = 1f
)

// Dati completi di modifica per una foto
data class PhotoEditState(
    var filterName: String = "Normal",
    var caption: String = "",
    var rotation: Float = 0f,
    var scaleX: Float = 1f, // 1f = normale, -1f = specchiato orizzontalmente
    var scaleY: Float = 1f, // 1f = normale, -1f = specchiato verticalmente
    var zoom: Float = 1f,   // Simulazione crop
    val stickers: MutableList<StickerLayer> = mutableListOf()
)

// Gli strumenti disponibili nella toolbar
enum class EditorTool {
    NONE, TEXT, EMOJI, FILTER, TRANSFORM // "Transform" include Ruota, Specchia, Crop(Zoom)
}