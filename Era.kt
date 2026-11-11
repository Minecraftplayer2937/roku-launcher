package com.example.rokulauncher

import androidx.compose.ui.graphics.Color

enum class Layout { SIDEBAR, STRIP }

/** Everything that makes one Roku era look different. Sizes are in dp on a 960 x 540 canvas. */
data class EraProfile(
    val label: String,
    val layout: Layout,
    val items: List<String> = emptyList(),
    val pinned: Boolean = true,          // true: endless list under a fixed bar. false: static list, bar moves.
    val sidebarX: Float = 40f,
    val barW: Float = 300f,
    val barH: Float = 38f,
    val row: Float = 42f,
    val fontSp: Float = 15f,
    val barTop: Float = 78f,             // top of the first/focused row
    val flat: Boolean = false,
    val barColors: List<Color> = listOf(Color(0xFFEFEFEF), Color(0xFFC6C6C6)),
    val corner: Float = 4f,
    val contentX: Float = 420f,
    val contentTop: Float = 98f,
    val cols: Int = 4,
    val tileW: Float = 106f,
    val tileH: Float = 79f,
    val gap: Float = 10f,
    val tileCorner: Float = 2f,
    val sectionTitle: String? = null,
    val countLabel: Boolean = false,
    val headerX: Float = 40f,
    val headerTop: Float = 26f,
    val logoSp: Float = 26f,
    val showOptions: Boolean = false,
    val defaultLight: Boolean = false    // true: original wallpaper is the light body (2008)
)

private val R2013 = EraProfile(
    label = "Roku 2013", layout = Layout.SIDEBAR,
    items = listOf("My Channels", "Movies", "TV Shows", "News", "Search", "Channel Store", "Settings"),
    pinned = false, sidebarX = 87f, barW = 275f, barH = 32f, row = 36f, fontSp = 15f, barTop = 121f,
    barColors = listOf(Color(0xFFDADAE6), Color(0xFFBDBDCC)), corner = 3f,
    contentX = 402f, cols = 3, tileW = 148f, tileH = 110f, gap = 6f, tileCorner = 0f,
    countLabel = true, headerX = 96f, headerTop = 34f, logoSp = 22f, showOptions = true
)

val ERAS = listOf(
    EraProfile(label = "Roku 2008", layout = Layout.STRIP, defaultLight = true),
    R2013,
    R2013.copy(label = "Roku 4 (flat)", flat = true, barColors = listOf(Color(0xFFE4E4EC), Color(0xFFE4E4EC)), corner = 0f, fontSp = 16f),
    EraProfile(
        label = "Roku 2024", layout = Layout.SIDEBAR,
        items = listOf("Home", "Live TV", "Featured Free", "Sports", "What to Watch", "Daily Trivia", "Search", "Streaming Store", "Settings"),
        pinned = true, sidebarX = 52f, barW = 324f, barH = 40f, row = 36f, fontSp = 17f, barTop = 120f,
        corner = 2f, contentX = 420f, cols = 4, tileW = 106f, tileH = 79f, gap = 12f, tileCorner = 0f,
        sectionTitle = "Apps", headerX = 50f, headerTop = 24f, logoSp = 34f
    ),
    EraProfile(
        label = "Roku 2026", layout = Layout.SIDEBAR,
        items = listOf("Home", "TV Inputs", "Live TV", "Subscriptions", "For You", "Search", "Streaming Store", "Sports", "Settings"),
        pinned = true, sidebarX = 46f, barW = 340f, barH = 38f, row = 42f, fontSp = 15f, barTop = 118f,
        corner = 4f, contentX = 442f, cols = 4, tileW = 107f, tileH = 80f, gap = 10f, tileCorner = 2f,
        sectionTitle = "Your Apps", headerX = 46f, headerTop = 26f, logoSp = 26f
    )
)

val SCALES = listOf(0.85f, 1f, 1.2f)
val SCALE_LABELS = listOf("Small", "Medium", "Large")
