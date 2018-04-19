
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.UserData;
import com.video.newqu.bean.UserDataInfo;


/**
 * @time 2017/5/23 10:50
 * @des 用户登录
 */
public interface LoginContract {

    interface View extends BaseContract.BaseView {
        void showQQWeichatUserData(UserData data);
        void showLoginError(String data);
        void showLoginError();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void qqAndWeichatLogin(UserDataInfo userDataInfo);
    }
}
