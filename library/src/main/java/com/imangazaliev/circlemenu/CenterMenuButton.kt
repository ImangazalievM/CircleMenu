package com.imangazaliev.circlemenu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

internal class CenterMenuButton(
        context: Context,
        private val iconColor: Int,
        private var isOpened: Boolean
) : FloatingActionButton(context) {

    private var preLollipopAnimationSet: AnimatorSet? = null

    init {
        setImageDrawable(getIconDrawable(!isOpened))
    }

    internal fun setColor(color: Int) {
        backgroundTintList = ColorStateList.valueOf(color)
    }

    fun setOpened(isOpened: Boolean) {
        this.isOpened = isOpened
        if (isLollipop()) {
            startVectorAnimation(isOpened)
        } else {
            startPreLollipopAnimation()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun startVectorAnimation(isOpened: Boolean) {
        val menuIcon = getIconDrawable(isOpened) as AnimatedVectorDrawable
        setImageDrawable(menuIcon)
        menuIcon.start()
    }

    private fun startPreLollipopAnimation() {
        if (preLollipopAnimationSet == null) {
            preLollipopAnimationSet = createPreLollipopIconAnimation()
        }
        preLollipopAnimationSet!!.start()
    }

    private fun createPreLollipopIconAnimation(): AnimatorSet {
        val preLollipopAnimationSet = AnimatorSet()
        val scaleOutX = ObjectAnimator.ofFloat(drawable, "scaleX", 1.0f, 0.2f)
        val scaleOutY = ObjectAnimator.ofFloat(drawable, "scaleY", 1.0f, 0.2f)
        val scaleInX = ObjectAnimator.ofFloat(this, "scaleX", 0.2f, 1.0f)
        val scaleInY = ObjectAnimator.ofFloat(this, "scaleY", 0.2f, 1.0f)
        scaleOutX.duration = 50
        scaleOutY.duration = 50
        scaleInX.duration = 150
        scaleInY.duration = 150
        scaleInX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                val icon = getIconDrawable(isOpened)
                setImageDrawable(icon)
            }
        })
        preLollipopAnimationSet.play(scaleOutX).with(scaleOutY)
        preLollipopAnimationSet.play(scaleInX).with(scaleInY).after(scaleOutX)
        preLollipopAnimationSet.interpolator = OvershootInterpolator(2f)
        return preLollipopAnimationSet
    }

    private fun getIconDrawable(isOpened: Boolean): Drawable {
        val iconResId = if (isLollipop()) {
            if (isOpened) R.drawable.ic_menu_animated else R.drawable.ic_close_animated
        } else {
            if (isOpened) R.drawable.ic_close_vector else R.drawable.ic_menu_vector
        }

        val icon = ContextCompat.getDrawable(context, iconResId)!!
        icon.setTintCompat(iconColor)
        return icon
    }


}