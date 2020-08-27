package com.imangazaliev.circlemenu

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.Gravity
import android.widget.FrameLayout

@SuppressLint("ViewConstructor")
internal class CircleMenuLayout(
        context: Context,
        centerButtonColor: Int,
        centerButtonIconColor: Int,
        menuIconType: CircleMenu.MenuIconType,
        openOnStart: Boolean,
        showSelectAnimation: Boolean,
        private val buttonIconsColor: Int,
        private val distance: Int,
        private val circleMaxAngle: Int,
        private val circleStartAngle: Int,
        private var icons: List<Int>,
        private var colors: List<Int>
) : FrameLayout(context), MenuController.Listener {

    val isOpened: Boolean
        get() = menuController.isOpened

    private var calculatedSize = 0
    private val centerButton: CenterMenuButton = CenterMenuButton(
            context,
            centerButtonColor,
            menuIconType,
            centerButtonIconColor,
            openOnStart
    )

    private val menuController: MenuController by lazy {
        val buttons = createButtons(context)
        MenuController(
                context = context,
                listener = this,
                buttons = buttons,
                startAngle = circleStartAngle.toFloat(),
                maxAngle = circleMaxAngle.toFloat(),
                distance = distance,
                isOpened = openOnStart,
                showSelectAnimation = showSelectAnimation
        )
    }

    private val eventListener: EventsListener = EventsListener()
    private var onItemClickListener: ((buttonIndex: Int) -> Unit)? = null
    private var onItemLongClickListener: ((buttonIndex: Int) -> Unit)? = null

    init {
        centerButton.setOnClickListener { toggle() }
        centerButton.onLaidOut(false) {
            menuController.setCenterButtonPosition(centerButton.x, centerButton.y)
        }
        val centerButtonParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        centerButtonParams.gravity = Gravity.CENTER
        addView(centerButton, centerButtonParams)

        val buttonSize = resources.getDimension(R.dimen.circle_menu_button_size)
        val ringRadius = (buttonSize + (distance - buttonSize / 2)).toInt()
        calculatedSize = (ringRadius * 2 * ItemSelectionAnimator.END_CIRCLE_SIZE_RATIO).toInt()
    }

    private fun createButtons(context: Context): List<CircleMenuButton> {
        return colors.mapIndexed { index, color ->
            val button = CircleMenuButton(context)
            button.setIconColor(buttonIconsColor)
            button.setIcon(icons[index])
            button.setColor(color)
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            button.layoutParams = params
            addView(button)
            button
        }
    }

    override fun onButtonClick(menuButton: CircleMenuButton, index: Int) {
        this.onItemClickListener?.invoke(index)
    }

    override fun onButtonLongClick(menuButton: CircleMenuButton, index: Int) {
        this.onItemLongClickListener?.invoke(index)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = resolveSizeAndState(calculatedSize, widthMeasureSpec, 0)
        val h = resolveSizeAndState(calculatedSize, heightMeasureSpec, 0)

        setMeasuredDimension(w, h)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        menuController.onDraw(canvas)
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

    override fun onSelectAnimationStart(buttonIndex: Int) {
        centerButton.setOpened(false)
        centerButton.isClickable = false
        eventListener.onButtonClickAnimationStart?.invoke(buttonIndex)
    }

    override fun onSelectAnimationEnd(buttonIndex: Int) {
        centerButton.isClickable = true
        eventListener.onButtonClickAnimationEnd?.invoke(buttonIndex)
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
        menuController.toggle()
    }

    fun open(animate: Boolean = true) {
        menuController.open(animate)
    }

    fun close(animate: Boolean) {
        menuController.close(animate)
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

    fun onButtonClickAnimationStart(listener: (buttonIndex: Int) -> Unit) {
        eventListener.onButtonClickAnimationStart = listener
    }

    fun onButtonClickAnimationEnd(listener: (buttonIndex: Int) -> Unit) {
        eventListener.onButtonClickAnimationEnd = listener
    }

}