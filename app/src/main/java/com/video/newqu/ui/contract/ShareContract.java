
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;


/**
 * @time 2017/5/24 09:13
 * @des 分享
 */
public interface ShareContract {

    interface View extends BaseContract.BaseView {
        void showShareResulet(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void shareResult(String userID, String videoID, String type);
    }
}
