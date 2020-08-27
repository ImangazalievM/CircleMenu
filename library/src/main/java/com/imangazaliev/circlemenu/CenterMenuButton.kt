package com.imangazaliev.circlemenu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

@SuppressLint("ViewConstructor")
internal class CenterMenuButton(
        context: Context,
        backgroundColor: Int,
        menuIconType: CircleMenu.MenuIconType,
        private val iconColor: Int,
        private var isOpened: Boolean
) : FloatingActionButton(context) {

    private var preLollipopAnimationSet: AnimatorSet? = null
    private val menuIcon: MenuIcon by lazy {
        when (menuIconType) {
            CircleMenu.MenuIconType.HAMBURGER -> HamburgerMenuIcon()
            CircleMenu.MenuIconType.PLUS -> PlusMenuIcon()
        }
    }

    init {
        backgroundTintList = ColorStateList.valueOf(backgroundColor)
        setImageDrawable(getIconDrawable(!isOpened))
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
    private fun startVectorAnimation(isOpening: Boolean) {
        val menuIcon = getIconDrawable(isOpening) as AnimatedVectorDrawable
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

    private fun getIconDrawable(isOpening: Boolean): Drawable {
        val iconResId = if (isLollipop()) {
            //animation from closed to opened
            if (isOpening) menuIcon.openingAnimatedIcon else menuIcon.closingAnimatedIcon
        } else {
            //if opening show close icon and vice versa
            if (isOpening) menuIcon.closeIcon else menuIcon.openIcon
        }

        val icon = ContextCompat.getDrawable(context, iconResId)!!
        icon.setTintCompat(iconColor)
        return icon
    }


}