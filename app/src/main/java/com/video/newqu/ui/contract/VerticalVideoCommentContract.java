
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.SingComentInfo;

/**
 * @time 2017/5/23 10:50
 * @des 留言列表
 */
public interface VerticalVideoCommentContract {

    interface View extends BaseContract.BaseView {
        void showComentList(ComentList data);
        void showComentListEmpty(String data);
        void showComentListError();
        void showAddComentRelult(SingComentInfo data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getComentList(String videoID, String page, String pageSize);
        void addComentMessage(String id, String video_id, String wordsmMessage, String toUserID);
    }
}
