package com.video.newqu.bean;


/**
 * TinyHung@Outlook.com
 * 2017/9/9.
 */

public class MediaFilterInfo {

    private String title;
    private int icon;
    private boolean isSelector;
    private int id;


    public MediaFilterInfo(){
        super();
    }
    public MediaFilterInfo(String title, int icon, boolean isSelector,int id) {
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
