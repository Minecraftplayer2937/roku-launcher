package com.example.rokulauncher

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Tile(val label: String, val banner: ImageBitmap?, val action: (Context) -> Unit)

private fun sys(label: String, action: String) =
    Tile(label, null) { c -> c.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePane(
    apps: List<AppEntry>, p: EraProfile, scale: Float, firstTile: FocusRequester, active: Boolean,
    onSettings: () -> Unit, onExitLeft: () -> Unit, modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val tw = p.tileW * scale
    val th = p.tileH * scale
    val room = 960f - p.contentX - 30f
    val cols = minOf(p.cols, maxOf(2, ((room + p.gap) / (tw + p.gap)).toInt()))
    val paneW = cols * (tw + p.gap)
    val titlePx = with(LocalDensity.current) { 22.dp.toPx() }
    // Roku behaviour: the focused row is pinned to the top of the pane and the content scrolls up beneath it.
    val spec = remember(titlePx) {
        object : BringIntoViewSpec {
            override val scrollAnimationSpec: AnimationSpec<Float> = tween(260, easing = FastOutSlowInEasing)
            override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float) = offset - titlePx
        }
    }
    val rows = remember(apps, cols) { apps.map { a -> Tile(a.label, a.banner) { c -> c.startActivity(a.intent) } }.chunked(cols) }
    val jump = listOf(
        Tile("Launcher settings", null) { onSettings() },
        sys("System settings", Settings.ACTION_SETTINGS),
        sys("Wi-Fi", Settings.ACTION_WIFI_SETTINGS),
        sys("Display", Settings.ACTION_DISPLAY_SETTINGS)
    )
    CompositionLocalProvider(LocalBringIntoViewSpec provides spec) {
        LazyColumn(
            modifier.width(paneW.dp).fillMaxHeight().padding(top = p.contentTop.dp),
            verticalArrangement = Arrangement.spacedBy(p.gap.dp),
            contentPadding = PaddingValues(bottom = 420.dp)
        ) {
            itemsIndexed(rows) { r, row ->
                Column {
                    if (r == 0) TitleRow(p, paneW, apps.size)
                    Row(horizontalArrangement = Arrangement.spacedBy(p.gap.dp)) {
                        row.forEachIndexed { c, t ->
                            TileView(t, tw, th, p, ctx, active, if (r == 0 && c == 0) firstTile else null, c == 0, onExitLeft)
                        }
                    }
                }
            }
            item {
                Column {
                    Box(Modifier.height(22.dp), contentAlignment = Alignment.CenterStart) {
                        BasicText("Jump To", style = TextStyle(color = Color(0xFFCFCBE0), fontSize = 11.sp))
                    }
                    jump.chunked(cols).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(p.gap.dp)) {
                            row.forEachIndexed { c, t -> TileView(t, tw, th, p, ctx, active, null, c == 0, onExitLeft) }
                        }
                        Spacer(Modifier.height(p.gap.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleRow(p: EraProfile, paneW: Float, count: Int) {
    Row(Modifier.width(paneW.dp).height(22.dp), verticalAlignment = Alignment.CenterVertically) {
        if (p.sectionTitle != null) BasicText(p.sectionTitle, style = TextStyle(color = Color(0xFFCFCBE0), fontSize = 11.sp))
        Spacer(Modifier.weight(1f))
        if (p.countLabel) BasicText("$count Channels", Modifier.padding(end = p.gap.dp), style = TextStyle(color = Color(0xFFCFCBE0), fontSize = 11.sp))
    }
}

@Composable
private fun TileView(
    t: Tile, tw: Float, th: Float, p: EraProfile, ctx: Context, showFocus: Boolean,
    requester: FocusRequester?, leftmost: Boolean, onExitLeft: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(p.tileCorner.dp)
    Box(
        Modifier.size(tw.dp, th.dp)
            .then(if (requester != null) Modifier.focusRequester(requester) else Modifier)
            .onFocusChanged { focused = it.isFocused }
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown) false else when (e.key) {
                    Key.DirectionLeft -> if (leftmost) { onExitLeft(); true } else false
                    Key.DirectionCenter, Key.Enter -> { t.action(ctx); true }
                    else -> false
                }
            }
            .focusable()
            .clip(shape)
            .background(Color(0x66000000))
            .border(if (focused && showFocus) 3.dp else 0.dp, Color.White, shape),
        contentAlignment = Alignment.Center
    ) {
        if (t.banner != null) Image(t.banner, t.label, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        else BasicText(t.label, style = TextStyle(color = Color(0xFFE6E3F2), fontSize = 10.sp))
    }
}
