package com.example.rokulauncher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

class WallStyle(val label: String, val base: Color, val mid: Color, val glow: Color)

/** Index 0 of the wallpaper setting is "Auto" (the era's default); these follow it. */
val WALLS = listOf(
    WallStyle("Purple", Color(0xFF05010F), Color(0xFF260645), Color(0xFF4A1580)),
    WallStyle("Midnight", Color(0xFF02050F), Color(0xFF0A1E45), Color(0xFF123A7A)),
    WallStyle("Teal", Color(0xFF011010), Color(0xFF073B3A), Color(0xFF0F6B66)),
    WallStyle("Sunset", Color(0xFF100210), Color(0xFF4A1050), Color(0xFF9B2C6A)),
    WallStyle("Black", Color(0xFF000000), Color(0xFF0A0A0A), Color(0xFF1A1A1A))
)

/** style == null draws the light 2008 body. */
@Composable
fun Wallpaper(style: WallStyle?) {
    Canvas(Modifier.fillMaxSize()) {
        if (style == null) {
            drawRect(Color(0xFFDADAE3))
            drawRect(
                Brush.radialGradient(
                    0f to Color(0xFFF0F0F5), 1f to Color(0xFFD2D2DD),
                    center = Offset(size.width * 0.5f, size.height * 0.55f), radius = size.width * 0.7f
                )
            )
        } else {
            drawRect(style.base)
            drawRect(
                Brush.radialGradient(
                    0f to style.glow, 0.45f to style.mid, 1f to style.base.copy(alpha = 0f),
                    center = Offset(size.width * 0.5f, size.height * 0.05f), radius = size.width * 0.75f
                )
            )
            drawRect(
                Brush.radialGradient(
                    0f to style.glow.copy(alpha = 0.4f), 1f to Color.Transparent,
                    center = Offset(size.width * 0.2f, size.height * 0.45f), radius = size.width * 0.35f
                )
            )
        }
    }
}
