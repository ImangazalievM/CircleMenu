package com.imangazaliev.circlemenu

internal class EventsListener {

    var onMenuOpenAnimationStart: (() -> Unit)? = null
    var onMenuOpenAnimationEnd: (() -> Unit)? = null
    var onMenuCloseAnimationStart: (() -> Unit)? = null
    var onMenuCloseAnimationEnd: (() -> Unit)? = null
    var onButtonClickAnimationStart: ((menuButton: CircleMenuButton) -> Unit)? = null
    var onButtonClickAnimationEnd: ((menuButton: CircleMenuButton) -> Unit)? = null

}