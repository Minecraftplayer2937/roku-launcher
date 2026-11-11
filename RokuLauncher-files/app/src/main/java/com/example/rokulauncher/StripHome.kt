package com.example.rokulauncher

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 2008 layout: purple header band, one horizontal strip of tiles, big focused tile, caption below. */
@Composable
fun StripHome(apps: List<AppEntry>, sel: Int, scale: Float, light: Boolean, modifier: Modifier = Modifier) {
    val total = apps.size + 1
    val bw = 84f * scale
    val bh = 58f * scale
    val fw = 200f * scale
    val fh = 140f * scale
    val gap = 6f
    val left = minOf(sel, 2)
    val x0 by animateDpAsState((300f - left * (bw + gap)).dp, tween(220), label = "x0")
    val main = if (light) Color(0xFF4A86B8) else Color.White
    val sub = if (light) Color(0xFF6A9CC4) else Color(0xFFCFCBE0)

    Box(modifier.fillMaxSize()) {
        Box(
            Modifier.fillMaxWidth().height(88.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF402A93), Color(0xFF2E1B73))))
        ) {
            BasicText(
                "Roku", Modifier.align(Alignment.CenterStart).padding(start = 75.dp),
                style = TextStyle(color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            )
            BasicText(
                "⌂", Modifier.align(Alignment.CenterEnd).padding(end = 70.dp),
                style = TextStyle(color = Color.White, fontSize = 28.sp)
            )
        }
        Row(
            Modifier.offset(x = x0, y = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(gap.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in (sel - left)..minOf(total - 1, sel + 4)) key(i) {
                StripTile(if (i == 0) null else apps[i - 1], i == sel, if (i == sel) fw else bw, if (i == sel) fh else bh)
            }
        }
        Column(Modifier.fillMaxWidth().padding(top = 318.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            BasicText(
                if (sel == 0) "Launcher settings" else apps[sel - 1].label,
                style = TextStyle(color = main, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
            BasicText(
                if (sel == 0) "Menu style, tile size and wallpaper" else "Press OK to open",
                style = TextStyle(color = sub, fontSize = 16.sp)
            )
        }
    }
}

@Composable
private fun StripTile(app: AppEntry?, focused: Boolean, w: Float, h: Float) {
    val aw by animateDpAsState(w.dp, tween(200), label = "w")
    val ah by animateDpAsState(h.dp, tween(200), label = "h")
    val shape = RoundedCornerShape(if (focused) 6.dp else 3.dp)
    val fill = if (app == null) Brush.verticalGradient(listOf(Color(0xFFF2F2F4), Color(0xFFD0D0D6)))
    else Brush.verticalGradient(listOf(Color(0xFF3A3A3E), Color(0xFF000000)))
    Box(
        Modifier.size(aw, ah).clip(shape).background(fill)
            .then(if (focused) Modifier.border(3.dp, Color(0xFF2A2A2E), shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (app == null) BasicText("⚙", style = TextStyle(color = Color(0xFF5B3FB0), fontSize = (aw.value * 0.45f).sp))
        else Image(
            app.banner, app.label,
            Modifier.fillMaxSize().padding(if (focused) 10.dp else 4.dp), contentScale = ContentScale.Fit
        )
    }
}
