package com.video.newqu.bean;

/**
 * TinyHung@outlook.com
 * 2017-05-29 1:50
 * 我的界面标题栏
 */

public class MineTabInfo {
    private String titleName;
    private int aboutCount;
    private boolean isSelector;

    public MineTabInfo(){
        super();
    }

    public MineTabInfo(String titleName, int aboutCount, boolean isSelector) {
        this.titleName = titleName;
        this.aboutCount = aboutCount;
        this.isSelector = isSelector;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }


    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getAboutCount() {
        return aboutCount;
    }

    public void setAboutCount(int aboutCount) {
        this.aboutCount = aboutCount;
    }
}
