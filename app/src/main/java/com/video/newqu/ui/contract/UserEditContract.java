
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import java.io.File;

/**
 * @time 2017/5/23 10:50
 * @des 用户编辑自己的资料上传
 */
public interface UserEditContract {

    interface View extends BaseContract.BaseView {
        void showPostUserDataResult(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void onPostUserData(String userID, String nikeName, String sex, String desp,String province,String city,String birthday,File file);
        void onPostImagePhoto(String userID, String filePathm);
    }
}
