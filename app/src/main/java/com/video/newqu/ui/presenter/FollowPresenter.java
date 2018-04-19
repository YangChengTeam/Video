
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.FollowContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 获取关注着
 */
public class FollowPresenter extends RxPresenter<FollowContract.View> implements FollowContract.Presenter<FollowContract.View> {

    private final Context context;
    private boolean isLoading=false;
    private boolean isHotLoading=false;
    public FollowPresenter(Context context){
        this.context=context;
    }


    public boolean isLoading() {
        return isLoading;
    }


    /**
     * 获取关注的作者视频列表
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

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "follows_video_list", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading=false;
                if(null!=data&&null!=data.getData()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showloadFollowVideoList(data);
                }else if(null!=data&&null!=data.getData()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showloadFollowListEmptry("没有更多数据");
                }else{
                    if(null!=mView) mView.showloadFollowListError();//加载失败
                }
            }
        });

        addSubscrebe(subscribe);
    }


    /**
     * 获取热门视频，热门视频详情列表会用到
      * @param page
     * @param uid
     */
    @Override
    public void getHotVideoList(String page, String uid) {
        if(isHotLoading) return;
        isHotLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("page",page);
        params.put("page_size","10");
        params.put("user_id",uid);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "hot_lists", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isHotLoading=false;
                if(null!=data&&null!=data.getData()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showHotVideoList(data);
                }else if(null!=data&&null!=data.getData()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showHotVideoListEmpty("没有更多数据");
                }else{
                    if(null!=mView) mView.showHotVideoListError("加载错误");
                }
            }
        });

        addSubscrebe(subscribe);
    }

    /**
     * 举报用户
     * @param userID
     * @param accuseUserId
     */
    @Override
    public void onReportUser(String userID, String accuseUserId) {

        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("accuse_user_id",accuseUserId);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "accuse_user", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showReportUserResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 举报视频
     * @param userID
     * @param videoID
     */
    @Override
    public void onReportVideo(String userID, String videoID) {
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "accuse_video", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showReportVideoResult(data);
                }
            }
        });

        addSubscrebe(subscribe);
    }


}
