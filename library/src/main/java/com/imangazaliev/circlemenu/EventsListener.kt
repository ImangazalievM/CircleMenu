package com.imangazaliev.circlemenu

internal class EventsListener {

    var onMenuOpenAnimationStart: (() -> Unit)? = null
    var onMenuOpenAnimationEnd: (() -> Unit)? = null
    var onMenuCloseAnimationStart: (() -> Unit)? = null
    var onMenuCloseAnimationEnd: (() -> Unit)? = null
    var onButtonClickAnimationStart: ((buttonIndex: Int) -> Unit)? = null
    var onButtonClickAnimationEnd: ((buttonIndex: Int) -> Unit)? = null

}