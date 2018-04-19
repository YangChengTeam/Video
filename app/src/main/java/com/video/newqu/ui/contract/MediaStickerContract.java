package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.StickerNetInfo;

/**
 * @time 2017/5/23 10:50
 * @des 贴纸列表
 */
public interface MediaStickerContract {
    interface View extends BaseContract.BaseView {
        void showStickerList(StickerNetInfo data);
        void showStickerEmpty(String data);
        void showStickerError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getStickerTypeList(String typeID, int page, int pageSize);
    }
}
