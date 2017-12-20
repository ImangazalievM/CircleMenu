package com.imangazaliev.circlemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CircleMenu extends ViewGroup implements
        MenuController.ControllerListener, ItemSelectionAnimator.AnimationDrawController {

    public interface OnItemClickListener {
        void onItemClick(CircleMenuButton menuButton);
    }

    public interface OnStateUpdateListener {
        void onMenuExpanded();

        void onMenuCollapsed();
    }

    public interface OnConfirmationListener {
        void onConfirmation(List<Object> listData);
    }

    // Sizes of the ViewGroup
    private float radius = -1;
    private int circleStartAngle;
    private boolean hintsEnabled;
    private boolean multipleCheck;
    private boolean borderCheck;
    private boolean alphaCheck;

    public CenterMenuButton getCenterButton() {
        return centerButton;
    }

    public void setCenterButton(CenterMenuButton centerButton) {
        this.centerButton = centerButton;
    }

    private CenterMenuButton centerButton;

    private MenuController menuController;
    private ItemSelectionAnimator itemSelectionAnimator;

    private OnStateUpdateListener stateUpdateListener;
    private OnItemClickListener onItemClickListener;
    private OnConfirmationListener onConfirmationListener;

    private List<Object> listObjectData;
    private Map<String, CircleMenuButton> listIndentifyChildMenuButton;

    private Drawable centerMenuButtonDrawable;
    private Drawable confirmationMenuButtonDrawable;

    private float alphaChecked = 0.5f;
    private float alphaUnChecked = 1f;

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
            centerMenuButtonDrawable = typedArray.getDrawable(R.styleable.CircleMenu_center_drawable);
            multipleCheck = typedArray.getBoolean(R.styleable.CircleMenu_multiple_check, false);
            borderCheck = typedArray.getBoolean(R.styleable.CircleMenu_border_check, false);
            alphaCheck = typedArray.getBoolean(R.styleable.CircleMenu_alpha_check, true);
            confirmationMenuButtonDrawable = typedArray.getDrawable(R.styleable.CircleMenu_confirmation_center_drawable);
            listObjectData = new ArrayList<>();
            listIndentifyChildMenuButton = new HashMap<>();
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
                Log.d("CircleMenu", "AQUI MISERA!: ");
                menuController.addButton((CircleMenuText) child);
            }
        }
    }

    private void createCenterButton(Context context) {
        centerButton = new CenterMenuButton(context);
        centerButton.setHasCenterButton(true);
        centerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCenterMenu();
            }
        });

        addCenterDrawableIfEnable();
        addView(centerButton, super.generateDefaultLayoutParams());

    }

    private void clickCenterMenu() {
        centerButton.setMultipleCheck(multipleCheck);
        menuController.toggle();
    }

    private void addCenterDrawableIfEnable() {
        if (this.centerMenuButtonDrawable != null) {
            centerButton.setImageDrawable(this.centerMenuButtonDrawable);
        }
    }

    private void addConfirmationDrawableIfEnable() {
        if (this.confirmationMenuButtonDrawable != null) {
            centerButton.setImageDrawable(this.confirmationMenuButtonDrawable);
        }
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
        addConfirmationDrawableIfEnable();
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
        addCenterDrawableIfEnable();
        centerButton.setClickable(true);
        if (this.multipleCheck) {
            if (onConfirmationListener != null) {
                onConfirmationListener.onConfirmation(listObjectData);
            }
            clearClircleMenuButtons();
        }
        if (stateUpdateListener != null) {
            stateUpdateListener.onMenuCollapsed();
        }
    }

    private void clearClircleMenuButtons() {
        setStatusDefaultCircleMenuButton();
        listIndentifyChildMenuButton = new HashMap<>();
        listObjectData = new ArrayList<>();
    }

    @Override
    public void onItemClick(CircleMenuButton menuButton) {
        centerButton.setExpanded(false);
        if (multipleCheck) {
            if (verifyAlreadyChecked(menuButton.getGenerateId())) {
                removeCheck(menuButton);
            } else {
                addCheck(menuButton);
            }
        } else {
            itemSelectionAnimator.onItemClick(menuButton, menuController.getButtonsPoint(menuButton));
        }

        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(menuButton);
        }
    }

    private void addCheck(CircleMenuButton menuButton) {
        menuButton.setGenerateId(UUID.randomUUID().toString());
        addCheckedAnimation(menuButton);
        listIndentifyChildMenuButton.put(menuButton.getGenerateId(), menuButton);
        listObjectData.add(menuButton.getMetaData());
    }

    private void addCheckedAnimation(CircleMenuButton menuButton) {
        if (alphaCheck){
            menuButton.setAlpha(alphaChecked);
        }
        if (borderCheck){
            menuButton.startCheckAnimation();
        }
    }

    private void removeCheck(CircleMenuButton menuButton) {
        unCheckedAnimation(menuButton);
        listObjectData.remove(menuButton.getMetaData());
        listIndentifyChildMenuButton.remove(menuButton.getGenerateId());
    }

    private void unCheckedAnimation(CircleMenuButton menuButton) {
        if (alphaCheck){
            menuButton.setAlpha(alphaUnChecked);
        }
        if (borderCheck){
            menuButton.reverseCheckAnimation();
        }
    }

    public void setStatusDefaultCircleMenuButton() {
        for(Map.Entry<String, CircleMenuButton> entry : listIndentifyChildMenuButton.entrySet()) {
            CircleMenuButton cirleMenuButton = entry.getValue();
            cirleMenuButton.setAlpha(1f);
        }
    }

    public boolean verifyAlreadyChecked(String id) {
        if (id != null) {
            if (listIndentifyChildMenuButton.get(id) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemLongClick(CircleMenuButton menuButton) {
        if (hintsEnabled) {
            Toast.makeText(getContext(), menuButton.getHintText(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
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

    public void setOnConfirmationListener(OnConfirmationListener onConfirmationListener) {
        this.onConfirmationListener = onConfirmationListener;
    }

    public void toggle() {
        menuController.toggle();
    }

    public boolean isExpanded() {
        return menuController.isExpanded();
    }

    public void addButton(CircleMenuText menuButton) {
        Log.d("CircleMenu", "addButton: ");
        addView(menuButton, getChildCount() - 1);
        menuController.addButton(menuButton);
    }

    public void addCircleMenuText(CircleMenuText circleMenuText) {
        addView(circleMenuText, getChildCount() -1);
        menuController.addCircleMenuText(circleMenuText);
    }

    public void addText(TextView txt, RelativeLayout.LayoutParams params) {
        addView(txt, getChildCount() - 1, params);
        //menuController.addButton(menuButton);
    }

    public boolean isMultipleCheck() {
        return multipleCheck;
    }

    public void setMultipleCheck(boolean buttonConfimation) {
        this.multipleCheck = buttonConfimation;
    }


    public void setAlphaChecked(float alphaChecked) {
        this.alphaChecked = alphaChecked;
    }

    public void setBorderChecked(boolean borderCheck) {
        this.borderCheck = borderCheck;
    }

    public void setAlphaUnChecked(float alphaUnChecked) {
        this.alphaUnChecked = alphaUnChecked;
    }

}
