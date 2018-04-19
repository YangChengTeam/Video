
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.MediaMusicSearchContract;
import com.video.newqu.util.Logger;

import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/6/1 15:00
 * 搜索音乐内容
 */
public class MediaMusicSearchPresenter extends RxPresenter<MediaMusicSearchContract.View> implements MediaMusicSearchContract.Presenter<MediaMusicSearchContract.View> {

    private final Context context;

    private boolean isSearching=false;
    private boolean isLikeIng;

    public boolean isSearching() {
        return isSearching;
    }

    public boolean isLikeIng() {
        return isLikeIng;
    }

    public MediaMusicSearchPresenter(Context context){
        this.context=context;
    }

    @Override
    public void getMediaSearchResult(String key, int page, int pageSize) {
        if(isSearching) return;
        isSearching=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("keyword",key);
        params.put("page",page+"");
        params.put("page_size",pageSize+"");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.MEDIA_EDIT_HOST + "music_search", MediaMusicCategoryList.class, params,false,false,false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MediaMusicCategoryList>() {
            @Override
            public void call(MediaMusicCategoryList data) {
                isSearching=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0){
                    if(null!=mView) mView.showMediaSearchList(data.getData());
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()<=0){
                    if(null!=mView)mView.showMediaSearchListEmpty("未搜索到相关内容，换个关键词试试！");
                }else{
                    if(null!=mView)mView.showMediaSearchListError("搜索失败，服务器异常");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 收藏和反收藏
     * @param musicID
     * @param actionType 0:收藏 1：取消
     */
    @Override
    public void likeMusic(String musicID, int actionType) {
        if(isLikeIng) return;
        isLikeIng=true;
        Map<String,String> params=new ArrayMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("music_id", musicID);
        params.put("action", actionType+"");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.MEDIA_EDIT_HOST + "music_collect", String.class, params,false,false,false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isLikeIng=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showLikeResultResult(data);
                }else{
                    if(null!=mView) mView.showLikeResultError("收藏失败，服务器异常");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
