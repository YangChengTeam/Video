
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.UserPlayerVideoHistoryList;

import java.util.List;

/**
 * @time 2017/5/23 10:50
 * @des 用户观看的视频记录
 */

public interface UserHistoryContract {

    interface View extends BaseContract.BaseView {
        void showVideoHistoryList(List<UserPlayerVideoHistoryList> data);
        void showVideoHistoryListEmpty(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getVideoHistoryList(int page, int pageSize);
        void getAllVideoHistoryList();
    }
}
