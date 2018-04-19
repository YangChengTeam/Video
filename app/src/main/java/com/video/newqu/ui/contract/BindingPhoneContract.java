
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.bean.UserData;
import com.video.newqu.bean.UserDataInfo;


/**
 * @time 2017/5/23 10:50
 * @des 验证码获取
 */
public interface BindingPhoneContract {

    interface View extends BaseContract.BaseView {
        void showBindingPhoneResult(String data);
        void showBindingPhoneError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void bindingPhone(String phoneNumber, String code);
    }
}
