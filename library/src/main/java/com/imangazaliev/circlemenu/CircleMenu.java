package com.imangazaliev.circlemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CircleMenu extends ViewGroup implements MenuController.ControllerListener, ItemSelectionAnimator.AnimationDrawController {

    public interface OnItemClickListener {
        void onItemClick(CircleMenuButton menuButton);
    }

    public interface OnStateUpdateListener {
        void onMenuExpanded();

        void onMenuCollapsed();
    }

    // Sizes of the ViewGroup
    private float mRadius = -1;
    private int mCircleStartAngle;

    private CenterMenuButton mCenterButton;

    private MenuController mMenuController;
    private ItemSelectionAnimator mItemSelectionAnimator;

    private OnStateUpdateListener mListener;
    private OnItemClickListener mOnItemClickListener;

    public CircleMenu(Context context) {
        this(context, null);
    }

    public CircleMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleMenu);
        try {
            mCircleStartAngle = typedArray.getInteger(R.styleable.CircleMenu_start_angle, getResources().getInteger(R.integer.circle_menu_start_angle));
            mRadius = typedArray.getDimension(R.styleable.CircleMenu_distance, getResources().getDimension(R.dimen.circle_menu_distance));
        } finally {
            typedArray.recycle();
        }

        mMenuController = new MenuController(this);
        mItemSelectionAnimator = new ItemSelectionAnimator(mMenuController, this);
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
            if (child != mCenterButton) {
                mMenuController.addButton((CircleMenuButton) child);
            }
        }
    }

    private void createCenterButton(Context context) {
        mCenterButton = new CenterMenuButton(context);
        mCenterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuController.toggle();
            }
        });

        addView(mCenterButton, super.generateDefaultLayoutParams());
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

        int centerButtonWidth = mCenterButton.getMeasuredWidth();
        int centerButtonHeight = mCenterButton.getMeasuredHeight();
        int centerButtonLeft = Math.round((float) ((circleWidth / 2.0) - centerButtonWidth / 2.0));
        int centerButtonTop = Math.round((float) ((circleHeight / 2.0) - centerButtonHeight / 2.0));
        mCenterButton.layout(centerButtonLeft, centerButtonTop, centerButtonLeft + centerButtonWidth, centerButtonTop + centerButtonHeight);

        mMenuController.calculateButtonsVertices(mRadius, mCircleStartAngle, circleWidth, circleHeight, centerButtonLeft, centerButtonTop);
        mItemSelectionAnimator.setCircleRadius(mRadius, circleWidth, circleHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        mItemSelectionAnimator.onDraw(canvas);
    }

    @Override
    public void onStartExpanding() {
        mCenterButton.setExpanded(true);
        mCenterButton.setClickable(false);
    }

    @Override
    public void onExpanded() {
        mCenterButton.setClickable(true);

        if (mListener != null) {
            mListener.onMenuExpanded();
        }
    }

    @Override
    public void onSelectAnimationStarted() {
        mCenterButton.setClickable(false);
    }

    @Override
    public void onSelectAnimationFinished() {

    }

    @Override
    public void onExitAnimationStarted() {

    }

    @Override
    public void onExitAnimationFinished() {
        mCenterButton.setClickable(true);
    }

    @Override
    public void onStartCollapsing() {
        mCenterButton.setExpanded(false);
        mCenterButton.setClickable(false);
    }

    @Override
    public void onCollapsed() {
        mCenterButton.setClickable(true);

        if (mListener != null) {
            mListener.onMenuCollapsed();
        }
    }

    @Override
    public void onItemClick(CircleMenuButton menuButton) {
        mCenterButton.setExpanded(false);
        mItemSelectionAnimator.onItemClick(menuButton, mMenuController.getButtonsPoint(menuButton));

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(menuButton);
        }
    }

    @Override
    public void redrawView() {
        invalidate();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setStateUpdateListener(OnStateUpdateListener listener) {
        this.mListener = listener;
    }

    public void toggle() {
        mMenuController.toggle();
    }


    public boolean isExpanded() {
        return mMenuController.isExpanded();
    }

    public void addButton(CircleMenuButton menuButton) {
        addView(menuButton, getChildCount() - 1);
        mMenuController.addButton(menuButton);
    }

}
