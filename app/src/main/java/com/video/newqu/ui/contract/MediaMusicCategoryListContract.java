
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MediaMusicCategoryList;
import java.util.List;


public interface MediaMusicCategoryListContract {

    interface View extends BaseContract.BaseView {
        void showCategoryMusicList(List<MediaMusicCategoryList.DataBean> data);
        void showCategoryMusicEmpty(String data);
        void showCategoryMusicError(String data);

        void showLikeResultResult(String data);
        void showLikeResultError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getCategoryMusicList(String categoryID, int page, int pageSize);
        void likeMusic(String musicID, int actionType);
    }
}
