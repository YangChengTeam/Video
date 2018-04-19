
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.SearchResultInfo;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface SearchUserContract {

    interface View extends BaseContract.BaseView {
        void showMoreUserListEmpty(String data);
        void showMoreUserListError();
        void showMoreUserList(SearchResultInfo data);
        void showFollowUser(String text);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getMoreUserList(String key, String type, String page, String pageSize);
        void onFollowUser(String userID, String fansUserID);
    }
}
