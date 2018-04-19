package com.video.newqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/3/26.
 * 根据日期对列表分组
 */

public class VideoGroupList implements Serializable{

    private String dateTime;
    private List<FollowVideoList.DataBean.ListsBean> mListsBeans;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<FollowVideoList.DataBean.ListsBean> getListsBeans() {
        return mListsBeans;
    }

    public void setListsBeans(List<FollowVideoList.DataBean.ListsBean> listsBeans) {
        mListsBeans = listsBeans;
    }
}
