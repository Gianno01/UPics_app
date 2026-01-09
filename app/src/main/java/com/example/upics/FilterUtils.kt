package com.example.upics
import androidx.compose.ui.graphics.ColorMatrix

data class PhotoFilter(val name: String, val colorMatrix: ColorMatrix)

object FilterUtils {
    val filters = listOf(
        PhotoFilter("Normal", ColorMatrix()),
        PhotoFilter("B&W", ColorMatrix().apply { setToSaturation(0f) }),
        PhotoFilter("Sepia", ColorMatrix(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))),
        PhotoFilter("Warm", ColorMatrix(floatArrayOf(
            1.438f, -0.062f, -0.062f, 0f, 0f,
            -0.122f, 1.378f, -0.122f, 0f, 0f,
            -0.016f, -0.016f, 1.483f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )))
    )
}