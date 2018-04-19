package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.MediaMusicCategoryList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.MediaMusicRecommendContract;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/11/9 15:00
 * 获取推荐的音乐数据
 */
public class MediaMusicRecommendPresenter extends RxPresenter<MediaMusicRecommendContract.View> implements MediaMusicRecommendContract.Presenter<MediaMusicRecommendContract.View> {

    private static final String TAG = MediaMusicRecommendPresenter.class.getSimpleName();
    private final Context context;


    private boolean isHomeLoading;
    private boolean isLikeIng;

    public boolean isLikeIng() {
        return isLikeIng;
    }

    public boolean isHomeLoading() {
        return isHomeLoading;
    }


    public MediaMusicRecommendPresenter(Context context){
        this.context=context;
    }

    @Override
    public void getRecommendMusicList(String categoryID, int page, int pageSize) {
        if(isHomeLoading) return;
        isHomeLoading=true;
        Map<String,String> params=new ArrayMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("page", page+"");
        params.put("page_size", pageSize+"");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.MEDIA_EDIT_HOST + "music_recommend", MediaMusicCategoryList.class, params,false,false,false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MediaMusicCategoryList>() {
            @Override
            public void call(MediaMusicCategoryList data) {
                isHomeLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0){
                    if(null!=mView)mView.showRecommendMusicList(data.getData());
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()<=0){
                    if(null!=mView)mView.showRecommendMusicEmpty("分类列表为空");
                }else{
                    if(null!=mView)mView.showRecommendMusicError("服务器异常，分类列表加载失败");
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
