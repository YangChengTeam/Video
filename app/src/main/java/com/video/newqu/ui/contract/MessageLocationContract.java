
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MessageListInfo;
import com.video.newqu.bean.NotifactionMessageInfo;

import java.util.List;


/**
 * @time 2017/5/23 10:50
 * @des 读取本地消息
 */
public interface MessageLocationContract {

    interface View extends BaseContract.BaseView {
        void showMessageList(List<NotifactionMessageInfo> messageListInfos);
        void getMessageError(String data);
        void getMessageEmpty(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getMessageList();
    }
}
