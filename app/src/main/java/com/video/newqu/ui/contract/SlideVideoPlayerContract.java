package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.TopicVideoList;


/**
 * @time 2017/10/19 10:50
 * @des 视频播放滑动界面的数据源获取
 */
public interface SlideVideoPlayerContract {

    interface View extends BaseContract.BaseView {
        //通用的数据类型
        void showVideoDataList(FollowVideoList data);
        void showVideoDataListEmpty(String data);
        void showVideoDataListError(String data);
        //话题列表数据类型要分开处理
        void showTopicVideoListFinlish(TopicVideoList data);
        void showTopicVideoListEmpty(String data);
        void showTopicVideoListError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //热门
        void getHotVideoList(String userID, int page);
        //关注
        void getFollowUserVideoList(String userID, int page);
        //用户发布的作品
        void getUserUpLoadVideoList(String userID, String fansID, int page);
        //用户收藏的视频
        void getLikeVideoList(String userID, int page);
        //获取话题下的列表
        void getTopicVideoList(String userID, String topic, String page);
    }
}
