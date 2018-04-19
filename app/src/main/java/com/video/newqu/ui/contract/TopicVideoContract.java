
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.TopicVideoList;

/**
 * @time 2017/5/24 09:13
 * @des 话题列表
 */
public interface TopicVideoContract {

    interface View extends BaseContract.BaseView {
        void showTopicVideoListFinlish(TopicVideoList data);
        void showTopicVideoListEmpty(String data);
        void showTopicVideoListError(String data);
        void showReportUserResult(String data);
        void showReportVideoResult(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getTopicVideoList(String userID, String topic, String page);
        void onReportUser(String userID, String accuseUserId);
        void onReportVideo(String userID, String videoID);
    }
}
