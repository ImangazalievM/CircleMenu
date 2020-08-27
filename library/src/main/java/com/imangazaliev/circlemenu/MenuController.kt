package com.imangazaliev.circlemenu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.View.OnLongClickListener
import android.view.animation.DecelerateInterpolator
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

internal class MenuController(
        context: Context?,
        private val buttons: List<CircleMenuButton>,
        private val listener: Listener,
        private val startAngle: Float,
        private val maxAngle: Float,
        private val distance: Int,
        private val showSelectAnimation: Boolean,
        var isOpened: Boolean
) {

    companion object {
        private const val TOGGLE_ANIMATION_DURATION = 200
    }

    private var menuCenterX: Float = 0f
    private var menuCenterY: Float = 0f
    private val itemSelectionAnimator: ItemSelectionAnimator = ItemSelectionAnimator(
            context = context!!,
            menuController = this,
            controllerListener = listener,
            circleRadius = distance
    )

    init {
        val onButtonItemClickListener = View.OnClickListener { v ->
            val menuButton = v as CircleMenuButton
            if (showSelectAnimation) {
                val buttonAngle = maxAngle / buttons.size * buttons.indexOf(menuButton) + startAngle
                val buttonIndex = buttons.indexOf(menuButton)
                itemSelectionAnimator.startSelectAnimation(menuButton, buttonIndex, buttonAngle)
            } else {
                close(true)
            }
            listener.onButtonClick(menuButton, buttons.indexOf(menuButton))
        }
        val onButtonItemLongClickListener = OnLongClickListener { v ->
            val menuButton = v as CircleMenuButton
            listener.onButtonLongClick(menuButton, buttons.indexOf(menuButton))
            true
        }
        for (menuButton in buttons) {
            menuButton.setOnClickListener(onButtonItemClickListener)
            menuButton.setOnLongClickListener(onButtonItemLongClickListener)
        }
        showButtons(isOpened)
        layoutButtons(if (isOpened) distance.toFloat() else 0.toFloat())
    }

    fun onDraw(canvas: Canvas) {
        itemSelectionAnimator.onDraw(canvas)
    }

    fun toggle() {
        if (isOpened) {
            close(true)
        } else {
            open(true)
        }
    }

    fun open(animate: Boolean) {
        if (isOpened) {
            return
        }
        enableButtons(false)
        layoutButtons(0f)
        showButtons(true)
        listener.onOpenAnimationStart()
        val buttonAnimator = ValueAnimator.ofFloat(0f, distance.toFloat())
        buttonAnimator.duration = if (animate) TOGGLE_ANIMATION_DURATION.toLong() else 0.toLong()
        buttonAnimator.interpolator = DecelerateInterpolator()
        buttonAnimator.addUpdateListener { animation -> layoutButtons(animation.animatedValue as Float) }
        buttonAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isOpened = true
                enableButtons(true)
                listener.onOpenAnimationEnd()
            }
        })
        buttonAnimator.start()
    }

    fun close(animate: Boolean) {
        if (!isOpened) {
            return
        }
        enableButtons(false)
        layoutButtons(distance.toFloat())
        listener.onCloseAnimationStart()
        val buttonAnimator = ValueAnimator.ofFloat(distance.toFloat(), 0f)
        buttonAnimator.duration = if (animate) TOGGLE_ANIMATION_DURATION.toLong() else 0.toLong()
        buttonAnimator.interpolator = DecelerateInterpolator()
        buttonAnimator.addUpdateListener { animation -> layoutButtons(animation.animatedValue as Float) }
        buttonAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isOpened = false
                showButtons(false)
                listener.onCloseAnimationEnd()
            }
        })
        buttonAnimator.start()
    }

    private fun layoutButtons(distance: Float) {
        val buttonsCount = buttons.size
        val angleStep = maxAngle / buttonsCount
        var lastAngle = startAngle
        for (i in 0 until buttonsCount) {
            val button = buttons[i]
            val x = (menuCenterX + distance * cos(Math.toRadians(lastAngle.toDouble()))).toFloat().roundToInt().toFloat()
            val y = (menuCenterY + distance * sin(Math.toRadians(lastAngle.toDouble()))).toFloat().roundToInt().toFloat()
            button.x = x
            button.y = y
            if (lastAngle > maxAngle) {
                lastAngle -= maxAngle
            }
            lastAngle += angleStep
        }
    }

    fun enableButtons(enabled: Boolean) {
        for (button in buttons) {
            button.isEnabled = enabled
        }
    }

    fun showButtons(visible: Boolean) {
        for (button in buttons) {
            button.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        }
    }

    fun setCenterButtonPosition(centerButtonX: Float, centerButtonY: Float) {
        this.menuCenterX = centerButtonX
        this.menuCenterY = centerButtonY
        itemSelectionAnimator.setCenterButtonPosition(menuCenterX, menuCenterY)
        layoutButtons(if (isOpened) distance.toFloat() else 0.toFloat())
    }

    internal interface Listener {

        fun onButtonClick(menuButton: CircleMenuButton, index: Int)

        fun onButtonLongClick(menuButton: CircleMenuButton, index: Int)

        fun onOpenAnimationStart()

        fun onOpenAnimationEnd()

        fun onCloseAnimationStart()

        fun onCloseAnimationEnd()

        fun onSelectAnimationStart(buttonIndex: Int)

        fun onSelectAnimationEnd(buttonIndex: Int)

        fun redrawView()

    }

}