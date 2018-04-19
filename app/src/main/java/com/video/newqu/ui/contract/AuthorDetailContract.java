
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.MineUserInfo;


/**
 * @time 2017/5/23 10:50
 * @des 用户中心
 */
public interface AuthorDetailContract {

    interface View extends BaseContract.BaseView {
        void showUserInfo(MineUserInfo data, String userID);
        void showFollowUser(Boolean isFollow, String text);
        void showReportUserResult(String data);
        void showUpLoadVideoList(FollowVideoList data);
        void showUpLoadVideoListEmpty(String data);
        void showUpLoadVideoListError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getUserInfo(String userID);
        void onFollowUser(String userID, String fansUserID);
        void onReportUser(String userID, String accuseUserId);
        void getUpLoadVideoList(String userID, String fansID, String page, String pageSize);
    }
}
