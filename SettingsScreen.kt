package com.example.rokulauncher

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OPTION_LABELS = listOf("Menu style", "Tile size", "Wallpaper")
private const val ROWS = 5 // 3 options, system settings, close

/** values = current value text for the three option rows. onChange(row, +1/-1) cycles that option. */
@Composable
fun LauncherSettings(
    values: List<String>, onChange: (Int, Int) -> Unit, onSystem: () -> Unit, onClose: () -> Unit,
    focus: FocusRequester
) {
    var row by remember { mutableIntStateOf(0) }
    Box(
        Modifier.fillMaxSize().background(Color(0xCC000000))
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown) false else when (e.key) {
                    Key.DirectionDown -> { row = (row + 1).coerceAtMost(ROWS - 1); true }
                    Key.DirectionUp -> { row = (row - 1).coerceAtLeast(0); true }
                    Key.DirectionLeft -> { if (row < 3) onChange(row, -1); true }
                    Key.DirectionRight -> { if (row < 3) onChange(row, 1); true }
                    Key.DirectionCenter, Key.Enter -> {
                        when (row) { 3 -> onSystem(); 4 -> onClose(); else -> onChange(row, 1) }
                        true
                    }
                    Key.Escape -> { onClose(); true }
                    else -> false
                }
            }
            .focusRequester(focus).focusable(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.width(520.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xF2140A2B)).padding(28.dp)
        ) {
            BasicText(
                "Launcher settings", Modifier.padding(bottom = 16.dp),
                style = TextStyle(color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            )
            for (i in 0 until ROWS) {
                val on = i == row
                val text = when (i) { 3 -> "Open system settings"; 4 -> "Close"; else -> OPTION_LABELS[i] }
                Row(
                    Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(4.dp))
                        .then(if (on) Modifier.background(Brush.verticalGradient(listOf(Color(0xFFEFEFEF), Color(0xFFC6C6C6)))) else Modifier)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val c = if (on) Color(0xFF111111) else Color(0xFFD8D6E4)
                    BasicText(text, style = TextStyle(color = c, fontSize = 16.sp))
                    Spacer(Modifier.weight(1f))
                    if (i < 3) BasicText("◀  ${values[i]}  ▶", style = TextStyle(color = c, fontSize = 16.sp))
                }
            }
        }
    }
}
