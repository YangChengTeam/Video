
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.NetMessageInfo;

import java.util.List;


/**
 * @time 2017/5/23 10:50
 * @des 用户消息
 */
public interface MessageContract {

    interface View extends BaseContract.BaseView {
        void showMessageInfo(List<NetMessageInfo.DataBean.ListBean> data);
        void showMessageEmpty();
        void showMessageError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getMessageList();
    }
}
