package com.imangazaliev.circlemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CircleMenu extends ViewGroup implements MenuController.ControllerListener, ItemSelectionAnimator.AnimationDrawController {

    public interface OnItemClickListener {
        void onItemClick(CircleMenuButton menuButton);
    }

    public interface OnStateUpdateListener {
        void onMenuExpanded();

        void onMenuCollapsed();
    }

    // Sizes of the ViewGroup
    private float radius = -1;
    private int circleStartAngle;
    private boolean hintsEnabled;

    private CenterMenuButton centerButton;

    private MenuController menuController;
    private ItemSelectionAnimator itemSelectionAnimator;

    private OnStateUpdateListener stateUpdateListener;
    private OnItemClickListener onItemClickListener;

    public CircleMenu(Context context) {
        this(context, null);
    }

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
            circleStartAngle = typedArray.getInteger(R.styleable.CircleMenu_start_angle, getResources().getInteger(R.integer.circle_menu_start_angle));
            radius = typedArray.getDimension(R.styleable.CircleMenu_distance, getResources().getDimension(R.dimen.circle_menu_distance));
            hintsEnabled = typedArray.getBoolean(R.styleable.CircleMenu_hintsEnabled, false);
        } finally {
            typedArray.recycle();
        }

        menuController = new MenuController(this, hintsEnabled);
        itemSelectionAnimator = new ItemSelectionAnimator(menuController, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addChildrenToController();
        createCenterButton(getContext());
    }

    private void addChildrenToController() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != centerButton) {
                menuController.addButton((CircleMenuButton) child);
            }
        }
    }

    private void createCenterButton(Context context) {
        centerButton = new CenterMenuButton(context);
        centerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menuController.toggle();
            }
        });

        addView(centerButton, super.generateDefaultLayoutParams());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // Measure child views first
        int maxChildWidth = 0;
        int maxChildHeight = 0;

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);

            maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
        }

        // Then decide what size we want to be
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(widthSize, heightSize);
        } else {
            //Be whatever you want
            width = maxChildWidth * 3;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(heightSize, widthSize);
        } else {
            //Be whatever you want
            height = maxChildHeight * 3;
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int circleHeight = getHeight();
        int circleWidth = getWidth();

        int centerButtonWidth = centerButton.getMeasuredWidth();
        int centerButtonHeight = centerButton.getMeasuredHeight();
        int centerButtonX = Math.round((float) ((circleWidth / 2.0) - centerButtonWidth / 2.0));
        int centerButtonY = Math.round((float) ((circleHeight / 2.0) - centerButtonHeight / 2.0));
        centerButton.layout(centerButtonX, centerButtonY, centerButtonX + centerButtonWidth, centerButtonY + centerButtonHeight);

        menuController.calculateButtonsVertices(radius, circleStartAngle, circleWidth, circleHeight);
        itemSelectionAnimator.setCircleRadius(radius, circleWidth, circleHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        itemSelectionAnimator.onDraw(canvas);
    }

    @Override
    public void onStartExpanding() {
        centerButton.setExpanded(true);
        centerButton.setClickable(false);
    }

    @Override
    public void onExpanded() {
        centerButton.setClickable(true);

        if (stateUpdateListener != null) {
            stateUpdateListener.onMenuExpanded();
        }
    }

    @Override
    public void onSelectAnimationStarted() {
        centerButton.setClickable(false);
    }

    @Override
    public void onSelectAnimationFinished() {

    }

    @Override
    public void onExitAnimationStarted() {

    }

    @Override
    public void onExitAnimationFinished() {
        centerButton.setClickable(true);
    }

    @Override
    public void onStartCollapsing() {
        centerButton.setExpanded(false);
        centerButton.setClickable(false);
    }

    @Override
    public void onCollapsed() {
        centerButton.setClickable(true);

        if (stateUpdateListener != null) {
            stateUpdateListener.onMenuCollapsed();
        }
    }

    @Override
    public void onItemClick(CircleMenuButton menuButton) {
        centerButton.setExpanded(false);
        itemSelectionAnimator.onItemClick(menuButton, menuController.getButtonsPoint(menuButton));

        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(menuButton);
        }
    }

    @Override
    public void onItemLongClick(CircleMenuButton menuButton) {
        if (hintsEnabled) {
            Toast.makeText(getContext(), menuButton.getHintText(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void redrawView() {
        invalidate();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setStateUpdateListener(OnStateUpdateListener listener) {
        this.stateUpdateListener = listener;
    }

    public void toggle() {
        menuController.toggle();
    }

    public boolean isExpanded() {
        return menuController.isExpanded();
    }

    public void addButton(CircleMenuButton menuButton) {
        addView(menuButton, getChildCount() - 1);
        menuController.addButton(menuButton);
    }

}
