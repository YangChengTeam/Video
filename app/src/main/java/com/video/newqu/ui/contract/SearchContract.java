
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.SearchResultInfo;


/**
 * @time 2017/5/23 10:50
 * @des 搜索
 */
public interface SearchContract {

    interface View extends BaseContract.BaseView {
        void showAutoSearcRelsult(SearchResultInfo data);
        void showSearcRelsult(SearchResultInfo data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getAutoSearchReachResult(String key);
        void getSearchReachResult(String key, String type, String page, String pageSize);
    }
}
