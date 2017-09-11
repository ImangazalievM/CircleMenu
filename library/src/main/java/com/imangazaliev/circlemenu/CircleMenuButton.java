package com.imangazaliev.circlemenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

public class CircleMenuButton extends AppCompatImageButton {

    private int colorNormal;
    private int colorPressed;
    private int colorDisabled;

    @DrawableRes
    private int icon;
    private Drawable iconDrawable;

    private int buttonSize;

    public CircleMenuButton(Context context) {
        this(context, null);
    }

    public CircleMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleMenuButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attributeSet) {
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.CircleMenuButton, 0, 0);
        colorNormal = attr.getColor(R.styleable.CircleMenuButton_colorNormal, getColor(R.color.circle_menu_button_color_normal));
        colorPressed = attr.getColor(R.styleable.CircleMenuButton_colorPressed, getColor(R.color.circle_menu_button_color_pressed));
        colorDisabled = attr.getColor(R.styleable.CircleMenuButton_colorDisabled, getColor(R.color.circle_menu_button_color_disabled));
        icon = attr.getResourceId(R.styleable.CircleMenuButton_icon, 0);
        attr.recycle();

        buttonSize = (int) getDimension(R.dimen.circle_menu_button_size);

        updateBackground();
    }


    public void setIcon(@DrawableRes int icon) {
        if (this.icon != icon) {
            this.icon = icon;
            iconDrawable = null;
            updateBackground();
        }
    }

    public void setIconDrawable(@NonNull Drawable iconDrawable) {
        if (this.iconDrawable != iconDrawable) {
            icon = 0;
            this.iconDrawable = iconDrawable;
            updateBackground();
        }
    }

    /**
     * @return the current Color for normal state.
     */
    public int getColorNormal() {
        return colorNormal;
    }

    public void setColorNormalResId(@ColorRes int colorNormal) {
        setColorNormal(getColor(colorNormal));
    }

    public void setColorNormal(int color) {
        if (colorNormal != color) {
            colorNormal = color;
            updateBackground();
        }
    }

    /**
     * @return the current color for pressed state.
     */
    public int getColorPressed() {
        return colorPressed;
    }

    public void setColorPressedResId(@ColorRes int colorPressed) {
        setColorPressed(getColor(colorPressed));
    }

    public void setColorPressed(int color) {
        if (colorPressed != color) {
            colorPressed = color;
            updateBackground();
        }
    }

    /**
     * @return the current color for disabled state.
     */
    public int getColorDisabled() {
        return colorDisabled;
    }

    public void setColorDisabledResId(@ColorRes int colorDisabled) {
        setColorDisabled(getColor(colorDisabled));
    }

    public void setColorDisabled(int color) {
        if (colorDisabled != color) {
            colorDisabled = color;
            updateBackground();
        }
    }

    int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(buttonSize, buttonSize);
    }

    void updateBackground() {
        setBackgroundCompat(createBackgroundDrawable());
        setImageDrawable(getIconDrawable());
    }

    Drawable getIconDrawable() {
        if (iconDrawable != null) {
            return iconDrawable;
        } else if (icon != 0) {
            return getResources().getDrawable(icon);
        } else {
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

    private StateListDrawable createBackgroundDrawable() {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, createCircleDrawable(colorDisabled));
        drawable.addState(new int[]{android.R.attr.state_pressed}, createCircleDrawable(colorPressed));
        drawable.addState(new int[]{}, createCircleDrawable(colorNormal));
        return drawable;
    }

    private Drawable createCircleDrawable(int color) {
        ShapeDrawable fillDrawable = new ShapeDrawable(new OvalShape());
        final Paint paint = fillDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        return fillDrawable;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

}
