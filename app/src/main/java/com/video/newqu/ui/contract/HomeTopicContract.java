
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.FindVideoListInfo;


/**
 * @time 2017/5/24 09:13
 * @des 首页话题
 */
public interface HomeTopicContract {

    interface View extends BaseContract.BaseView {
        void showHomeTopicDataList(FindVideoListInfo data);
        void showHomeTopicDataEmpty(String data);
        void showHomeTopicDataError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getHomeTopicDataList(String userID, String page, String pageSize);
    }
}
