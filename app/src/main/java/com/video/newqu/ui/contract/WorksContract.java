
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.FollowVideoList;


/**
 * @time 2017/5/23 10:50
 * @des 用户编辑自己的资料上传
 */
public interface WorksContract {

    interface View extends BaseContract.BaseView {
        void showUpLoadVideoList(FollowVideoList data);
        void showUpLoadVideoListEmpty(FollowVideoList data);
        void showUpLoadVideoListError(String data);
        void showDeteleVideoResult(String data);
        void showPublicResult(String result);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getUpLoadVideoList(String userID, String fansID, String page, String pageSize);
        void deleteVideo(String userID, String videoID);
        void publicVideo(String videoID, String userID);
    }
}
