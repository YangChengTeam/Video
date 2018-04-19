package com.video.newqu.bean;


import android.graphics.drawable.Drawable;

/**
 * TinyHung@Outlook.com
 * 2017/9/9.
 */

public class MediaMVInfo {

    private String title;
    private Drawable icon;
    private boolean isSelector;
    private int id;


    public MediaMVInfo(){
        super();
    }
    public MediaMVInfo(String title, Drawable icon, boolean isSelector, int id) {
        this.title = title;
        this.icon = icon;
        this.id=id;
        this.isSelector = isSelector;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }
}
