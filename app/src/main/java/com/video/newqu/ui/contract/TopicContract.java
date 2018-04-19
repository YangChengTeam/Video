
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.TopicList;


/**
 * @time 2017/5/24 09:13
 * @des 话题列表
 */
public interface TopicContract {

    interface View extends BaseContract.BaseView {
        void showTopicListFinlish(TopicList data);
        void showTopicListEmpty(String data);
        void showTopicListError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getTopicList();
    }
}
