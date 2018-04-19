package com.video.newqu.bean;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/10/17.
 * 这个类用于封装主页和滑动列表视频播放详情界面的关系
 */

public class ChangingViewEvent {

    private int fragmentType;//源Fragment类型

    public boolean isFixedPosition() {
        return isFixedPosition;
    }

    public void setFixedPosition(boolean fixedPosition) {
        isFixedPosition = fixedPosition;
    }

    private boolean isFixedPosition ;//是否定位
    private List<FollowVideoList.DataBean.ListsBean> mListsBeanList;//源数据
    private List< TopicVideoList.DataBean.VideoListBean> mVideoListBeen;//Topic源数据
    private List<UserPlayerVideoHistoryList> mVideoHistoryLists;


    public List<UserPlayerVideoHistoryList> getVideoHistoryLists() {
        return mVideoHistoryLists;
    }

    public void setVideoHistoryLists(List<UserPlayerVideoHistoryList> videoHistoryLists) {
        mVideoHistoryLists = videoHistoryLists;
    }

    public List<TopicVideoList.DataBean.VideoListBean> getVideoListBeen() {
        return mVideoListBeen;
    }

    public void setVideoListBeen(List<TopicVideoList.DataBean.VideoListBean> videoListBeen) {
        mVideoListBeen = videoListBeen;
    }



    private int poistion;//看到第几个视频来了？
    private int page;//看到第几页来了？

    public ChangingViewEvent(){
        super();
    }

    public ChangingViewEvent(int fragmentType, List<FollowVideoList.DataBean.ListsBean> listsBeanList, int poistion, int page) {
        this.fragmentType = fragmentType;
        mListsBeanList = listsBeanList;
        this.poistion = poistion;
        this.page = page;
    }

    public int getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }

    public List<FollowVideoList.DataBean.ListsBean> getListsBeanList() {
        return mListsBeanList;
    }

    public void setListsBeanList(List<FollowVideoList.DataBean.ListsBean> listsBeanList) {
        mListsBeanList = listsBeanList;
    }

    public int getPoistion() {
        return poistion;
    }

    public void setPoistion(int poistion) {
        this.poistion = poistion;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
