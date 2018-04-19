
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.bean.SearchResultInfo;

import java.util.List;


/**
 * @time 2017/5/23 10:50
 * @des 音乐搜索
 */
public interface MediaMusicSearchContract {

    interface View extends BaseContract.BaseView {
        void showMediaSearchList(List<MediaMusicCategoryList.DataBean> data);
        void showMediaSearchListEmpty(String data);
        void showMediaSearchListError(String data);

        void showLikeResultResult(String data);
        void showLikeResultError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getMediaSearchResult(String key, int page, int pageSize);

        void likeMusic(String musicID, int actionType);
    }
}
