package com.imangazaliev.circlemenu;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class MenuController {

    private static final int TOGGLE_ANIMATION_DELAY = 100;
    private static final int TOGGLE_ANIMATION_DURATION = 200;

    interface ControllerListener {

        void onStartCollapsing();

        void onCollapsed();

        void onStartExpanding();

        void onExpanded();

        void onSelectAnimationStarted();

        void onSelectAnimationFinished();

        void onExitAnimationStarted();

        void onExitAnimationFinished();

        void onItemClick(CircleMenuButton menuButton);

        void onItemLongClick(CircleMenuButton menuButton);

    }

    private List<CircleMenuText> buttons = new ArrayList<>();
    private List<CircleMenuText> menuTexts = new ArrayList<>();

    private HashMap<CircleMenuText, MenuButtonPoint> buttonsPositions = new HashMap<>();
    private final ControllerListener listener;

    private View.OnClickListener onButtonItemClickListener;
    private View.OnLongClickListener onButtonItemLongClickListener;

    @MenuState
    private int state;

    MenuController(ControllerListener listener, final boolean hintsEnabled) {
        this.listener = listener;
        this.state = MenuState.COLLAPSED;
        this.onButtonItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick((CircleMenuText) v);
            }
        };
        this.onButtonItemLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClick((CircleMenuText) v);
                return hintsEnabled;
            }
        };
    }

    void calculateButtonsVertices(float radius, int startAngle, int circleWidth, int circleHeight) {
        int collapsedX, collapsedY, expandedX, expandedY;
        int buttonWidth, buttonHeight;
        int childCount = getButtonsCount();

        float angleStep = 360.0f / (childCount);
        float lastAngle = startAngle;

        for (int i = 0; i < childCount; i++) {
            final CircleMenuText button = buttons.get(i);

            if (button.getVisibility() == View.GONE) {
                continue;
            }

            buttonWidth = button.getMeasuredWidth();
            buttonHeight = button.getMeasuredHeight();

            collapsedX = Math.round((float) ((circleWidth / 2.0) - buttonWidth / 2.0));
            collapsedY = Math.round((float) ((circleHeight / 2.0) - buttonHeight / 2.0));
            expandedX = Math.round((float) (collapsedX + radius * Math.cos(Math.toRadians(lastAngle))));
            expandedY = Math.round((float) (collapsedY + radius * Math.sin(Math.toRadians(lastAngle))));

            MenuButtonPoint menuButtonPoint = new MenuButtonPoint(collapsedX, collapsedY, expandedX, expandedY, lastAngle);
            buttonsPositions.put(button, menuButtonPoint);

            if (lastAngle > 360) {
                lastAngle -= 360;
            } else if (lastAngle < 0) {
                lastAngle += 360;
            }

            button.layout(collapsedX, collapsedY, collapsedX + buttonWidth, collapsedY + buttonHeight);

            lastAngle += angleStep;
        }

    }

    void toggle() {
        if (isExpanded()) {
            startCollapseAnimation();
        } else {
            startExpandAnimation();
        }
    }

    private void startCollapseAnimation() {
        setButtonsStartPosition(false);
        setState(MenuState.COLLAPSE_ANIMATION_STARTED);

        for (final CircleMenuText button : buttons) {
            MenuButtonPoint buttonPosition = buttonsPositions.get(button);

            //x axis animation
            ValueAnimator buttonAnimatorX = ValueAnimator.ofFloat(buttonPosition.expandedX, buttonPosition.collapsedX);
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
            ValueAnimator buttonAnimatorY = ValueAnimator.ofFloat(buttonPosition.expandedY, buttonPosition.collapsedY);
            buttonAnimatorY.setInterpolator(new DecelerateInterpolator());
            buttonAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    button.setY((float) animation.getAnimatedValue() - button.getLayoutParams().height / 2);
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
        setButtonsStartPosition(true);
        setState(MenuState.EXPAND_ANIMATION_STARTED);

        for (final CircleMenuText button : buttons) {
            MenuButtonPoint buttonPosition = buttonsPositions.get(button);

            //x axis animation
            ValueAnimator buttonAnimatorX = ValueAnimator.ofFloat(buttonPosition.collapsedX, buttonPosition.expandedX);
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
            ValueAnimator buttonAnimatorY = ValueAnimator.ofFloat(buttonPosition.collapsedY, buttonPosition.expandedY);
            buttonAnimatorY.setInterpolator(new DecelerateInterpolator());
            buttonAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    button.setY((float) animation.getAnimatedValue() - button.getLayoutParams().height / 2);
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

    private void setButtonsStartPosition(boolean collapsed) {
        for (final CircleMenuText button : buttons) {
            MenuButtonPoint buttonPosition = buttonsPositions.get(button);
            button.setX(collapsed ? buttonPosition.collapsedX : buttonPosition.expandedX);
            button.setY(collapsed ? buttonPosition.collapsedY : buttonPosition.expandedY);
        }
    }

    void setState(@MenuState int state) {

        Log.d("MenuController", "setState: " + state);
        this.state = state;
        switch (state) {
            case MenuState.EXPAND_ANIMATION_STARTED:
                disableItems();
                showItems();
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

    boolean isExpanded() {
        return state == MenuState.EXPANDED;
    }

    void addButton(final CircleMenuText menuButton) {
        buttons.add(menuButton);
        menuButton.setOnClickListener(onButtonItemClickListener);
        menuButton.setOnLongClickListener(onButtonItemLongClickListener);
    }

    void addCircleMenuText(final CircleMenuText circleMenuText) {
        menuTexts.add(circleMenuText);
    }

    private void onItemClick(CircleMenuText menuButton) {
        if (listener != null) {
            listener.onItemClick(menuButton.getCircleMenuButton());
        }
    }

    private void onItemLongClick(CircleMenuText menuButton) {
            listener.onItemLongClick(menuButton.getCircleMenuButton());
    }

    private int getButtonsCount() {
        return buttons.size();
    }

    private void disableItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setClickable(false);
        }
    }

    private void enableItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setClickable(true);
        }
    }

    private void hideItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setVisibility(View.GONE);
        }
    }

    private void showItems() {
        for (int i = 0; i < getButtonsCount(); i++) {
            buttons.get(i).setVisibility(View.VISIBLE);
        }
    }

    MenuButtonPoint getButtonsPoint(CircleMenuButton menuButton) {
        return buttonsPositions.get(menuButton);
    }

    public List<CircleMenuText> getMenuTexts() {
        return menuTexts;
    }

}
