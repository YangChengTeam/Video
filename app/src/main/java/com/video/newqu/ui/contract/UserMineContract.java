
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MineUserInfo;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface UserMineContract {

    interface View extends BaseContract.BaseView {
        void showUserInfo(MineUserInfo data);
        void showPostImageBGResult(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getUserInfo(String userID);
        void onPostImageBG(String userID, String filePath);
    }
}
