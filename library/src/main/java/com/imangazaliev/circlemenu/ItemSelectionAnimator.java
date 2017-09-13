package com.imangazaliev.circlemenu;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.view.animation.DecelerateInterpolator;

class ItemSelectionAnimator {

    private static final int SELECT_ANIMATION_DURATION = 550;
    private static final int EXIT_ANIMATION_DURATION = 600;

    private static final float START_CIRCLE_SIZE = 1;
    private static final float END_CIRCLE_SIZE = 1.3f;

    private static final int START_CIRCLE_ANGLE = 1;
    private static final int END_CIRCLE_ANGLE = 360;

    private static final int ALPHA_TRANSPARENT = 0;
    private static final int ALPHA_OPAQUE = 255;

    interface AnimationDrawController {
        void redrawView();
    }

    private int circleColor, circleAlpha;
    private float startAngle, currentCircleAngle;

    private float originalCircleStrokeWidth, currentCircleStrokeWidth;
    private float originalCircleRadius, currentCircleRadius;
    private int menuWidth, menuHeight;
    private RectF circleRect = new RectF();

    private Bitmap currentIconBitmap;
    private Rect iconSourceRect;
    private RectF iconRect = new RectF();

    private boolean animationIsActive;

    private MenuController menuController;
    private AnimationDrawController animationDrawController;

    public ItemSelectionAnimator(MenuController menuController, AnimationDrawController animationDrawController) {
        this.menuController = menuController;
        this.animationDrawController = animationDrawController;
        this.animationIsActive = false;
        this.currentCircleAngle = START_CIRCLE_ANGLE;
        this.circleAlpha = ALPHA_OPAQUE;
    }

    public void setCircleRadius(float circleRadius, int menuWidth, int menuHeight) {
        this.originalCircleRadius = circleRadius;
        this.currentCircleRadius = originalCircleRadius;
        this.menuWidth = menuWidth;
        this.menuHeight = menuHeight;
    }

    public void onItemClick(CircleMenuButton menuButton, MenuButtonPoint menuButtonPoint) {
        circleColor = menuButton.getColorNormal();
        originalCircleStrokeWidth = menuButton.getWidth();
        currentCircleStrokeWidth = originalCircleStrokeWidth;
        startAngle = menuButtonPoint.angle;

        Drawable iconDrawable = menuButton.getDrawable();
        currentIconBitmap = getIconBitmap(iconDrawable);
        iconSourceRect = iconDrawable.getBounds();

        startCircleDrawingAnimation();
    }

    private Bitmap getIconBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmapFromVectorDrawable((VectorDrawable) drawable);
        } else {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getBitmapFromVectorDrawable(VectorDrawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void startCircleDrawingAnimation() {
        onAnimationStarted();
        menuController.setState(MenuState.SELECT_ANIMATION_STARTED);

        ValueAnimator circleAngleAnimation = ValueAnimator.ofFloat(START_CIRCLE_ANGLE, END_CIRCLE_ANGLE);
        circleAngleAnimation.setDuration(SELECT_ANIMATION_DURATION);
        circleAngleAnimation.setInterpolator(new DecelerateInterpolator());
        circleAngleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentCircleAngle = (float) animation.getAnimatedValue();
                animationDrawController.redrawView();

                if (currentCircleAngle == END_CIRCLE_ANGLE) {
                    menuController.setState(MenuState.SELECT_ANIMATION_FINISHED);
                    startExitAnimation();
                }
            }


        });
        circleAngleAnimation.start();
    }

    private void startExitAnimation() {
        menuController.setState(MenuState.EXIT_ANIMATION_STARTED);

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator circleSizeAnimation = ValueAnimator.ofFloat(START_CIRCLE_SIZE, END_CIRCLE_SIZE);
        circleSizeAnimation.setDuration(EXIT_ANIMATION_DURATION);
        circleSizeAnimation.setInterpolator(new DecelerateInterpolator());
        circleSizeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animationValue = (float) animation.getAnimatedValue();
                currentCircleRadius = originalCircleRadius * animationValue;
                currentCircleStrokeWidth = originalCircleStrokeWidth * animationValue;
                animationDrawController.redrawView();

                if (animationValue == END_CIRCLE_SIZE) {
                    currentCircleAngle = START_CIRCLE_ANGLE;
                    currentCircleRadius = originalCircleRadius;
                    currentCircleStrokeWidth = originalCircleStrokeWidth;
                    animationDrawController.redrawView();

                    menuController.setState(MenuState.EXIT_ANIMATION_FINISHED);
                    onAnimationFinished();
                }
            }


        });
        animatorSet.play(circleSizeAnimation);

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
        animatorSet.play(circleAlphaAnimation);

        animatorSet.start();
    }

    private void onAnimationStarted() {
        animationIsActive = true;
    }

    private void onAnimationFinished() {
        animationIsActive = false;
    }

    void onDraw(Canvas canvas) {
        if (!animationIsActive) {
            return;
        }

        drawCircle(canvas);
        drawIcon(canvas);
    }

    private void drawCircle(Canvas canvas) {
        int horizontalCenter = menuWidth / 2;
        int verticalCenter = menuHeight / 2;

        int left = (int) (horizontalCenter - currentCircleRadius);
        int top = (int) (verticalCenter - currentCircleRadius);
        int right = (int) (horizontalCenter + currentCircleRadius);
        int bottom = (int) (verticalCenter + currentCircleRadius);

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
        float centerX = Math.round((float) ((menuWidth / 2.0) - currentIconBitmap.getWidth() / 2.0));
        float centerY = Math.round((float) ((menuHeight / 2.0) - currentIconBitmap.getHeight() / 2.0));

        float left = Math.round((float) (centerX + originalCircleRadius * Math.cos(Math.toRadians(angle))));
        float top = Math.round((float) (centerY + originalCircleRadius * Math.sin(Math.toRadians(angle))));
        float right = left + iconSourceRect.right;
        float bottom = top + iconSourceRect.bottom;

        iconRect.set(left, top, right, bottom);
        canvas.drawBitmap(currentIconBitmap, iconSourceRect, iconRect, null);
    }

}
