package com.example.rokulauncher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Lay everything out on a 960 x 540 dp canvas regardless of the TV's real resolution.
            BoxWithConstraints(Modifier.fillMaxSize()) {
                CompositionLocalProvider(LocalDensity provides Density(constraints.maxWidth / 960f, 1f)) {
                    RokuLauncher()
                }
            }
        }
    }
}

@Composable
fun RokuLauncher() {
    val ctx = LocalContext.current
    val prefs = remember { Prefs(ctx) }
    val scope = rememberCoroutineScope()
    val apps = remember { loadApps(ctx) }

    var eraIdx by remember { mutableIntStateOf(prefs.era) }
    var scaleIdx by remember { mutableIntStateOf(prefs.scale) }
    var wallIdx by remember { mutableIntStateOf(prefs.wall) }
    val p = ERAS[eraIdx]
    val scale = SCALES[scaleIdx]
    val wall: WallStyle? = if (wallIdx == 0) (if (p.defaultLight) null else WALLS[0]) else WALLS[wallIdx - 1]

    val pos = remember { Animatable(0f) }
    var target by remember { mutableIntStateOf(0) }
    var inContent by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var stripSel by remember { mutableIntStateOf(0) }

    val n = p.items.size
    val selected = if (p.pinned) Math.floorMod(target, maxOf(n, 1)) else target
    val homeSelected = selected == 0

    val rootFocus = remember { FocusRequester() }
    val firstTile = remember { FocusRequester() }
    val settingsFocus = remember { FocusRequester() }

    val sidebarX by animateDpAsState(
        if (inContent) (-(p.barW + p.sidebarX + 30f)).dp else 0.dp, tween(280, easing = FastOutSlowInEasing), label = "sx"
    )
    val contentX by animateDpAsState(
        if (inContent) 60.dp else p.contentX.dp, tween(280, easing = FastOutSlowInEasing), label = "cx"
    )
    val contentAlpha by animateFloatAsState(if (homeSelected || inContent) 1f else 0f, tween(180), label = "ca")
    val sidebarAlpha by animateFloatAsState(if (inContent) 0f else 1f, tween(200), label = "sa")

    LaunchedEffect(eraIdx) {
        target = 0; pos.snapTo(0f); inContent = false; stripSel = 0
    }
    LaunchedEffect(showSettings, inContent, eraIdx) {
        delay(30)
        runCatching {
            when {
                showSettings -> settingsFocus.requestFocus()
                inContent -> firstTile.requestFocus()
                else -> rootFocus.requestFocus()
            }
        }
    }
    BackHandler(enabled = showSettings || inContent) { if (showSettings) showSettings = false else inContent = false }

    fun step(d: Int) {
        target = if (p.pinned) target + d else (target + d).coerceIn(0, n - 1)
        scope.launch { pos.animateTo(target.toFloat(), tween(230, easing = FastOutSlowInEasing)) }
    }
    fun openSystem() = ctx.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    Box(
        Modifier.fillMaxSize()
            .onPreviewKeyEvent { e ->
                when {
                    e.type != KeyEventType.KeyDown || showSettings -> false
                    e.key == Key.Menu -> { showSettings = true; true }
                    p.layout == Layout.STRIP -> when (e.key) {
                        Key.DirectionRight -> { stripSel = (stripSel + 1).coerceAtMost(apps.size); true }
                        Key.DirectionLeft -> { stripSel = (stripSel - 1).coerceAtLeast(0); true }
                        Key.DirectionCenter, Key.Enter -> {
                            if (stripSel == 0) showSettings = true else ctx.startActivity(apps[stripSel - 1].intent)
                            true
                        }
                        else -> false
                    }
                    inContent -> false
                    else -> when (e.key) {
                        Key.DirectionDown -> { step(1); true }
                        Key.DirectionUp -> { step(-1); true }
                        Key.DirectionRight -> { if (homeSelected) inContent = true; true }
                        Key.DirectionCenter, Key.Enter -> {
                            if (selected == n - 1) showSettings = true else if (homeSelected) inContent = true
                            true
                        }
                        else -> false
                    }
                }
            }
            .focusRequester(rootFocus).focusable()
    ) {
        Wallpaper(wall)
        if (p.layout == Layout.SIDEBAR) {
            Header(p, inContent)
            EraSidebar(pos.value, p, Modifier.offset(x = p.sidebarX.dp + sidebarX).alpha(sidebarAlpha))
            HomePane(
                apps, p, scale, firstTile, inContent, { showSettings = true }, { inContent = false },
                Modifier.offset(x = contentX).alpha(contentAlpha)
            )
        } else {
            StripHome(apps, stripSel, scale, wall == null)
        }
        if (showSettings) {
            LauncherSettings(
                values = listOf(
                    ERAS[eraIdx].label, SCALE_LABELS[scaleIdx],
                    if (wallIdx == 0) "Auto (era default)" else WALLS[wallIdx - 1].label
                ),
                onChange = { row, d ->
                    when (row) {
                        0 -> { eraIdx = Math.floorMod(eraIdx + d, ERAS.size); prefs.era = eraIdx }
                        1 -> { scaleIdx = Math.floorMod(scaleIdx + d, SCALES.size); prefs.scale = scaleIdx }
                        2 -> { wallIdx = Math.floorMod(wallIdx + d, WALLS.size + 1); prefs.wall = wallIdx }
                    }
                },
                onSystem = { openSystem() },
                onClose = { showSettings = false },
                focus = settingsFocus
            )
        }
    }
}

@Composable
private fun Header(p: EraProfile, inContent: Boolean) {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) { while (true) { now = Date(); delay(10_000) } }
    val time = remember(now) { SimpleDateFormat("h:mm a", Locale.getDefault()).format(now).lowercase() }
    val dim = Color(0xFFCFCBE0)
    Row(
        Modifier.fillMaxWidth().padding(start = p.headerX.dp, end = 40.dp, top = p.headerTop.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText("Roku", style = TextStyle(color = Color.White, fontSize = p.logoSp.sp, fontWeight = FontWeight.ExtraBold))
        if (inContent && !p.showOptions) BasicText("   ·   Home", style = TextStyle(color = Color.White, fontSize = 15.sp))
        Spacer(Modifier.weight(1f))
        if (p.showOptions) BasicText("Options ✱   |   ", style = TextStyle(color = dim, fontSize = 13.sp))
        else if (inContent) BasicText("✱ for Options   ·   ", style = TextStyle(color = dim, fontSize = 13.sp))
        BasicText(time, style = TextStyle(color = Color.White, fontSize = 13.sp))
    }
}
