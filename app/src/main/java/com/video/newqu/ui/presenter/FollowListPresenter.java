
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.util.Log;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.FollowListContract;
import com.video.newqu.util.Logger;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 用户收藏列表
 */
public class FollowListPresenter extends RxPresenter<FollowListContract.View> implements FollowListContract.Presenter<FollowListContract.View> {

    private final Context context;
    private boolean isLoading=false;
    private boolean isUnFollowing=false;


    public boolean isUnFollowing() {
        return isUnFollowing;
    }



    public FollowListPresenter(Context context){
        this.context=context;
    }


    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 获取收藏视频列表
     * @param uid
     * @param page
     * @param pageSize
     */

    @Override
    public void getFollowVideoList(String uid, String page, String pageSize) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",uid);
        params.put("page",page);
        params.put("page_size",pageSize);
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST+"list_by_collect",FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getLists()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showFollowVideoList(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getLists()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showFollowVideoListEmpty("没有更多数据");
                }else{
                    if(null!=mView) mView.showFollowVideoListError("加载错误");
                }
            }
        });
        addSubscrebe(subscribe);
    }


    /**
     * 收藏视频
     * @param videoID
     */
    @Override
    public void followVideo(String videoID) {
        if(isUnFollowing) return;
        isUnFollowing=true;

        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("video_id",videoID);

        HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "collect", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String result) {
                isUnFollowing=false;
                if(null!=mView) mView.showFollowVideoResult(result);
            }
        });
    }
}
