package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2017/10/13.
 * 消息
 */

public class VideoEventMessage {

    private String message;
    private FollowVideoList.DataBean.ListsBean mListsBean;
    private UserPlayerVideoHistoryList data;
    private TopicVideoList.DataBean.VideoListBean mVideoListBean;
    private int poistion;


    public UserPlayerVideoHistoryList getData() {
        return data;
    }

    public void setData(UserPlayerVideoHistoryList data) {
        this.data = data;
    }


    public TopicVideoList.DataBean.VideoListBean getVideoListBean() {
        return mVideoListBean;
    }

    public void setVideoListBean(TopicVideoList.DataBean.VideoListBean videoListBean) {
        mVideoListBean = videoListBean;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FollowVideoList.DataBean.ListsBean getListsBean() {
        return mListsBean;
    }

    public void setListsBean(FollowVideoList.DataBean.ListsBean listsBean) {
        mListsBean = listsBean;
    }

    public int getPoistion() {
        return poistion;
    }

    public void setPoistion(int poistion) {
        this.poistion = poistion;
    }
}
