package com.imangazaliev.circlemenu;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuController {

    private static final int TOGGLE_ANIMATION_DELAY = 100;
    private static final int TOGGLE_ANIMATION_DURATION = 200;

    public interface ControllerListener {

        void onStartCollapsing();

        void onCollapsed();

        void onStartExpanding();

        void onExpanded();

        void onSelectAnimationStarted();

        void onSelectAnimationFinished();

        void onExitAnimationStarted();

        void onExitAnimationFinished();

        void onItemClick(CircleMenuButton menuButton);
    }

    private List<CircleMenuButton> buttons = new ArrayList<>();
    private HashMap<CircleMenuButton, MenuButtonPoint> buttonsPositions = new HashMap<>();

    private int centerPositionX, centerPositionY;

    @MenuState
    private int state;

    private final ControllerListener listener;


    public MenuController(ControllerListener listener) {
        this.listener = listener;
        this.state = MenuState.COLLAPSED;
    }

    public void calculateButtonsVertices(float radius, int startAngle, int circleWidth, int circleHeight, int centerX, int centerY) {
        int left, top, childWidth, childHeight;
        int childCount = getButtonsCount();

        float angleStep = 360.0f / (childCount);
        float lastAngle = startAngle;

        centerPositionX = centerX;
        centerPositionY = centerY;

        for (int i = 0; i < childCount; i++) {
            final CircleMenuButton button = buttons.get(i);

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
            buttonsPositions.put(button, new MenuButtonPoint(left, top, currentAngle));

            if (state == MenuState.COLLAPSED) {
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
            final CircleMenuButton button = buttons.get(i);
            MenuButtonPoint buttonPosition = buttonsPositions.get(button);

            float startPositionX = buttonPosition.x;
            float endPositionX = centerPositionX;

            float startPositionY = buttonPosition.y;
            float endPositionY = centerPositionY;

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
            buttonAnimatorX.setDuration(TOGGLE_ANIMATION_DURATION);
            buttonAnimatorX.setStartDelay(TOGGLE_ANIMATION_DELAY);
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
            buttonAnimatorY.setDuration(TOGGLE_ANIMATION_DURATION);
            buttonAnimatorY.setStartDelay(TOGGLE_ANIMATION_DELAY);
            buttonAnimatorY.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(MenuState.COLLAPSED);
            }
        }, TOGGLE_ANIMATION_DURATION);
    }

    private void startExpandAnimation() {
        setState(MenuState.EXPAND_ANIMATION_STARTED);

        showItems();
        for (int i = 0; i < getButtonsCount(); i++) {
            final CircleMenuButton button = buttons.get(i);
            MenuButtonPoint buttonPosition = buttonsPositions.get(button);

            float startPositionX = centerPositionX;
            float endPositionX = buttonPosition.x;

            float startPositionY = centerPositionY;
            float endPositionY = buttonPosition.y;

            button.setX(startPositionX);
            button.setY(startPositionY);

            //x axis animation
            ValueAnimator buttonAnimatorX = ValueAnimator.ofFloat(startPositionX, endPositionX);
            buttonAnimatorX.setInterpolator(new DecelerateInterpolator());
            buttonAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    button.setX((float) animation.getAnimatedValue() - button.getLayoutParams().width / 2);
                    button.requestLayout();
                }
            });
            buttonAnimatorX.setDuration(TOGGLE_ANIMATION_DURATION);
            buttonAnimatorX.setStartDelay(TOGGLE_ANIMATION_DELAY);
            buttonAnimatorX.start();

            //y axis animation
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
            buttonAnimatorY.setDuration(TOGGLE_ANIMATION_DURATION);
            buttonAnimatorY.setStartDelay(TOGGLE_ANIMATION_DELAY);
            buttonAnimatorY.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(MenuState.EXPANDED);
            }
        }, TOGGLE_ANIMATION_DURATION);
    }

    public void setState(@MenuState int state) {
        this.state = state;
        switch (state) {
            case MenuState.EXPAND_ANIMATION_STARTED:
                disableItems();
                listener.onStartExpanding();
                break;
            case MenuState.EXPANDED:
                enableItems();
                listener.onExpanded();
                break;
            case MenuState.COLLAPSE_ANIMATION_STARTED:
                disableItems();
                listener.onStartCollapsing();
                break;
            case MenuState.COLLAPSED:
                hideItems();
                listener.onCollapsed();
                break;
            case MenuState.SELECT_ANIMATION_STARTED:
                disableItems();
                listener.onSelectAnimationStarted();
                break;
            case MenuState.SELECT_ANIMATION_FINISHED:
                hideItems();
                listener.onSelectAnimationFinished();
                break;
            case MenuState.EXIT_ANIMATION_STARTED:
                listener.onExitAnimationStarted();
                break;
            case MenuState.EXIT_ANIMATION_FINISHED:
                listener.onExitAnimationFinished();
                break;
        }
    }

    public boolean isExpanded() {
        return state == MenuState.EXPANDED;
    }

    public void addButton(final CircleMenuButton menuButton) {
        buttons.add(menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(menuButton);
            }
        });
    }

    private void onItemClick(CircleMenuButton menuButton) {
        if (listener != null) {
            listener.onItemClick(menuButton);
        }
    }

    public int getButtonsCount() {
        return buttons.size();
    }

    public void disableItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setClickable(false);
        }
    }

    public void enableItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setClickable(true);
        }
    }

    public void hideItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setVisibility(View.GONE);
        }
    }

    public void showItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setVisibility(View.VISIBLE);
        }
    }

    public MenuButtonPoint getButtonsPoint(CircleMenuButton menuButton) {
        return buttonsPositions.get(menuButton);
    }


}
