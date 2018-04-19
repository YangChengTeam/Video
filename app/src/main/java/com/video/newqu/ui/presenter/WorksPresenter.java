
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.WorksContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/6/1 15:00
 * 获取用户的上传的视频列表
 */
public class WorksPresenter extends RxPresenter<WorksContract.View> implements WorksContract.Presenter<WorksContract.View> {

    private final Context context;
    private boolean isLoading;
    private boolean isPublicing;
    private boolean isDeletecing;


    public boolean isDeletecing() {
        return isDeletecing;
    }



    public boolean isPublicing() {
        return isPublicing;
    }




    public WorksPresenter(Context context){
        this.context=context;
    }


    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 获取用户已经发布的视频
     * @param userID
     * @param fansID
     * @param page
     * @param pageSize
     */
    @Override
    public void getUpLoadVideoList(String userID,String fansID, String page, String pageSize) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("page",page);
        params.put("page_size",pageSize);
        params.put("visit_user_id",fansID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "list_byUserId", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading=false;
                if(null!=data&&null!=data.getData()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showUpLoadVideoList(data);
                }else if(null!=data&&null!=data.getData()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showUpLoadVideoListEmpty(data);
                }else{
                    if(null!=mView) mView.showUpLoadVideoListError("加载失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 删除视频
     * @param userID
     * @param videoID
     */

    @Override
    public void deleteVideo(String userID, String videoID) {
        if(isDeletecing) return;
        isDeletecing=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "video_del", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isDeletecing=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showDeteleVideoResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 公开视频
     * @param videoID
     * @param userID
     */

    @Override
    public void publicVideo(String videoID, String userID) {

        if(isPublicing) return;
        isPublicing=true;

        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "set_not_private", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isPublicing=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showPublicResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
