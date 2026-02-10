package com.example.upics

import androidx.compose.ui.graphics.ColorMatrix

// 1. Definizione di un filtro
data class FilterItem(val name: String, val colorMatrix: ColorMatrix?)

// 2. La lista dei filtri disponibili
object FilterUtils {
    val filters = listOf(
        FilterItem("Normal", null),
        FilterItem("B&W", ColorMatrix().apply { setToSaturation(0f) }),
        FilterItem("Sepia", ColorMatrix().apply {
            setToScale(1f, 0.95f, 0.82f, 1f)
        }),
        FilterItem("Cool", ColorMatrix().apply {
            setToScale(0.8f, 0.9f, 1f, 1f)
        }),
        FilterItem("Warm", ColorMatrix().apply {
            setToScale(1.1f, 1f, 0.9f, 1f)
        })
    )
}