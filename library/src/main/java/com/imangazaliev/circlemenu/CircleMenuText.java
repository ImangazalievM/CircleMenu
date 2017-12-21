package com.imangazaliev.circlemenu;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author NetoDevel
 */
public class CircleMenuText extends LinearLayout {

    /* Attributes */
    private CircleMenuButton circleMenuButton;
    private TextView textTitle;

    public CircleMenuText(Context context) {
        super(context);
        init(context);
    }

    public CircleMenuText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleMenuText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleMenuText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setTitle(String text) {
        textTitle.setText(text);
        textTitle.requestLayout();
    }

    public void setTitleColor(Integer color) {
        textTitle.setTextColor(color);
        textTitle.requestLayout();
    }

    private void init(Context context) {
        setVisibility(INVISIBLE);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        circleMenuButton = new CircleMenuButton(context);

        circleMenuButton.setId(10 + 1);
        circleMenuButton.setColorNormal(R.color.color_normal);
        circleMenuButton.setColorPressed(R.color.color_pressed);
        circleMenuButton.setIconResId(R.drawable.ic_favorite);
        circleMenuButton.setClickable(false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        addView(circleMenuButton, params);

        textTitle = new TextView(context);
        textTitle.setText("");
        textTitle.setClickable(false);
        textTitle.setTextColor(Color.WHITE);
        textTitle.setTextSize(13);

        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(textTitle, textParams);
    }

    public CircleMenuButton getCircleMenuButton() {
        return circleMenuButton;
    }

    public void setMetaData(Object objectData) {
        this.getCircleMenuButton().setMetaData(objectData);
    }

    public void setIconResId(Integer resId) {
        this.getCircleMenuButton().setIconResId(resId);
    }

    public void setFullDrawable(Boolean value) {
        this.getCircleMenuButton().setFullDrawable(value);
    }

    public void setEnableBorder(Boolean value) {
        this.getCircleMenuButton().setEnableBorder(value);
    }

    public void setColorBorder(Integer color) {
        this.getCircleMenuButton().setColorBorder(color);
    }

}
