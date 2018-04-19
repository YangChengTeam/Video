
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.FansInfo;
import com.video.newqu.bean.FollowUserList;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface FansContract {

    interface View extends BaseContract.BaseView {
        //粉丝
        void showFansList(FansInfo data);
        void showFansListEmpty(String data);
        void showFansListError(String data);
        //关注
        void showFollowUserList(FollowUserList data);
        void showFollowUserListEmpty(String data);
        void showFollowUserListError(String data);
        void showFollowUser(String text);

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getFanslist(String userID, String page, String pageSize);
        void onFollowUser(String userID, String fansUserID);
        void getFollowUserList(String userID, String page, String pageSize);
    }
}
