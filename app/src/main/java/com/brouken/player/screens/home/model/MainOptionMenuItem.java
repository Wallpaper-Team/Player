package com.brouken.player.screens.home.model;

public class MainOptionMenuItem {
    private final int resId;
    private final int titleId;
    private final int descriptionId;

    public MainOptionMenuItem(int resId, int titleId, int descriptionId) {
        this.resId = resId;
        this.titleId = titleId;
        this.descriptionId = descriptionId;
    }

    public int getResId() {
        return resId;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }
}
