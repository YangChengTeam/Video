package com.video.newqu.ui.presenter;

import android.content.Context;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.MediaMusicInfo;
import com.video.newqu.bean.CaptionsInfo;
import com.video.newqu.bean.StickerListInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.MediaEditContract;
import com.video.newqu.util.Logger;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 用户收藏列表
 */
public class MediaEditPresenter extends RxPresenter<MediaEditContract.View> implements MediaEditContract.Presenter<MediaEditContract.View> {

    private final Context context;



    private boolean isStickerLoading =false;
    private boolean isCaptionsLoading =false;

    public boolean isStickerLoading() {
        return isStickerLoading;
    }


    public MediaEditPresenter(Context context){
        this.context=context;
    }



    /**
     * 获取贴纸所有分类
     */
    @Override
    public void getStickerList() {
        if(isStickerLoading) return;
        isStickerLoading =true;
        Subscription subscribe = HttpCoreEngin.get(context).rxget(NetContants.MEDIA_EDIT_HOST + "sticker_types",StickerListInfo.class, null, false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<StickerListInfo>() {
            @Override
            public void call(StickerListInfo data) {
                isStickerLoading =false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0){
                    if(null!=mView) mView.showStickerList(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()==0){
                    if(null!=mView) mView.showStickerEmpty("获取贴纸列表为空");
                }else{
                    if(null!=mView) mView.showStickerError("获取贴纸列表失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取字幕素材所有列表
     */
    @Override
    public void getCaptionsList() {

        if(isCaptionsLoading) return;
        isCaptionsLoading =true;

        Subscription subscribe = HttpCoreEngin.get(context).rxget(NetContants.BASE_HOST + "captions_material",CaptionsInfo.class, null, false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CaptionsInfo>() {
            @Override
            public void call(CaptionsInfo data) {
                isStickerLoading =false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0){
                    if(null!=mView) mView.showCaptionsList(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()==0){
                    if(null!=mView) mView.showCaptionsEmpty("获取贴纸列表为空");
                }else{
                    if(null!=mView) mView.showCaptionsError("获取贴纸列表失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
