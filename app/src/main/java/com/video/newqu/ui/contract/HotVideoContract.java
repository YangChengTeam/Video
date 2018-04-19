
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.FollowVideoList;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface HotVideoContract {

    interface View extends BaseContract.BaseView {
        void showHotVideoList(FollowVideoList data);
        void showHotVideoListEmpty(String data);
        void showHotVideoListError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getHotVideoList(String page, String uid);
    }
}
