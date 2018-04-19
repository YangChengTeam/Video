package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/8/25
 */

public class FilterBean {

    private int filterType;
    private String filterName;

    public FilterBean(int filterType, String filterName) {
        this.filterType = filterType;
        this.filterName = filterName;
    }

    public FilterBean(){
        super();
    }


    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
