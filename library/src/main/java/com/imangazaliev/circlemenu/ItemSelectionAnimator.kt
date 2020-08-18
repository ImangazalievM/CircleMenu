package com.imangazaliev.circlemenu

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.view.animation.DecelerateInterpolator
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

internal class ItemSelectionAnimator(
        context: Context,
        private val menuController: MenuController,
        private val controllerListener: MenuControllerListener,
        menuCenterX: Float,
        menuCenterY: Float,
        circleRadius: Int
) {
    private var circleColor = 0
    private var circleAlpha: Int
    private var startAngle = 0f
    private var currentCircleAngle: Float
    private val originalCircleStrokeWidth: Float
    private var currentCircleStrokeWidth = 0f
    private val originalCircleRadius: Float
    private var currentCircleRadius: Float
    private val menuCenterX: Float
    private val menuCenterY: Float
    private val circleRect = RectF()
    private var currentMenuButton: CircleMenuButton? = null
    private var currentIconBitmap: Bitmap? = null
    private var iconSourceRect: Rect? = null
    private val iconRect = RectF()
    private var isAnimating = false

    fun startSelectAnimation(menuButton: CircleMenuButton, buttonAngle: Float) {
        if (isAnimating) {
            return
        }
        menuController.enableButtons(false)
        currentMenuButton = menuButton
        circleColor = menuButton.colorNormal
        currentCircleStrokeWidth = originalCircleStrokeWidth
        startAngle = buttonAngle
        val iconDrawable = menuButton.drawable
        currentIconBitmap = getIconBitmap(iconDrawable)
        iconSourceRect = iconDrawable.bounds
        startCircleDrawingAnimation()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getIconBitmap(drawable: Drawable): Bitmap? {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is VectorDrawableCompat
                || drawable is VectorDrawable) {
            getBitmapFromVectorDrawable(drawable)
        } else {
            null
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmapFromVectorDrawable(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun startCircleDrawingAnimation() {
        isAnimating = true
        controllerListener.onSelectAnimationStart(currentMenuButton)
        val circleAngleAnimation = ValueAnimator.ofFloat(START_CIRCLE_ANGLE.toFloat(), END_CIRCLE_ANGLE.toFloat())
        circleAngleAnimation.duration = SELECT_ANIMATION_DURATION.toLong()
        circleAngleAnimation.interpolator = DecelerateInterpolator()
        circleAngleAnimation.addUpdateListener { animation ->
            currentCircleAngle = animation.animatedValue as Float
            controllerListener.redrawView()
            if (currentCircleAngle == END_CIRCLE_ANGLE.toFloat()) {
                menuController.showButtons(false)
                startExitAnimation()
            }
        }
        circleAngleAnimation.start()
    }

    private fun startExitAnimation() {
        val circleSizeAnimation = ValueAnimator.ofFloat(START_CIRCLE_SIZE_RATIO, END_CIRCLE_SIZE_RATIO)
        circleSizeAnimation.duration = EXIT_ANIMATION_DURATION.toLong()
        circleSizeAnimation.interpolator = DecelerateInterpolator()
        circleSizeAnimation.addUpdateListener { animation ->
            val animationValue = animation.animatedValue as Float
            currentCircleRadius = originalCircleRadius * animationValue
            currentCircleStrokeWidth = originalCircleStrokeWidth * animationValue
            controllerListener.redrawView()
            if (animationValue == END_CIRCLE_SIZE_RATIO) {
                currentCircleAngle = START_CIRCLE_ANGLE.toFloat()
                currentCircleRadius = originalCircleRadius
                currentCircleStrokeWidth = originalCircleStrokeWidth
                controllerListener.redrawView()
                controllerListener.onSelectAnimationEnd(currentMenuButton)
                menuController.isOpened = false
                currentMenuButton = null
                isAnimating = false
            }
        }
        val circleAlphaAnimation = ValueAnimator.ofInt(ALPHA_OPAQUE, ALPHA_TRANSPARENT)
        circleAlphaAnimation.duration = EXIT_ANIMATION_DURATION.toLong()
        circleAlphaAnimation.interpolator = DecelerateInterpolator()
        circleAlphaAnimation.addUpdateListener { animation ->
            circleAlpha = animation.animatedValue as Int
            if (circleAlpha == ALPHA_TRANSPARENT) {
                circleAlpha = ALPHA_OPAQUE
            }
        }
        val animatorSet = AnimatorSet()
        animatorSet.play(circleSizeAnimation)
        animatorSet.play(circleAlphaAnimation)
        animatorSet.start()
    }

    fun onDraw(canvas: Canvas) {
        if (!isAnimating) {
            return
        }
        drawCircle(canvas)
        drawIcon(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        val left = menuCenterX - currentCircleRadius
        val top = menuCenterY - currentCircleRadius
        val right = menuCenterX + currentCircleRadius
        val bottom = menuCenterY + currentCircleRadius
        circleRect[left, top, right] = bottom
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = circleColor
        paint.strokeWidth = currentCircleStrokeWidth
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.alpha = circleAlpha
        canvas.drawArc(circleRect, startAngle, currentCircleAngle, false, paint)
    }

    private fun drawIcon(canvas: Canvas) {
        if (currentIconBitmap == null || currentCircleAngle == END_CIRCLE_ANGLE.toFloat()) {
            return
        }
        val angle = startAngle + currentCircleAngle
        val centerX = (menuCenterX - currentIconBitmap!!.width / 2.0).toFloat().roundToInt().toFloat()
        val centerY = (menuCenterY - currentIconBitmap!!.height / 2.0).toFloat().roundToInt().toFloat()
        val left = (centerX + originalCircleRadius * cos(Math.toRadians(angle.toDouble()))).toFloat().roundToInt().toFloat()
        val top = (centerY + originalCircleRadius * sin(Math.toRadians(angle.toDouble()))).toFloat().roundToInt().toFloat()
        val right = left + iconSourceRect!!.right
        val bottom = top + iconSourceRect!!.bottom
        iconRect[left, top, right] = bottom
        canvas.drawBitmap(currentIconBitmap!!, iconSourceRect, iconRect, null)
    }

    companion object {
        private const val SELECT_ANIMATION_DURATION = 550
        private const val EXIT_ANIMATION_DURATION = 600
        private const val START_CIRCLE_SIZE_RATIO = 1f
        const val END_CIRCLE_SIZE_RATIO = 1.3f
        private const val START_CIRCLE_ANGLE = 1
        private const val END_CIRCLE_ANGLE = 360
        private const val ALPHA_TRANSPARENT = 0
        private const val ALPHA_OPAQUE = 255
    }

    init {
        currentCircleAngle = START_CIRCLE_ANGLE.toFloat()
        circleAlpha = ALPHA_OPAQUE
        originalCircleRadius = circleRadius.toFloat()
        currentCircleRadius = originalCircleRadius
        originalCircleStrokeWidth = context.resources.getDimension(R.dimen.circle_menu_button_size)
        this.menuCenterX = menuCenterX + originalCircleStrokeWidth / 2
        this.menuCenterY = menuCenterY + originalCircleStrokeWidth / 2
    }
}