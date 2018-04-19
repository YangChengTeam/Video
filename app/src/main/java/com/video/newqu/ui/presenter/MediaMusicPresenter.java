
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.util.ArrayMap;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.MediaMusicHomeMenu;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.MediaMusicContract;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/11/9 15:00
 * 音乐模块，获取音乐分类列表
 */
public class MediaMusicPresenter extends RxPresenter<MediaMusicContract.View> implements MediaMusicContract.Presenter<MediaMusicContract.View> {

    private final Context context;

    public boolean isHomeLoading() {
        return isHomeLoading;
    }

    private boolean isHomeLoading;

    public MediaMusicPresenter(Context context){
        this.context=context;
    }

    @Override
    public void getHomeMusicData() {
        if(isHomeLoading) return;
        isHomeLoading=true;
        Map<String,String> params=new ArrayMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.MEDIA_EDIT_HOST + "music_types", MediaMusicHomeMenu.class, params,false,false,false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MediaMusicHomeMenu>() {
            @Override
            public void call(MediaMusicHomeMenu data) {
                isHomeLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0){
                    if(null!=mView) mView.showMusicCategoryList(data.getData());
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()<=0){
                    if(null!=mView) mView.showMusicCategoryEmpty("分类列表为空");
                }else{
                    if(null!=mView) mView.showMusicCategoryError("分类列表加载失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
