package com.video.newqu.ui.contract;

import com.video.newqu.base.BaseContract;
import com.video.newqu.bean.MediaMusicInfo;
import com.video.newqu.bean.CaptionsInfo;
import com.video.newqu.bean.StickerListInfo;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface MediaEditContract {

    interface View extends BaseContract.BaseView {
        //贴纸
        void showStickerList(StickerListInfo stickerListInfoList);
        void showStickerEmpty(String data);
        void showStickerError(String data);
        //字幕
        void showCaptionsList(CaptionsInfo data);
        void showCaptionsEmpty(String data);
        void showCaptionsError(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getStickerList();
        void getCaptionsList();
    }
}
