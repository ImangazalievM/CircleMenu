package com.imangazaliev.circlemenu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class CenterMenuButton(context: Context?, private var isOpened: Boolean) : CircleButton(context) {

    private var preLollipopAnimationSet: AnimatorSet? = null

    fun setOpened(isOpened: Boolean) {
        this.isOpened = isOpened
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startVectorAnimation(isOpened)
        } else {
            startPreLollipopAnimation()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun startVectorAnimation(isOpened: Boolean) {
        val iconId = if (isOpened) R.drawable.ic_menu_animated else R.drawable.ic_close_animated
        val menuIcon = ContextCompat.getDrawable(context, iconId) as AnimatedVectorDrawable?
        setImageDrawable(menuIcon)
        menuIcon!!.start()
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
                setImageResource(if (isOpened) R.drawable.ic_close_vector else R.drawable.ic_menu_vector)
            }
        })
        preLollipopAnimationSet.play(scaleOutX).with(scaleOutY)
        preLollipopAnimationSet.play(scaleInX).with(scaleInY).after(scaleOutX)
        preLollipopAnimationSet.interpolator = OvershootInterpolator(2f)
        return preLollipopAnimationSet
    }

    init {
        val colorNormal = resources.getColor(R.color.circle_menu_center_button_color_normal)
        val colorPressed = resources.getColor(R.color.circle_menu_center_button_color_pressed)
        setBackgroundCompat(createBackgroundDrawable(colorNormal, colorPressed))
        setImageResource(if (isOpened) R.drawable.ic_close_vector else R.drawable.ic_menu_vector)

        //setVisibility(View.INVISIBLE);
    }

}