
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.MediaMusicCategoryListContract;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/11/9 15:00
 * 音乐模块，获取音乐分类下的音乐列表
 */
public class MediaMusicCategoryListPresenter extends RxPresenter<MediaMusicCategoryListContract.View> implements MediaMusicCategoryListContract.Presenter<MediaMusicCategoryListContract.View> {

    private final Context context;

    public boolean isLikeIng() {
        return isLikeIng;
    }

    private boolean isLikeIng;

    public boolean isHomeLoading() {
        return isHomeLoading;
    }

    private boolean isHomeLoading;

    public MediaMusicCategoryListPresenter(Context context){
        this.context=context;
    }


    @Override
    public void getCategoryMusicList(String categoryID, int page, int pageSize) {
        if(isHomeLoading) return;
        isHomeLoading=true;
        Map<String,String> params=new ArrayMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("cate_id", categoryID);
        params.put("page", page+"");
        params.put("page_size", pageSize+"");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.MEDIA_EDIT_HOST + "music_lists", MediaMusicCategoryList.class, params,false,false,false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MediaMusicCategoryList>() {
            @Override
            public void call(MediaMusicCategoryList data) {
                isHomeLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0){
                    if(null!=mView) mView.showCategoryMusicList(data.getData());
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()<=0){
                    if(null!=mView) mView.showCategoryMusicEmpty("分类列表为空");
                }else{
                    if(null!=mView) mView.showCategoryMusicError("服务器异常，分类列表加载失败");
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
