package com.example.rokulauncher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.floor

private const val DIV = 26f
private val IDLE = Color(0xFFD8D6E4)
private val ACTIVE = Color(0xFF111111)

private fun g(v: Int, n: Int, row: Float) = v * row + Math.floorDiv(v, n) * DIV

@Composable
fun EraSidebar(pos: Float, p: EraProfile, modifier: Modifier = Modifier) {
    val n = p.items.size
    Box(modifier.width((p.barW + 20).dp).fillMaxHeight()) {
        if (p.pinned) PinnedList(pos, p, n) else StaticList(pos, p, n)
    }
}

@Composable
private fun Bar(p: EraProfile, y: Float) {
    val brush: Brush = if (p.flat) SolidColor(p.barColors[0]) else Brush.verticalGradient(p.barColors)
    Box(
        Modifier.offset(0.dp, y.dp).size(p.barW.dp, p.barH.dp)
            .clip(RoundedCornerShape(p.corner.dp)).background(brush)
    )
}

@Composable
private fun Label(text: String, p: EraProfile, y: Float, closeness: Float, a: Float) {
    Box(Modifier.offset(0.dp, y.dp).height(p.row.dp).alpha(a), contentAlignment = Alignment.CenterStart) {
        BasicText(
            text, Modifier.padding(start = 18.dp),
            style = TextStyle(color = lerp(IDLE, ACTIVE, closeness), fontSize = p.fontSp.sp)
        )
    }
}

/** 2024/2026: the bar stays put, the list slides under it, items above fade, a divider marks the wrap. */
@Composable
private fun PinnedList(pos: Float, p: EraProfile, n: Int) {
    val base = floor(pos).toInt()
    val frac = pos - base
    val gFocus = g(base, n, p.row) + frac * (g(base + 1, n, p.row) - g(base, n, p.row))
    Bar(p, p.barTop + (p.row - p.barH) / 2)
    for (v in base - 1..base + 11) {
        val y = g(v, n, p.row) - gFocus
        val closeness = (1f - abs(y) / p.row).coerceIn(0f, 1f)
        val a = if (y < 0) (1f + y / p.row).coerceIn(0f, 1f) else 1f
        Label(p.items[Math.floorMod(v, n)], p, p.barTop + y, closeness, a)
    }
    for (k in Math.floorDiv(base, n)..Math.floorDiv(base + 11, n) + 1) {
        val y = g(k * n, n, p.row) - DIV / 2 - gFocus
        if (y > 0) Box(Modifier.offset(0.dp, (p.barTop + y).dp).width((p.barW - 35).dp).height(1.dp).background(Color(0x99FFFFFF)))
    }
}

/** 2013/Roku 4: a plain list; only the highlight bar slides from item to item. */
@Composable
private fun StaticList(pos: Float, p: EraProfile, n: Int) {
    Bar(p, p.barTop + pos * p.row + (p.row - p.barH) / 2)
    for (i in 0 until n) {
        val closeness = (1f - abs(i - pos)).coerceIn(0f, 1f)
        Label(p.items[i], p, p.barTop + i * p.row, closeness, 1f)
    }
}
