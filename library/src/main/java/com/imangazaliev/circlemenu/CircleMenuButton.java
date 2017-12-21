package com.imangazaliev.circlemenu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class CircleMenuButton extends AppCompatImageButton {

    private static final Interpolator SWEEP_INTERPOLATOR = new DecelerateInterpolator();
    private static final int SWEEP_ANIMATOR_DURATION = 500;

    private String generateId;
    private int colorNormal;
    private int colorPressed;
    private int colorDisabled;
    private int iconId;
    private Drawable iconDrawable;
    private String hintText;
    private int buttonSize;
    private Object metaData;

    private boolean hasCenterButton;
    private ValueAnimator mValueAnimatorSweep;
    private float mCurrentSweepAngle = 360f;
    private RectF mRectF;
    private Paint mPaint;

    private Integer colorBorder;

    private boolean enableBorder;
    private boolean fullDrawable;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmapToRounded(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;

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
        iconId = attr.getResourceId(R.styleable.CircleMenuButton_icon, 0);
        enableBorder = attr.getBoolean(R.styleable.CircleMenuButton_enable_border, false);
        fullDrawable = attr.getBoolean(R.styleable.CircleMenuButton_full_drawable, false);

        hintText = attr.getString(R.styleable.CircleMenuButton_hintText);
        attr.recycle();

        buttonSize = (int) getDimension(R.dimen.circle_menu_button_size);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.GRAY);

        updateBackground();
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
        if (iconDrawable == null) {
            iconDrawable = iconId != 0 ? getResources().getDrawable(iconId) : new ColorDrawable(Color.TRANSPARENT);
        }
        return iconDrawable;
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

    private void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
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
     * @return the current Color for normal state.
     */
    public int getColorNormal() {
        return colorNormal;
    }

    public void setColorPressedResId(@ColorRes int colorPressedResId) {
        setColorPressed(getColor(colorPressedResId));
    }

    public void setColorPressed(int color) {
        if (colorPressed != color) {
            colorPressed = color;
            updateBackground();
        }
    }

    /**
     * @return the current color for pressed state.
     */
    public int getColorPressed() {
        return colorPressed;
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

    /**
     * @return the current color for disabled state.
     */
    public int getColorDisabled() {
        return colorDisabled;
    }

    public void setIconResId(@DrawableRes int iconResId) {
        if (this.iconId != iconResId) {
            this.iconId = iconResId;
            iconDrawable = null;
            updateBackground();
        }
    }

    public void setIconDrawable(@NonNull Drawable iconDrawable) {
        if (this.iconDrawable != iconDrawable) {
            iconId = 0;
            this.iconDrawable = iconDrawable;
            updateBackground();
        }
    }

    public void setHintText(@StringRes int hintTextResId) {
        this.hintText = getResources().getString(hintTextResId);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.getDrawable() != null && hasCenterButton == false) {
            Bitmap source = BitmapHelper.drawableToBitmap(this.getDrawable());
            Bitmap resizeBitmap = BitmapHelper.resizeBitmap(source, getWidth(), getHeight());

            if (fullDrawable) {
                BitmapHelper.transformCircularBitmap(canvas, resizeBitmap);
            } else {
                float radius = resizeBitmap.getWidth() / 2f;
                radius = radius - source.getWidth() / 2f;
                canvas.drawBitmap(source, radius , radius, mPaint);
            }
            this.mRectF = BitmapHelper.createRectFFromBitmap(resizeBitmap, 1);

            if (enableBorder) {
                addCircularBorder(canvas);
                addSecondCircularBorder(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private void addCircularBorder(Canvas canvas) {
        this.mPaint.setColor(getColorBorder() != null ? getColorBorder() : Color.WHITE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(this.mRectF, 0f, 360f, false, this.mPaint);
        } else {
            Path path = new Path();
            path.addArc(this.mRectF, 0f, 360f);
            canvas.drawPath(path, this.mPaint);
        }
    }

    private void addSecondCircularBorder(Canvas canvas) {
        this.mPaint.setColor(Color.GRAY);
        canvas.drawArc(mRectF, 0, mCurrentSweepAngle, false, mPaint);
    }

    public void startCheckAnimation() {
        mValueAnimatorSweep = ValueAnimator.ofFloat(360, 0);
        mValueAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mValueAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);

        mValueAnimatorSweep.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setCurrentSweepAngle((float)animation.getAnimatedValue());
                invalidate();
            }
        });

        mValueAnimatorSweep.start();
    }

    public void reverseCheckAnimation() {
        mPaint.setColor(Color.GRAY);
        mValueAnimatorSweep = ValueAnimator.ofFloat(0, 360);
        mValueAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mValueAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);

        mValueAnimatorSweep.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setCurrentSweepAngle((float)animation.getAnimatedValue());
                invalidate();
            }
        });

        mValueAnimatorSweep.start();
    }

    public void setCurrentSweepAngle(float currentSweepAngle) {
        mCurrentSweepAngle = currentSweepAngle;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    /**
     * @return text displayed when the button is pressed for a long time
     */
    public String getHintText() {
        return hintText;
    }

    public Object getMetaData() {
        return metaData;
    }

    public void setMetaData(Object metaData) {
        this.metaData = metaData;
    }

    public void setGenerateId(String generateId) {
        this.generateId = generateId;
    }

    public String getGenerateId() {
        return generateId;
    }

    public boolean isHasCenterButton() {
        return hasCenterButton;
    }

    public void setHasCenterButton(boolean hasCenterButton) {
        this.hasCenterButton = hasCenterButton;
    }

    public void setEnableBorder(boolean enableBorder) {
        this.enableBorder = enableBorder;
    }

    public void setFullDrawable(boolean fullDrawable) {
        this.fullDrawable = fullDrawable;
    }

    public void setColorBorder(Integer colorBorder) {
        this.colorBorder = colorBorder;
    }

    public Integer getColorBorder() {
        return colorBorder;
    }
}
