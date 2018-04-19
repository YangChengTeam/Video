
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.FollowVideoList;


/**
 * @time 2017/5/23 10:50
 * @des 获取收藏的视频列表
 */
public interface FollowListContract {

    interface View extends BaseContract.BaseView {
        void showFollowVideoList(FollowVideoList data);
        void showFollowVideoListEmpty(String data);
        void showFollowVideoListError(String data);
        void showFollowVideoResult(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getFollowVideoList(String uid, String page, String pageSize);
        void followVideo(String videoID);
    }
}
