
package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MediaMusicHomeMenu;
import java.util.List;

import static android.R.attr.data;


public interface MediaMusicContract {

    interface View extends BaseContract.BaseView {
        void showMusicCategoryList(List<MediaMusicHomeMenu.DataBean> data);
        void showMusicCategoryEmpty(String data);
        void showMusicCategoryError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getHomeMusicData();
    }
}
