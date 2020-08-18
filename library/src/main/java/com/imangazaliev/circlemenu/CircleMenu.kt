package com.imangazaliev.circlemenu

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import java.util.*

class CircleMenu : FrameLayout, MenuControllerListener {

    val isOpened: Boolean
        get() = menuController!!.isOpened

    private var distance = 0
    private var circleStartAngle = 0
    private var openOnStart = false
    private var hintsEnabled = false
    private var calculatedSize = 0
    private lateinit var centerButton: CenterMenuButton
    private var menuController: MenuController? = null
    private val eventListener: EventsListener = EventsListener()

    private var onItemClickListener: ((menuButton: CircleMenuButton?) -> Unit)? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleMenu)
        try {
            circleStartAngle = typedArray.getInteger(R.styleable.CircleMenu_startAngle, resources.getInteger(R.integer.circle_menu_start_angle))
            distance = typedArray.getDimension(R.styleable.CircleMenu_distance, resources.getDimension(R.dimen.circle_menu_distance)).toInt()
            openOnStart = typedArray.getBoolean(R.styleable.CircleMenu_openOnStart, false)
            hintsEnabled = typedArray.getBoolean(R.styleable.CircleMenu_hintsEnabled, false)
        } finally {
            typedArray.recycle()
        }
        val buttonSize = resources.getDimension(R.dimen.circle_menu_button_size)
        val ringRadius = (buttonSize + (distance - buttonSize / 2)).toInt()
        calculatedSize = (ringRadius * 2 * ItemSelectionAnimator.END_CIRCLE_SIZE_RATIO).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        createCenterButton()
    }

    private fun createCenterButton() {
        centerButton = CenterMenuButton(context, openOnStart)
        centerButton.setOnClickListener { toggle() }
        val centerButtonParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        centerButtonParams.gravity = Gravity.CENTER
        centerButton.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    centerButton.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                initMenuController()
            }
        })
        addView(centerButton, centerButtonParams)
    }

    private fun initMenuController() {
        val buttons: MutableList<CircleMenuButton> = ArrayList()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child !== centerButton) {
                buttons.add(child as CircleMenuButton)
            }
        }
        menuController = MenuController(context, buttons, this, centerButton.x, centerButton.y,
                circleStartAngle.toFloat(), distance, openOnStart, hintsEnabled)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = View.resolveSizeAndState(calculatedSize, widthMeasureSpec, 0)
        val h = View.resolveSizeAndState(calculatedSize, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        menuController!!.onDraw(canvas)
    }

    override fun onOpenAnimationStart() {
        centerButton.isClickable = false
        centerButton.setOpened(true)
        eventListener.onMenuOpenAnimationStart
    }

    override fun onOpenAnimationEnd() {
        centerButton.isClickable = true
        eventListener.onMenuOpenAnimationEnd?.invoke()
    }

    override fun onCloseAnimationStart() {
        centerButton.isClickable = false
        centerButton.setOpened(false)
        eventListener.onMenuCloseAnimationStart?.invoke()
    }

    override fun onCloseAnimationEnd() {
        centerButton.isClickable = true
        eventListener.onMenuCloseAnimationEnd?.invoke()
    }

    override fun onSelectAnimationStart(menuButton: CircleMenuButton) {
        centerButton.setOpened(false)
        centerButton.isClickable = false
        onItemClickListener?.invoke(menuButton)
        eventListener.onButtonClickAnimationStart?.invoke(menuButton)
    }

    override fun onSelectAnimationEnd(menuButton: CircleMenuButton) {
        centerButton.isClickable = true
        eventListener.onButtonClickAnimationEnd?.invoke(menuButton)
    }

    override fun redrawView() {
        invalidate()
    }

    fun setOnItemClickListener(listener: (menuButton: CircleMenuButton?) -> Unit) {
        this.onItemClickListener = listener
    }

    fun toggle() {
        menuController!!.toggle()
    }

    fun open(animate: Boolean) {
        menuController!!.open(animate)
    }

    fun close(animate: Boolean) {
        menuController!!.close(animate)
    }

    fun relayotMenuItems() {
        menuController!!.setCenterButtonPosition(centerButton.x, centerButton.y)
    }

    fun onMenuOpenAnimationStart(listener: () -> Unit) {
        eventListener.onMenuOpenAnimationStart = listener
    }
    fun onMenuOpenAnimationEnd(listener: () -> Unit) {
        eventListener.onMenuOpenAnimationEnd = listener
    }
    fun onMenuCloseAnimationStart(listener: () -> Unit) {
        eventListener.onMenuCloseAnimationStart = listener
    }
    fun onMenuCloseAnimationEnd(listener: () -> Unit) {
        eventListener.onMenuCloseAnimationEnd = listener
    }
    fun onButtonClickAnimationStart(listener: (menuButton: CircleMenuButton) -> Unit) {
        eventListener.onButtonClickAnimationStart = listener
    }
    fun onButtonClickAnimationEnd(listener: (menuButton: CircleMenuButton) -> Unit) {
        eventListener.onButtonClickAnimationEnd = listener
    }


}