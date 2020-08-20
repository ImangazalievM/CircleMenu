package com.imangazaliev.circlemenu

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.core.content.ContextCompat


class CircleMenu @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), MenuController.Listener {

    val isOpened: Boolean
        get() = menuController!!.isOpened

    private var menuButtonColor = 0
    private var menuIconColor = 0
    private var buttonIconsColor = 0
    private var distance = 0
    private var circleStartAngle = 0
    private var openOnStart = false
    private var calculatedSize = 0
    private lateinit var centerButton: CenterMenuButton

    private var menuController: MenuController? = null
    private val eventListener: EventsListener = EventsListener()
    internal var onItemClickListener: ((buttonIndex: Int) -> Unit)? = null
    internal var onItemLongClickListener: ((buttonIndex: Int) -> Unit)? = null

    private lateinit var icons: List<Int>
    private lateinit var colors: List<Int>

    constructor(
            context: Context,
            icons: List<Int>,
            colors: List<Int>
    ) : this(context, null, 0) {
        this.icons = icons
        this.colors = colors

        init(null)
    }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleMenu)
        try {
            circleStartAngle = typedArray.getInteger(R.styleable.CircleMenu_startAngle, resources.getInteger(R.integer.circle_menu_start_angle))
            distance = typedArray.getDimension(R.styleable.CircleMenu_distance, resources.getDimension(R.dimen.circle_menu_distance)).toInt()
            openOnStart = typedArray.getBoolean(R.styleable.CircleMenu_openOnStart, false)

            val menuButtonColorDef = ContextCompat.getColor(context, R.color.circle_menu_center_button_color)
            menuButtonColor = typedArray.getColor(R.styleable.CircleMenu_menuButtonColor, menuButtonColorDef)
            val menuIconColorDef = ContextCompat.getColor(context, R.color.circle_menu_center_button_icon_color)
            menuIconColor = typedArray.getColor(R.styleable.CircleMenu_menuIconColor, menuIconColorDef)
            val iconsColorDef = ContextCompat.getColor(context, R.color.circle_menu_button_icon_color)
            buttonIconsColor = typedArray.getColor(R.styleable.CircleMenu_iconsColor, iconsColorDef)

            val iconArrayId: Int = typedArray.getResourceId(R.styleable.CircleMenu_buttonIcons, 0)
            val colorArrayId: Int = typedArray.getResourceId(R.styleable.CircleMenu_buttonColors, 0)

            colors = resources.getIntArray(colorArrayId).asList()
            icons = resources.obtainTypedArray(iconArrayId).let { iconsIds ->
                (0 until iconsIds.length()).map { iconsIds.getResourceId(it, -1) }
            }

            if (colors.size != colors.size) {
                throw IllegalArgumentException("Colors array size must be equal to the icons array")
            }

            if (colors.size != colors.size) {
                throw IllegalArgumentException("Colors and icons array must not be empty")
            }
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
        centerButton = CenterMenuButton(context, menuIconColor, openOnStart)
        centerButton.setColor(menuButtonColor)
        centerButton.setOnClickListener { toggle() }
        centerButton.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    centerButton.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                initMenuController()
            }
        })
        val centerButtonParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        centerButtonParams.gravity = Gravity.CENTER
        addView(centerButton, centerButtonParams)
    }

    private fun initMenuController() {
        val buttons = colors.mapIndexed { index, color ->
            val button = CircleMenuButton(context)
            button.setIconColor(buttonIconsColor)
            button.setIcon(icons[index])
            button.setColor(color)
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            button.layoutParams = params
            addView(button)
            button
        }


        menuController = MenuController(
                context = context,
                buttons = buttons,
                listener = this,
                menuCenterX = centerButton.x,
                menuCenterY = centerButton.y,
                startAngle = circleStartAngle.toFloat(),
                distance = distance,
                isOpened = openOnStart
        )
    }

    override fun onButtonClick(menuButton: CircleMenuButton, index: Int) {
        this.onItemClickListener?.invoke(index)
    }

    override fun onButtonLongClick(menuButton: CircleMenuButton, index: Int) {
        this.onItemLongClickListener?.invoke(index)
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
        eventListener.onButtonClickAnimationStart?.invoke(menuButton)
    }

    override fun onSelectAnimationEnd(menuButton: CircleMenuButton) {
        centerButton.isClickable = true
        eventListener.onButtonClickAnimationEnd?.invoke(menuButton)
    }

    override fun redrawView() {
        invalidate()
    }

    fun setOnItemClickListener(listener: (buttonIndex: Int) -> Unit) {
        this.onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (buttonIndex: Int) -> Unit) {
        this.onItemLongClickListener = listener
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