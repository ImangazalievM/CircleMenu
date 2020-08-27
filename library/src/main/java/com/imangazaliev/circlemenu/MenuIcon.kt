package com.imangazaliev.circlemenu

sealed class MenuIcon {

    abstract val openingAnimatedIcon: Int
    abstract val closingAnimatedIcon: Int

    abstract val openIcon: Int
    abstract val closeIcon: Int

}

class HamburgerMenuIcon : MenuIcon() {

    override val openingAnimatedIcon = R.drawable.avd_menu_to_cross
    override val closingAnimatedIcon = R.drawable.avd_cross_to_menu

    override val openIcon = R.drawable.ic_menu
    override val closeIcon = R.drawable.ic_close

}

class PlusMenuIcon : MenuIcon() {

    override val openingAnimatedIcon = R.drawable.avd_plus_to_cross
    override val closingAnimatedIcon = R.drawable.avd_cross_to_plus

    override val openIcon = R.drawable.ic_plus
    override val closeIcon = R.drawable.ic_close

}