
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.UpdataApkInfo;


/**
 * @time 2017/5/24 09:13
 * @des 首页
 */
public interface MainContract {

    interface View extends BaseContract.BaseView {
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void register();
    }
}
