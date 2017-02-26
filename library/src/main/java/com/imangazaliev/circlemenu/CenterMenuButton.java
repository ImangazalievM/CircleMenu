package com.imangazaliev.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.OvershootInterpolator;

public class CenterMenuButton extends CircleMenuButton {

    private boolean mExpanded;
    private AnimatorSet preLollipopAnimationSet;

    public CenterMenuButton(Context context) {
        super(context);

        this.mExpanded = false;
    }

    @Override
    void updateBackground() {
        setColorNormalResId(R.color.circle_menu_center_button_color_normal);
        setColorPressedResId(R.color.circle_menu_center_button_color_pressed);
        super.updateBackground();
    }

    @Override
    Drawable getIconDrawable() {
        if (isVectorAnimationSupported()) {
            return ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_vector);
        } else {
            return VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_vector, getContext().getTheme());
        }
    }

    public void setExpanded(boolean isExpanded) {
        mExpanded = isExpanded;
        if (isVectorAnimationSupported()) {
            startVectorAnimation(isExpanded);
        } else {
            startPreLollipopAnimation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startVectorAnimation(boolean isExpanded) {
        int iconId = isExpanded ? R.drawable.ic_menu_animated : R.drawable.ic_close_animated;
        AnimatedVectorDrawable menuIcon = (AnimatedVectorDrawable) ContextCompat.getDrawable(getContext(), iconId);
        setImageDrawable(menuIcon);
        menuIcon.start();
    }

    private void startPreLollipopAnimation() {
        if (preLollipopAnimationSet == null) {
            preLollipopAnimationSet = createPreLollipopIconAnimation();
        }

        preLollipopAnimationSet.start();
    }

    private AnimatorSet createPreLollipopIconAnimation() {
        AnimatorSet preLollipopAnimationSet = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(getDrawable(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(getDrawable(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(this, "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(this, "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                CenterMenuButton.this.setImageResource(mExpanded ? R.drawable.ic_close_vector : R.drawable.ic_menu_vector);
            }
        });

        preLollipopAnimationSet.play(scaleOutX).with(scaleOutY);
        preLollipopAnimationSet.play(scaleInX).with(scaleInY).after(scaleOutX);
        preLollipopAnimationSet.setInterpolator(new OvershootInterpolator(2));
        return preLollipopAnimationSet;
    }

    private boolean isVectorAnimationSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
