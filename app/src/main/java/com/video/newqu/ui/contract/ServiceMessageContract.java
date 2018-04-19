
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.bean.PhotoInfo;
import com.video.newqu.bean.UserData;
import com.video.newqu.bean.UserDataInfo;

import java.util.List;


/**
 * @time 2017/5/23 10:50
 * @des 用户登录
 */
public interface ServiceMessageContract {

    interface View extends BaseContract.BaseView {
        void showSendMessageError(String data);
        void showSendMessageResult(String data);
        void onUpdataProgress(float progress);
        void showCutImageIng(String data);
        void showStartUpdata(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
         void sendMessage(String userID, String content, String phoneNumber, List<PhotoInfo> infoList);
    }
}
