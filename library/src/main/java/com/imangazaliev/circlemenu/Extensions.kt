package com.imangazaliev.circlemenu

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat


internal fun Drawable.setTintCompat(color: Int) {
    if (isLollipop()) {
        setTint(color)
    } else {
        val wrapperDrawable = DrawableCompat.wrap(this)
        DrawableCompat.setTintList(wrapperDrawable, ColorStateList.valueOf(color))
    }
}


internal fun isLollipop() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun View.onLaidOut(listener: () -> Unit) {
    onLaidOut(true, listener)
}

/** Executes the given [java.lang.Runnable] when the view is laid out  */
internal fun View.onLaidOut(removeListener: Boolean, listener: () -> Unit) {
    if (isLaidOut(this)) {
        listener()
        return
    }
    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (removeListener) {
                removeOnGlobalLayoutListener(viewTreeObserver, this)
            }
            listener()
        }
    })
}

private fun removeOnGlobalLayoutListener(observer: ViewTreeObserver,
                                         listener: OnGlobalLayoutListener?) {
    if (Build.VERSION.SDK_INT >= 16) {
        observer.removeOnGlobalLayoutListener(listener)
    } else {
        observer.removeGlobalOnLayoutListener(listener)
    }
}

/** Returns whether or not the view has been laid out  */
private fun View.isLaidOut(view: View): Boolean {
    return ViewCompat.isLaidOut(view) && view.width > 0 && view.height > 0
}