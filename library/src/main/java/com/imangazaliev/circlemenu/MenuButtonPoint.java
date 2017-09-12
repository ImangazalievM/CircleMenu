package com.imangazaliev.circlemenu;

class MenuButtonPoint {

    int collapsedX;
    int collapsedY;
    int expandedX;
    int expandedY;
    float angle;

    MenuButtonPoint(int collapsedX, int collapsedY, int expandedX, int expandedY, float angle) {
        this.collapsedX = collapsedX;
        this.collapsedY = collapsedY;
        this.expandedX = expandedX;
        this.expandedY = expandedY;
        this.angle = angle;
    }

}
