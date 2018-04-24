
package com.video.newqu.ui.presenter;

import android.content.Context;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.HotVideoContract;
import com.video.newqu.util.Logger;

import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 检测用户登录
 */

public class HotVideoPresenter extends RxPresenter<HotVideoContract.View> implements HotVideoContract.Presenter<HotVideoContract.View> {

    private final Context context;

    public boolean isLoading() {
        return isLoading;
    }

    private boolean isLoading;

    public HotVideoPresenter(Context context){
        this.context=context;
    }

    @Override
    public void getHotVideoList(final String page, String uid ) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("page",page);
        params.put("page_size","10");
        params.put("user_id",uid);
        params.put("imeil", VideoApplication.mUuid);
        params.put("res_type", VideoApplication.mBuildChanleType+"");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "hot_lists", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading=false;
                if(null!=data&&null!=data.getData()&&null!=data.getData().getLists()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showHotVideoList(data);
                }else if(null!=data&&null!=data.getData()&&null!=data.getData().getLists()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showHotVideoListEmpty("没有更多数据了");
                }else{
                    if(null!=mView) mView.showHotVideoListError("加载失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
