package com.example.upics

// 1. Gli strumenti disponibili
enum class EditorTool { NONE, FILTER, TRANSFORM, EMOJI, TEXT }

// 2. Lo stato della modifica
data class PhotoEditState(
    val filterName: String = "Normal",
    val rotation: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val zoom: Float = 1f,
    val panX: Float = 0f, // <--- NUOVO: Spostamento orizzontale
    val panY: Float = 0f, // <--- NUOVO: Spostamento verticale
    val caption: String = "",
    val stickers: List<StickerLayer> = emptyList()
)

// 3. Cos'è uno sticker
data class StickerLayer(
    val id: Long = System.currentTimeMillis(),
    val emoji: String,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f
)