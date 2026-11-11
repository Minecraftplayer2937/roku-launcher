package com.example.rokulauncher

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppEntry(val label: String, val banner: ImageBitmap, val intent: Intent)

fun loadApps(ctx: Context): List<AppEntry> {
    val pm = ctx.packageManager
    var list = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER), 0)
    if (list.isEmpty()) list = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
    return list.filter { it.activityInfo.packageName != ctx.packageName }.map { ri ->
        val d = ri.activityInfo.loadBanner(pm) ?: ri.loadIcon(pm)
        AppEntry(
            label = ri.loadLabel(pm).toString(),
            banner = d.toBitmap(208, 116).asImageBitmap(),
            intent = Intent(Intent.ACTION_MAIN)
                .setClassName(ri.activityInfo.packageName, ri.activityInfo.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }.sortedBy { it.label.lowercase() }
}
