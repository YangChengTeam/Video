
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.SearchResultInfo;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface SearchVideoContract {

    interface View extends BaseContract.BaseView {

        void showReportUserResult(String data);
        void showReportVideoResult(String data);
        void showMoreVideoListEmpty(String data);
        void showMoreVideoListError();
        void showMoreVideoList(SearchResultInfo data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void onReportUser(String userID, String accuseUserId);
        void onReportVideo(String userID, String videoID);
        void getMoreVideoList(String key, String type, String page, String pageSize);
    }
}
