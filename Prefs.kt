package com.example.rokulauncher

import android.content.Context

class Prefs(ctx: Context) {
    private val sp = ctx.getSharedPreferences("launcher", Context.MODE_PRIVATE)
    var era: Int
        get() = sp.getInt("era", 3).coerceIn(0, ERAS.size - 1)
        set(v) { sp.edit().putInt("era", v).apply() }
    var scale: Int
        get() = sp.getInt("scale", 1).coerceIn(0, SCALES.size - 1)
        set(v) { sp.edit().putInt("scale", v).apply() }
    var wall: Int
        get() = sp.getInt("wall", 0).coerceIn(0, WALLS.size)
        set(v) { sp.edit().putInt("wall", v).apply() }
}
