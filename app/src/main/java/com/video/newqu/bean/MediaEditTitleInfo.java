package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/9/9.
 */

public class MediaEditTitleInfo {

    private String title;
    private int icon;
    private boolean isSelector;

    public MediaEditTitleInfo(){
        super();
    }

    public MediaEditTitleInfo(String title, int icon, boolean isSelector) {
        this.title = title;
        this.icon = icon;
        this.isSelector = isSelector;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }


}
