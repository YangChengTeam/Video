
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MediaMusicCategoryList;

import java.util.List;


public interface MediaMusicRecommendContract {

    interface View extends BaseContract.BaseView {
        void showRecommendMusicList(List<MediaMusicCategoryList.DataBean> data);
        void showRecommendMusicEmpty(String data);
        void showRecommendMusicError(String data);
        void showLikeResultResult(String data);
        void showLikeResultError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getRecommendMusicList(String categoryID, int page, int pageSize);
        void likeMusic(String musicID, int actionType);
    }
}
