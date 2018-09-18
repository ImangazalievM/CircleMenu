package com.imangazaliev.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.animation.OvershootInterpolator;

public class CenterMenuButton extends CircleButton {

    private boolean isOpened;
    private AnimatorSet preLollipopAnimationSet;

    public  CenterMenuButton(Context context, boolean isOpened) {
        super(context);

        this.isOpened = isOpened;
        int colorNormal = getResources().getColor(R.color.circle_menu_center_button_color_normal);
        int colorPressed = getResources().getColor(R.color.circle_menu_center_button_color_pressed);

        setBackgroundCompat(createBackgroundDrawable(colorNormal, colorPressed));
        setImageResource(isOpened ? R.drawable.ic_close_vector : R.drawable.ic_menu_vector);

        //setVisibility(View.INVISIBLE);
    }

    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
        if (isVectorAnimationSupported()) {
            startVectorAnimation(isOpened);
        } else {
            startPreLollipopAnimation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startVectorAnimation(boolean isOpened) {
        int iconId = isOpened ? R.drawable.ic_menu_animated : R.drawable.ic_close_animated;
        AnimatedVectorDrawable menuIcon = (AnimatedVectorDrawable) ContextCompat.getDrawable(getContext(), iconId);
        setImageDrawable(menuIcon);
        if (menuIcon != null) {
            menuIcon.start();
        }
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
                setImageResource(isOpened ? R.drawable.ic_close_vector : R.drawable.ic_menu_vector);
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
