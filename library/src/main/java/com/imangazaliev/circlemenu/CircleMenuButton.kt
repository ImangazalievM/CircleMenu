package com.imangazaliev.circlemenu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet

class CircleMenuButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : CircleButton(context, attrs) {

    val colorNormal: Int
    val hintText: String?

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.CircleMenuButton, 0, 0)
        colorNormal = attr.getColor(R.styleable.CircleMenuButton_colorNormal, resources.getColor(R.color.circle_menu_button_color_normal))
        val colorPressed = attr.getColor(R.styleable.CircleMenuButton_colorPressed, resources.getColor(R.color.circle_menu_button_color_pressed))
        hintText = attr.getString(R.styleable.CircleMenuButton_hintText)
        val iconId = attr.getResourceId(R.styleable.CircleMenuButton_icon, 0)
        attr.recycle()
        setBackgroundCompat(createBackgroundDrawable(colorNormal, colorPressed))
        if (iconId != 0) {
            setImageResource(iconId)
        } else {
            setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}