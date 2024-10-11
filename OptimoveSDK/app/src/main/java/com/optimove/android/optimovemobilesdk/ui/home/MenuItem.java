package com.optimove.android.optimovemobilesdk.ui.home;

public class MenuItem {

    private int image;
    private String text;
    int actionId = 0;

    public MenuItem(String text, int image, int actionId) {
        this.image = image;
        this.text = text;
        this.actionId = actionId;
    }

    public int getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public int getActionId() {
        return actionId;
    }
}
