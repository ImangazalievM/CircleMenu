package com.imangazaliev.circlemenu

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.DrawableCompat

internal fun Drawable.setTintCompat(color: Int) {
    if (isLollipop()) {
        setTint(color)
    } else {
        val wrapperDrawable = DrawableCompat.wrap(this)
        DrawableCompat.setTintList(wrapperDrawable, ColorStateList.valueOf(color))
    }
}


internal fun isLollipop() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP