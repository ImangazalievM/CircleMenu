package com.imangazaliev.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuController {

    private static final int ANIMATION_DELAY = 100;
    private static final int ANIMATION_DURATION = 200;

    public interface ControllerListener {
        void onStartCollapsing();

        void onCollapsed();

        void onStartExpanding();

        void onExpanded();

        void onSelectAnimationStarted();

        void onSelectAnimationFinished();

        void onExitAnimationStarted();

        void onExitAnimationFinished();

        void onItemClick(MenuButton menuButton);
    }

    private List<MenuButton> mButtons = new ArrayList<>();
    private HashMap<MenuButton, MenuButtonPoint> mButtonsPositions = new HashMap<>();

    private int mCenterPositionX, mCenterPositionY;

    @MenuState
    private int mState;

    private final ControllerListener mListener;


    public MenuController(ControllerListener listener) {
        this.mListener = listener;
        this.mState = MenuState.COLLAPSED;
    }

    public void calculateButtonsVertices(float radius, int startAngle, int circleWidth, int circleHeight, int centerX, int centerY) {
        int left, top, childWidth, childHeight;
        int childCount = getButtonsCount();

        float angleStep = 360.0f / (childCount);
        float lastAngle = startAngle;

        mCenterPositionX = centerX;
        mCenterPositionY = centerY;

        for (int i = 0; i < childCount; i++) {
            final MenuButton button = mButtons.get(i);

            if (button.getVisibility() == View.GONE) {
                continue;
            }

            childWidth = button.getMeasuredWidth();
            childHeight = button.getMeasuredHeight();


            final float currentAngle = lastAngle;
            left = Math.round((float) (((circleWidth / 2.0) - childWidth / 2.0) + radius * Math.cos(Math.toRadians(currentAngle))));
            top = Math.round((float) (((circleHeight / 2.0) - childHeight / 2.0) + radius * Math.sin(Math.toRadians(currentAngle))));

            if (currentAngle > 360) {
                lastAngle -= 360;
            } else if (currentAngle < 0) {
                lastAngle += 360;
            }
            mButtonsPositions.put(button, new MenuButtonPoint(left, top, currentAngle));

            if (mState == MenuState.COLLAPSED) {
                left = Math.round((float) ((circleWidth / 2.0) - childWidth / 2.0));
                top = Math.round((float) ((circleHeight / 2.0) - childHeight / 2.0));
                button.layout(left, top, left + childWidth, top + childHeight);
            } else {
                button.layout(left, top, left + childWidth, top + childHeight);
            }

            lastAngle += angleStep;
        }
    }

    public void toggle() {
        if (isExpanded()) {
            collapse();
        } else {
            expand();
        }
    }

    private void collapse() {
        if (isExpanded()) {
            startCollapseAnimation();
        }
    }

    private void expand() {
        if (!isExpanded()) {
            startExpandAnimation();
        }
    }

    private void startCollapseAnimation() {
        setState(MenuState.COLLAPSE_ANIMATION_STARTED);

        for (int i = 0; i < getButtonsCount(); i++) {
            final MenuButton button = mButtons.get(i);
            MenuButtonPoint buttonPosition = mButtonsPositions.get(button);

            float startPositionX = buttonPosition.x;
            float endPositionX = mCenterPositionX;

            float startPositionY = buttonPosition.y;
            float endPositionY = mCenterPositionY;

            button.setX(startPositionX);
            button.setY(startPositionY);

            ValueAnimator buttonAnimatorX = ValueAnimator.ofFloat(startPositionX, endPositionX);
            buttonAnimatorX.setInterpolator(new DecelerateInterpolator());
            buttonAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    button.setX((float) animation.getAnimatedValue() - button.getLayoutParams().width / 2);
                    button.requestLayout();
                }
            });
            buttonAnimatorX.setDuration(ANIMATION_DURATION);
            buttonAnimatorX.setStartDelay(ANIMATION_DELAY);
            buttonAnimatorX.start();


            ValueAnimator buttonAnimatorY = ValueAnimator.ofFloat(startPositionY, endPositionY);
            buttonAnimatorY.setInterpolator(new DecelerateInterpolator());
            buttonAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    button.setY(animatedValue - button.getLayoutParams().height / 2);
                    button.requestLayout();
                }
            });
            buttonAnimatorY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(MenuState.COLLAPSED);
                }
            });
            buttonAnimatorY.setDuration(ANIMATION_DURATION);
            buttonAnimatorY.setStartDelay(ANIMATION_DELAY);
            buttonAnimatorY.start();
        }
    }

    private void startExpandAnimation() {
        setState(MenuState.EXPAND_ANIMATION_STARTED);

        showItems();
        for (int i = 0; i < getButtonsCount(); i++) {
            final MenuButton button = mButtons.get(i);
            MenuButtonPoint buttonPosition = mButtonsPositions.get(button);

            float startPositionX = mCenterPositionX;
            float endPositionX = buttonPosition.x;

            float startPositionY = mCenterPositionY;
            float endPositionY = buttonPosition.y;

            button.setX(startPositionX);
            button.setY(startPositionY);

            ValueAnimator buttonAnimatorX = ValueAnimator.ofFloat(startPositionX, endPositionX);
            buttonAnimatorX.setInterpolator(new DecelerateInterpolator());
            buttonAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    button.setX((float) animation.getAnimatedValue() - button.getLayoutParams().width / 2);
                    button.requestLayout();
                }
            });
            buttonAnimatorX.setDuration(ANIMATION_DURATION);
            buttonAnimatorX.setStartDelay(ANIMATION_DELAY);
            buttonAnimatorX.start();

            ValueAnimator buttonAnimatorY = ValueAnimator.ofFloat(startPositionY, endPositionY);
            buttonAnimatorY.setInterpolator(new DecelerateInterpolator());
            buttonAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    button.setY(animatedValue - button.getLayoutParams().height / 2);
                    button.requestLayout();
                }
            });
            buttonAnimatorY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setState(MenuState.EXPANDED);
                }
            });
            buttonAnimatorY.setDuration(ANIMATION_DURATION);
            buttonAnimatorY.setStartDelay(ANIMATION_DELAY);
            buttonAnimatorY.start();
        }
    }

    public void setState(@MenuState int state) {
        mState = state;
        switch (state) {
            case MenuState.EXPAND_ANIMATION_STARTED:
                disableItems();
                mListener.onStartExpanding();
                break;
            case MenuState.EXPANDED:
                enableItems();
                mListener.onExpanded();
                break;
            case MenuState.COLLAPSE_ANIMATION_STARTED:
                disableItems();
                mListener.onStartCollapsing();
                break;
            case MenuState.COLLAPSED:
                hideItems();
                mListener.onCollapsed();
                break;
            case MenuState.SELECT_ANIMATION_STARTED:
                disableItems();
                mListener.onSelectAnimationStarted();
                break;
            case MenuState.SELECT_ANIMATION_FINISHED:
                hideItems();
                mListener.onSelectAnimationFinished();
                break;
            case MenuState.EXIT_ANIMATION_STARTED:
                mListener.onExitAnimationStarted();
                break;
            case MenuState.EXIT_ANIMATION_FINISHED:
                mListener.onExitAnimationFinished();
                break;
        }
    }

    public boolean isExpanded() {
        return mState == MenuState.EXPANDED;
    }

    public void addButton(final MenuButton menuButton) {
        mButtons.add(menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(menuButton);
            }
        });
    }

    private void onItemClick(MenuButton menuButton) {
        if (mListener != null) {
            mListener.onItemClick(menuButton);
        }
    }

    public int getButtonsCount() {
        return mButtons.size();
    }

    public void disableItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            mButtons.get(i).setClickable(false);
        }
    }

    public void enableItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            mButtons.get(i).setClickable(true);
        }
    }

    public void hideItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            mButtons.get(i).setVisibility(View.GONE);
        }
    }

    public void showItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            mButtons.get(i).setVisibility(View.VISIBLE);
        }
    }

    public MenuButtonPoint getButtonsPoint(MenuButton menuButton) {
        return mButtonsPositions.get(menuButton);
    }



}
