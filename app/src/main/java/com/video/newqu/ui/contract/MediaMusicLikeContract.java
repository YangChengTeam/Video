
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MediaMusicCategoryList;

import java.util.List;


public interface MediaMusicLikeContract {

    interface View extends BaseContract.BaseView {
        void showLikeMusicList(List<MediaMusicCategoryList.DataBean> data);
        void showLikeMusicEmpty(String data);
        void showLikeMusicError(String data);
        void showLikeResultResult(String data);
        void showLikeResultError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getLikeMusicList(String categoryID, int page, int pageSize);
        void likeMusic(String musicID, int actionType);
    }
}
