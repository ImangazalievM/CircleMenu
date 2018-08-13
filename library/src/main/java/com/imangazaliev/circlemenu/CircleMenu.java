package com.imangazaliev.circlemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class CircleMenu extends FrameLayout implements MenuControllerListener {

    private int distance;
    private int circleStartAngle;
    private int angleRange;
    private boolean openOnStart;
    private boolean hintsEnabled;
    private int calculatedSize;
    private int centerButtonGravity;

    private CenterMenuButton centerButton;
    private MenuController menuController;

    private EventListener eventListener;
    private OnItemClickListener onItemClickListener;

    public CircleMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleMenu);

        try {
            circleStartAngle = typedArray.getInteger(R.styleable.CircleMenu_startAngle, getResources().getInteger(R.integer.circle_menu_start_angle));
            angleRange = typedArray.getInteger(R.styleable.CircleMenu_angleRange, 360);
            distance = (int) typedArray.getDimension(R.styleable.CircleMenu_distance, getResources().getDimension(R.dimen.circle_menu_distance));
            centerButtonGravity = typedArray.getInt(R.styleable.CircleMenu_centerButtonGravity, Gravity.CENTER);
            openOnStart = typedArray.getBoolean(R.styleable.CircleMenu_openOnStart, false);
            hintsEnabled = typedArray.getBoolean(R.styleable.CircleMenu_hintsEnabled, false);
        } finally {
            typedArray.recycle();
        }

        float buttonSize = getResources().getDimension(R.dimen.circle_menu_button_size);
        int ringRadius = (int) (buttonSize + (distance - buttonSize / 2));
        calculatedSize = (int) (ringRadius * 2 * ItemSelectionAnimator.END_CIRCLE_SIZE_RATIO);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        createCenterButton();
    }

    private void createCenterButton() {
        centerButton = new CenterMenuButton(getContext(), openOnStart);
        centerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        LayoutParams centerButtonParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        centerButtonParams.gravity = centerButtonGravity;

        centerButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                centerButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                initMenuController();
            }
        });

        addView(centerButton, centerButtonParams);
    }

    private void initMenuController() {
        List<CircleMenuButton> buttons = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != centerButton) {
                buttons.add((CircleMenuButton) child);
            }
        }

        menuController = new MenuController(getContext(), buttons, this, centerButton.getX(), centerButton.getY(),
                circleStartAngle, angleRange, distance, openOnStart, hintsEnabled);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int w = resolveSizeAndState(calculatedSize, widthMeasureSpec, 0);
        final int h = resolveSizeAndState(calculatedSize, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        menuController.onDraw(canvas);
    }

    @Override
    public void onOpenAnimationStart() {
        centerButton.setClickable(false);
        centerButton.setOpened(true);

        if (eventListener != null) {
            eventListener.onMenuOpenAnimationStart();
        }
    }

    @Override
    public void onOpenAnimationEnd() {
        centerButton.setClickable(true);

        if (eventListener != null) {
            eventListener.onMenuOpenAnimationEnd();
        }
    }

    @Override
    public void onCloseAnimationStart() {
        centerButton.setClickable(false);
        centerButton.setOpened(false);

        if (eventListener != null) {
            eventListener.onMenuCloseAnimationStart();
        }
    }

    @Override
    public void onCloseAnimationEnd() {
        centerButton.setClickable(true);

        if (eventListener != null) {
            eventListener.onMenuCloseAnimationEnd();
        }
    }

    @Override
    public void onSelectAnimationStart(CircleMenuButton menuButton) {
        centerButton.setOpened(false);
        centerButton.setClickable(false);

        if (eventListener != null) {
            eventListener.onButtonClickAnimationStart(menuButton);
        }
    }

    @Override
    public void onSelectAnimationEnd(CircleMenuButton menuButton) {
        centerButton.setClickable(true);
        if (eventListener != null) {
            eventListener.onButtonClickAnimationEnd(menuButton);
        }
    }

    @Override
    public void redrawView() {
        invalidate();
    }

    @Override
    public void onClick(CircleMenuButton menuButton) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(menuButton);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setEventListener(EventListener listener) {
        this.eventListener = listener;
    }

    public void toggle() {
        menuController.toggle();
    }

    public void open(boolean animate) {
        menuController.open(animate);
    }

    public void close(boolean animate) {
        menuController.close(animate);
    }

    public boolean isOpened() {
        return menuController.isOpened();
    }

    public interface OnItemClickListener {
        void onItemClick(CircleMenuButton menuButton);
    }

    public interface EventListener {

        void onMenuOpenAnimationStart();

        void onMenuOpenAnimationEnd();

        void onMenuCloseAnimationStart();

        void onMenuCloseAnimationEnd();

        void onButtonClickAnimationStart(@NonNull CircleMenuButton menuButton);

        void onButtonClickAnimationEnd(@NonNull CircleMenuButton menuButton);
    }


}
