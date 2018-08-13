package com.imangazaliev.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.util.List;

class MenuController {

    private static final int TOGGLE_ANIMATION_DURATION = 200;

    private List<CircleMenuButton> buttons;
    private float menuCenterX, menuCenterY;
    private float startAngle;
    private float angleRange;
    private int distance;
    private boolean isOpened;

    private final ItemSelectionAnimator itemSelectionAnimator;
    private final MenuControllerListener listener;


    MenuController(Context context,
                   List<CircleMenuButton> menuButtons,
                   final MenuControllerListener listener,
                   float menuCenterX,
                   float menuCenterY,
                   final float startAngle,
                   final float angleRange,
                   int distance,
                   boolean openOnStart,
                   final boolean hintsEnabled) {
        this.buttons = menuButtons;
        this.menuCenterX = menuCenterX;
        this.menuCenterY = menuCenterY;
        this.startAngle = startAngle;
        this.angleRange = angleRange;
        this.distance = distance;
        this.isOpened = openOnStart;
        this.listener = listener;
        this.itemSelectionAnimator = new ItemSelectionAnimator(context, this, listener, menuCenterX, menuCenterY, distance);

        View.OnClickListener onButtonItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleMenuButton menuButton = (CircleMenuButton) v;
                if (menuButton.isShowClickAnim()) {
                    float buttonAngle = (angleRange / buttons.size()) * buttons.indexOf(menuButton) + startAngle;
                    itemSelectionAnimator.startSelectAnimation(menuButton, buttonAngle);
                }
                listener.onClick(menuButton);
            }
        };
        View.OnLongClickListener onButtonItemLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CircleMenuButton menuButton = (CircleMenuButton) v;
                if (hintsEnabled) {
                    Toast.makeText(menuButton.getContext(), menuButton.getHintText(), Toast.LENGTH_SHORT).show();
                }
                return hintsEnabled;
            }
        };

        for (CircleMenuButton menuButton : buttons) {
            menuButton.setOnClickListener(onButtonItemClickListener);
            menuButton.setOnLongClickListener(onButtonItemLongClickListener);
        }

        layoutButtons(openOnStart ? distance : 0);
    }

    void onDraw(Canvas canvas) {
        itemSelectionAnimator.onDraw(canvas);
    }

    void toggle() {
        if (isOpened()) {
            close(true);
        } else {
            open(true);
        }
    }

    void open(boolean animate) {
        if (isOpened) {
            return;
        }

        enableButtons(false);
        layoutButtons(0);
        showButtons(true);
        listener.onOpenAnimationStart();

        ValueAnimator buttonAnimator = ValueAnimator.ofFloat(0f, distance);
        buttonAnimator.setDuration(animate ? TOGGLE_ANIMATION_DURATION : 0);
        buttonAnimator.setInterpolator(new DecelerateInterpolator());
        buttonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutButtons((float) animation.getAnimatedValue());
            }
        });
        buttonAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOpened = true;
                enableButtons(true);
                listener.onOpenAnimationEnd();
            }
        });
        buttonAnimator.start();
    }

    void close(boolean animate) {
        if (!isOpened) {
            return;
        }

        enableButtons(false);
        layoutButtons(distance);
        listener.onCloseAnimationStart();

        ValueAnimator buttonAnimator = ValueAnimator.ofFloat(distance, 0f);
        buttonAnimator.setDuration(animate ? TOGGLE_ANIMATION_DURATION : 0);
        buttonAnimator.setInterpolator(new DecelerateInterpolator());
        buttonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutButtons((float) animation.getAnimatedValue());
            }
        });
        buttonAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOpened = false;
                showButtons(false);
                listener.onCloseAnimationEnd();
            }
        });
        buttonAnimator.start();
    }

    private void layoutButtons(float distance) {
        int buttonsCount = buttons.size();
        float angleStep = angleRange / buttonsCount;
        float lastAngle = startAngle;

        for (int i = 0; i < buttonsCount; i++) {
            CircleMenuButton button = buttons.get(i);

            float x = Math.round((float) (menuCenterX + distance * Math.cos(Math.toRadians(lastAngle))));
            float y = Math.round((float) (menuCenterY + distance * Math.sin(Math.toRadians(lastAngle))));

            button.setX(x);
            button.setY(y);

            if (lastAngle > startAngle + angleRange) {
                lastAngle -= angleRange;
            }

            lastAngle += angleStep;
        }

    }

    void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    boolean isOpened() {
        return isOpened;
    }

    void enableButtons(boolean enabled) {
        for (CircleMenuButton button : buttons) {
            button.setEnabled(enabled);
        }
    }

    void showButtons(boolean visible) {
        for (CircleMenuButton button : buttons) {
            button.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

}
