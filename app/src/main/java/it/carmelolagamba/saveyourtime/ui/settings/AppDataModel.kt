package it.carmelolagamba.saveyourtime.ui.settings

import android.graphics.drawable.Drawable

/**
 * @author carmelolg
 * @since version 1.0
 */
class AppDataModel internal constructor(
    var icon: Drawable?,
    var name: String?,
    var packageName: String,
    var checked: Boolean,
    var notifyTime: Int,
    var todayUsage: Int,
    var lastUpdate: Long
)