package com.imangazaliev.circlemenu

internal interface MenuControllerListener {

    fun onOpenAnimationStart()

    fun onOpenAnimationEnd()

    fun onCloseAnimationStart()

    fun onCloseAnimationEnd()

    fun onSelectAnimationStart(menuButton: CircleMenuButton)

    fun onSelectAnimationEnd(menuButton: CircleMenuButton)

    fun redrawView()

}