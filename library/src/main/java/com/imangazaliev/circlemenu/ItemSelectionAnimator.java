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
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

public class ItemSelectionAnimator {

    public static final int SELECT_ANIMATION_DURATION = 550;
    public static final int EXIT_ANIMATION_DURATION = 600;

    private static final float START_CIRCLE_SIZE = 1;
    private static final float END_CIRCLE_SIZE = 1.3f;

    private static final int START_CIRCLE_ANGLE = 1;
    private static final int END_CIRCLE_ANGLE = 360;

    private static final int ALPHA_TRANSPARENT = 0;
    private static final int ALPHA_OPAQUE = 255;

    public interface AnimationDrawController {
        void redrawView();
    }

    private int mCircleColor, mCircleAlpha;
    private float mStartAngle, mCurrentCircleAngle;

    private float mOriginalCircleStrokeWidth, mCurrentCircleStrokeWidth;
    private float mOriginalCircleRadius, mCurrentCircleRadius;
    private int mMenuWidth, mMenuHeight;
    private RectF mCircleRect = new RectF();

    private Bitmap mCurrentIconBitmap;
    private Rect mIconSourceRect;
    private RectF mIconRect = new RectF();

    private boolean mAnimationIsActive;

    private MenuController mMenuController;
    private AnimationDrawController mAnimationDrawController;

    public ItemSelectionAnimator(MenuController menuController, AnimationDrawController animationDrawController) {
        this.mMenuController = menuController;
        this.mAnimationDrawController = animationDrawController;
        this.mAnimationIsActive = false;
        this.mCurrentCircleAngle = START_CIRCLE_ANGLE;
        this.mCircleAlpha = ALPHA_OPAQUE;
    }

    public void setCircleRadius(float circleRadius, int menuWidth, int menuHeight) {
        this.mOriginalCircleRadius = circleRadius;
        this.mCurrentCircleRadius = mOriginalCircleRadius;
        this.mMenuWidth = menuWidth;
        this.mMenuHeight = menuHeight;
    }

    public void onItemClick(CircleMenuButton menuButton, MenuButtonPoint menuButtonPoint) {
        mCircleColor = menuButton.getColorNormal();
        mOriginalCircleStrokeWidth = menuButton.getWidth();
        mCurrentCircleStrokeWidth = mOriginalCircleStrokeWidth;
        mStartAngle = menuButtonPoint.angle;

        Drawable iconDrawable = menuButton.getDrawable();
        mCurrentIconBitmap = getIconBitmap(iconDrawable);
        mIconSourceRect = iconDrawable.getBounds();

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
        mMenuController.setState(MenuState.SELECT_ANIMATION_STARTED);

        ValueAnimator circleAngleAnimation = ValueAnimator.ofFloat(START_CIRCLE_ANGLE, END_CIRCLE_ANGLE);
        circleAngleAnimation.setDuration(SELECT_ANIMATION_DURATION);
        circleAngleAnimation.setInterpolator(new DecelerateInterpolator());
        circleAngleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentCircleAngle = (float) animation.getAnimatedValue();
                mAnimationDrawController.redrawView();

                if (mCurrentCircleAngle == END_CIRCLE_ANGLE) {
                    mMenuController.setState(MenuState.SELECT_ANIMATION_FINISHED);
                    startExitAnimation();
                }


            }


        });
        circleAngleAnimation.start();
    }

    private void startExitAnimation() {
        mMenuController.setState(MenuState.EXIT_ANIMATION_STARTED);

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator circleSizeAnimation = ValueAnimator.ofFloat(START_CIRCLE_SIZE, END_CIRCLE_SIZE);
        circleSizeAnimation.setDuration(EXIT_ANIMATION_DURATION);
        circleSizeAnimation.setInterpolator(new DecelerateInterpolator());
        circleSizeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animationValue = (float) animation.getAnimatedValue();
                mCurrentCircleRadius = mOriginalCircleRadius * animationValue;
                mCurrentCircleStrokeWidth = mOriginalCircleStrokeWidth * animationValue;
                mAnimationDrawController.redrawView();

                if (animationValue == END_CIRCLE_SIZE) {
                    mCurrentCircleAngle = START_CIRCLE_ANGLE;
                    mCurrentCircleRadius = mOriginalCircleRadius;
                    mCurrentCircleStrokeWidth = mOriginalCircleStrokeWidth;
                    mAnimationDrawController.redrawView();

                    mMenuController.setState(MenuState.EXIT_ANIMATION_FINISHED);
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
                mCircleAlpha = (int) animation.getAnimatedValue();

                if (mCircleAlpha == ALPHA_TRANSPARENT) {
                    mCircleAlpha = ALPHA_OPAQUE;
                }
            }


        });
        animatorSet.play(circleAlphaAnimation);

        animatorSet.start();
    }

    private void onAnimationStarted() {
        mAnimationIsActive = true;
    }

    private void onAnimationFinished() {
        mAnimationIsActive = false;
    }

    public void onDraw(Canvas canvas) {
        if (!mAnimationIsActive) {
            return;
        }

        drawCircle(canvas);
        drawIcon(canvas);
    }

    private void drawCircle(Canvas canvas) {
        int horizontalCenter = mMenuWidth / 2;
        int verticalCenter = mMenuHeight / 2;

        int left = (int) (horizontalCenter - mCurrentCircleRadius);
        int top = (int) (verticalCenter - mCurrentCircleRadius);
        int right = (int) (horizontalCenter + mCurrentCircleRadius);
        int bottom = (int) (verticalCenter + mCurrentCircleRadius);

        mCircleRect.set(left, top, right, bottom);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mCircleColor);
        paint.setStrokeWidth(mCurrentCircleStrokeWidth);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAlpha(mCircleAlpha);

        canvas.drawArc(mCircleRect, mStartAngle, mCurrentCircleAngle, false, paint);
    }

    private void drawIcon(Canvas canvas) {
        if (mCurrentIconBitmap == null || mCurrentCircleAngle == END_CIRCLE_ANGLE) {
            return;
        }

        float iconPadding = mOriginalCircleRadius + mOriginalCircleStrokeWidth / 2;
        float left = iconPadding + Math.round((mOriginalCircleStrokeWidth / 2.0) + mOriginalCircleRadius * Math.cos(Math.toRadians(mStartAngle + mCurrentCircleAngle)));
        float top = iconPadding + Math.round((mOriginalCircleStrokeWidth / 2.0) + mOriginalCircleRadius * Math.sin(Math.toRadians(mStartAngle + mCurrentCircleAngle)));

        left += mIconSourceRect.right / 2;
        top += mIconSourceRect.bottom / 2;

        float right = left + mIconSourceRect.right;
        float bottom = top + mIconSourceRect.bottom;

        mIconRect.set(left, top, right, bottom);
        canvas.drawBitmap(mCurrentIconBitmap, mIconSourceRect, mIconRect, null);
    }

}
