
package com.video.newqu.ui.presenter;

import android.content.Context;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.TopicVideoList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.SlideVideoPlayerContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/10/19 10:53
 * 一个综合体，包含 热门列表、用户关注的、用户喜欢的、用户发布的视频、话题下的分类列表视频
 */
public class SlideVideoPlayerPresenter extends RxPresenter<SlideVideoPlayerContract.View> implements SlideVideoPlayerContract.Presenter<SlideVideoPlayerContract.View> {

    private final Context context;

    private boolean isLoading;

    public boolean isLoading() {
        return isLoading;
    }

    public SlideVideoPlayerPresenter(Context context){
        this.context=context;
    }

    /**
     * 获取热门视频
     * @param userID
     * @param page
     */
    @Override
    public void getHotVideoList(String userID, int page) {
        if(isLoading) return;
        isLoading =true;
        Map<String,String> params=new HashMap<>();
        params.put("page",page+"");
        params.put("page_size","10");
        params.put("user_id",userID);
        params.put("imeil", VideoApplication.mUuid);
        params.put("res_type", VideoApplication.mBuildChanleType+"");
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "hot_lists", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading =false;
                if(null!=data&&null!=data.getData()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showVideoDataList(data);
                }else if(null!=data&&null!=data.getData()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showVideoDataListEmpty("没有更多视频了");
                }else{
                    if(null!=mView)  mView.showVideoDataListError("加载失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户关注的用户的作品
     * @param userID
     * @param page
     */
    @Override
    public void getFollowUserVideoList(String userID, int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("page",page+"");
        params.put("page_size","10");
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "follows_video_list", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading=false;
                if(null!=data&&null!=data.getData()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showVideoDataList(data);
                }else if(null!=data&&null!=data.getData()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showVideoDataListEmpty("没有更多视频了");
                }else{
                    if(null!=mView) mView.showVideoDataListError("加载失败");//加载失败
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户上传的视频
     * @param userID
     * @param fansID
     * @param page
     */
    @Override
    public void getUserUpLoadVideoList(String userID, String fansID, int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("page",page+"");
        params.put("page_size","20");
        params.put("visit_user_id",fansID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "list_byUserId", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading=false;
                if(null!=data&&null!=data.getData()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showVideoDataList(data);
                }else if(null!=data&&null!=data.getData()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showVideoDataListEmpty("没有更多视频了");
                }else{
                    if(null!=mView) mView.showVideoDataListError("加载失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户收藏的视频
     * @param userID
     * @param page
     */
    @Override
    public void getLikeVideoList(String userID, int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("page",page+"");
        params.put("page_size","15");
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST+"list_by_collect",FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                isLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getLists()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showVideoDataList(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getLists()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showVideoDataListEmpty("没有更多视频了");
                }else{
                    if(null!=mView) mView.showVideoDataListError("加载错误");
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取某个话题下的视频
     * @param userID
     * @param topic
     * @param page
     */
    @Override
    public void getTopicVideoList(String userID, String topic, String page) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("topic",topic);
        params.put("page",page);
        params.put("page_size","10");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "topic_list", TopicVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<TopicVideoList>() {

            @Override
            public void call(TopicVideoList data) {
                isLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getVideo_list()&&data.getData().getVideo_list().size()>0){
                    if(null!=mView) mView.showTopicVideoListFinlish(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getVideo_list()&&data.getData().getVideo_list().size()<=0){
                    if(null!=mView) mView.showTopicVideoListEmpty("没有更多了");
                }else{
                    if(null!=mView) mView.showTopicVideoListError("加载错误");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
