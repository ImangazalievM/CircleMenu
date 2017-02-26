package com.imangazaliev.circlemenu;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        MenuState.EXPANDED,
        MenuState.COLLAPSED,
        MenuState.EXPAND_ANIMATION_STARTED,
        MenuState.COLLAPSE_ANIMATION_STARTED,
        MenuState.SELECT_ANIMATION_STARTED,
        MenuState.SELECT_ANIMATION_FINISHED,
        MenuState.EXIT_ANIMATION_STARTED,
        MenuState.EXIT_ANIMATION_FINISHED
})
public @interface MenuState {

    int EXPAND_ANIMATION_STARTED = 1;
    int EXPANDED = 2;
    int COLLAPSE_ANIMATION_STARTED = 3;
    int COLLAPSED = 4;
    int SELECT_ANIMATION_STARTED = 5;
    int SELECT_ANIMATION_FINISHED = 6;
    int EXIT_ANIMATION_STARTED = 7;
    int EXIT_ANIMATION_FINISHED = 8;

}