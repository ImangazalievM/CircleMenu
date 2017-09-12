package com.imangazaliev.circlemenu;

public class MenuButtonPoint {

    public int collapsedX;
    public int collapsedY;
    public int expandedX;
    public int expandedY;
    public float angle;

    public MenuButtonPoint(int collapsedX, int collapsedY, int expandedX, int expandedY, float angle) {
        this.collapsedX = collapsedX;
        this.collapsedY = collapsedY;
        this.expandedX = expandedX;
        this.expandedY = expandedY;
        this.angle = angle;
    }

}
