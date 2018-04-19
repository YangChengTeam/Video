
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;

/**
 * @time 2017/5/23 10:50
 * @des 用户编辑自己的资料上传
 */
public interface UserEditContract {

    interface View extends BaseContract.BaseView {
        void showPostUserDataResult(String data);
        void showPostImagePhotoResult(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void onPostUserData(String userID, String nikeName, String sex, String desp);
        void onPostImagePhoto(String userID, String filePathm);
    }
}
