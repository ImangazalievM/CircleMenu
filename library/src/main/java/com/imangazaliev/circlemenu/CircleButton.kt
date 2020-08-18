package com.imangazaliev.circlemenu

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.util.AttributeSet
import android.util.StateSet
import androidx.appcompat.widget.AppCompatImageButton

open class CircleButton @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null
) : AppCompatImageButton(context!!, attrs) {

    private val buttonSize: Int = resources.getDimension(R.dimen.circle_menu_button_size).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(buttonSize, buttonSize)
    }

    fun createBackgroundDrawable(colorNormal: Int, colorPressed: Int): StateListDrawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(android.R.attr.state_pressed), createCircleDrawable(colorPressed))
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), createCircleDrawable(colorNormal))
        drawable.addState(StateSet.WILD_CARD, createCircleDrawable(colorNormal))
        return drawable
    }

    private fun createCircleDrawable(color: Int): Drawable {
        val ovalDrawable = ShapeDrawable(OvalShape())
        val paint = ovalDrawable.paint
        paint.isAntiAlias = true
        paint.color = color
        return ovalDrawable
    }

    fun setBackgroundCompat(drawable: Drawable?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            background = drawable
        } else {
            setBackgroundDrawable(drawable)
        }
    }

}