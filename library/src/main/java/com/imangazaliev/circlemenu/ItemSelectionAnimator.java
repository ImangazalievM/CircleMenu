package com.imangazaliev.circlemenu;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

class ItemSelectionAnimator {

    private static final int SELECT_ANIMATION_DURATION = 550;
    private static final int EXIT_ANIMATION_DURATION = 600;

    private static final float START_CIRCLE_SIZE_RATIO = 1;
    static final float END_CIRCLE_SIZE_RATIO = 1.3f;

    private static final int START_CIRCLE_ANGLE = 1;
    private static final int END_CIRCLE_ANGLE = 360;

    private static final int ALPHA_TRANSPARENT = 0;
    private static final int ALPHA_OPAQUE = 255;

    private int circleColor, circleAlpha;
    private float startAngle, currentCircleAngle;

    private float originalCircleStrokeWidth, currentCircleStrokeWidth;
    private float originalCircleRadius, currentCircleRadius;
    private float menuCenterX, menuCenterY;
    private RectF circleRect = new RectF();

    private CircleMenuButton currentMenuButton;
    private Bitmap currentIconBitmap;
    private Rect iconSourceRect;
    private RectF iconRect = new RectF();
    private boolean isAnimating;

    private MenuController menuController;
    private MenuControllerListener controllerListener;

    ItemSelectionAnimator(Context context,
                          MenuController menuController,
                          MenuControllerListener controllerListener,
                          float menuCenterX,
                          float menuCenterY,
                          int circleRadius) {
        this.menuController = menuController;
        this.controllerListener = controllerListener;
        this.isAnimating = false;
        this.currentCircleAngle = START_CIRCLE_ANGLE;
        this.circleAlpha = ALPHA_OPAQUE;
        this.originalCircleRadius = circleRadius;
        this.currentCircleRadius = originalCircleRadius;
        this.originalCircleStrokeWidth = context.getResources().getDimension(R.dimen.circle_menu_button_size);
        this.menuCenterX = menuCenterX + originalCircleStrokeWidth / 2;
        this.menuCenterY = menuCenterY + originalCircleStrokeWidth / 2;
    }

    void startSelectAnimation(CircleMenuButton menuButton, float buttonAngle) {
        if (isAnimating) {
            return;
        }

        menuController.enableButtons(false);
        currentMenuButton = menuButton;
        circleColor = menuButton.getColorNormal();
        currentCircleStrokeWidth = originalCircleStrokeWidth;
        startAngle = buttonAngle;

        Drawable iconDrawable = menuButton.getDrawable();
        currentIconBitmap = getIconBitmap(iconDrawable);
        iconSourceRect = iconDrawable.getBounds();

        startCircleDrawingAnimation();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getIconBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat
                || drawable instanceof VectorDrawable) {
            return getBitmapFromVectorDrawable(drawable);
        } else {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void startCircleDrawingAnimation() {
        isAnimating = true;
        controllerListener.onSelectAnimationStart(currentMenuButton);

        ValueAnimator circleAngleAnimation = ValueAnimator.ofFloat(START_CIRCLE_ANGLE, END_CIRCLE_ANGLE);
        circleAngleAnimation.setDuration(SELECT_ANIMATION_DURATION);
        circleAngleAnimation.setInterpolator(new DecelerateInterpolator());
        circleAngleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentCircleAngle = (float) animation.getAnimatedValue();
                controllerListener.redrawView();

                if (currentCircleAngle == END_CIRCLE_ANGLE) {
                    menuController.showButtons(false);
                    startExitAnimation();
                }
            }


        });
        circleAngleAnimation.start();
    }

    private void startExitAnimation() {
        ValueAnimator circleSizeAnimation = ValueAnimator.ofFloat(START_CIRCLE_SIZE_RATIO, END_CIRCLE_SIZE_RATIO);
        circleSizeAnimation.setDuration(EXIT_ANIMATION_DURATION);
        circleSizeAnimation.setInterpolator(new DecelerateInterpolator());
        circleSizeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animationValue = (float) animation.getAnimatedValue();
                currentCircleRadius = originalCircleRadius * animationValue;
                currentCircleStrokeWidth = originalCircleStrokeWidth * animationValue;
                controllerListener.redrawView();

                if (animationValue == END_CIRCLE_SIZE_RATIO) {
                    currentCircleAngle = START_CIRCLE_ANGLE;
                    currentCircleRadius = originalCircleRadius;
                    currentCircleStrokeWidth = originalCircleStrokeWidth;
                    controllerListener.redrawView();


                    controllerListener.onSelectAnimationEnd(currentMenuButton);
                    menuController.setOpened(false);
                    currentMenuButton = null;
                    isAnimating = false;
                }
            }


        });

        ValueAnimator circleAlphaAnimation = ValueAnimator.ofInt(ALPHA_OPAQUE, ALPHA_TRANSPARENT);
        circleAlphaAnimation.setDuration(EXIT_ANIMATION_DURATION);
        circleAlphaAnimation.setInterpolator(new DecelerateInterpolator());
        circleAlphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleAlpha = (int) animation.getAnimatedValue();

                if (circleAlpha == ALPHA_TRANSPARENT) {
                    circleAlpha = ALPHA_OPAQUE;
                }
            }


        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(circleSizeAnimation);
        animatorSet.play(circleAlphaAnimation);
        animatorSet.start();
    }

    void onDraw(Canvas canvas) {
        if (!isAnimating) {
            return;
        }

        drawCircle(canvas);
        drawIcon(canvas);
    }

    private void drawCircle(Canvas canvas) {
        float left = menuCenterX - currentCircleRadius;
        float top = menuCenterY - currentCircleRadius;
        float right = menuCenterX + currentCircleRadius;
        float bottom = menuCenterY + currentCircleRadius;

        circleRect.set(left, top, right, bottom);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(circleColor);
        paint.setStrokeWidth(currentCircleStrokeWidth);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAlpha(circleAlpha);

        canvas.drawArc(circleRect, startAngle, currentCircleAngle, false, paint);
    }

    private void drawIcon(Canvas canvas) {
        if (currentIconBitmap == null || currentCircleAngle == END_CIRCLE_ANGLE) {
            return;
        }

        float angle = startAngle + currentCircleAngle;
        float centerX = Math.round((float) (menuCenterX - currentIconBitmap.getWidth() / 2.0));
        float centerY = Math.round((float) (menuCenterY - currentIconBitmap.getHeight() / 2.0));

        float left = Math.round((float) (centerX + originalCircleRadius * Math.cos(Math.toRadians(angle))));
        float top = Math.round((float) (centerY + originalCircleRadius * Math.sin(Math.toRadians(angle))));
        float right = left + iconSourceRect.right;
        float bottom = top + iconSourceRect.bottom;

        iconRect.set(left, top, right, bottom);
        canvas.drawBitmap(currentIconBitmap, iconSourceRect, iconRect, null);
    }

}
