
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface MakePasswordContract {

    interface View extends BaseContract.BaseView {
        void makePasswordFinlish(String data);
        void makePasswordError(String data);
        void errorView();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void makePassword(String imeil, String account, String passsword, String code, String countryCode);
    }
}
