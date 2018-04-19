package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/9/12
 */

public class MediaSoundFilter {

    private int icon;
    private String name;
    private  boolean  isSelector;

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
