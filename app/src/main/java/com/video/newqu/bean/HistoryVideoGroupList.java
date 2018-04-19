package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/3/26.
 * 根据日期对列表分组
 */

public class HistoryVideoGroupList implements Serializable{

    private String dateTime;
    private List<UserPlayerVideoHistoryList> mListsBeans;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<UserPlayerVideoHistoryList> getListsBeans() {
        return mListsBeans;
    }

    public void setListsBeans(List<UserPlayerVideoHistoryList> listsBeans) {
        mListsBeans = listsBeans;
    }
}
