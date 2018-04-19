
package com.video.newqu.ui.presenter;

import android.app.Activity;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.SingComentInfo;
import com.video.newqu.bean.VideoInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.VideoDetailsContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 用户留言，分享，收藏，举报等
 */

public class VideoDetailsPresenter extends RxPresenter<VideoDetailsContract.View> implements VideoDetailsContract.Presenter<VideoDetailsContract.View> {

    private final Activity context;
    private boolean isLoading;
    private boolean isReportVideo;
    private boolean isPriseVideo;
    private boolean isReportUser;
    private boolean isPrivateVideo;
    private boolean isDownloadPermiss;
    private boolean isDeteleVideo;
    private boolean isFollowUser;
    public boolean isFollowUser() {
        return isFollowUser;
    }
    public boolean isPrivateVideo() {
        return isPrivateVideo;
    }
    public boolean isDownloadPermiss() {
        return isDownloadPermiss;
    }
    public boolean isDeteleVideo() {
        return isDeteleVideo;
    }
    public boolean isReportUser() {
        return isReportUser;
    }
    public boolean isReportVideo() {
        return isReportVideo;
    }
    public boolean isPriseVideo() {
        return isPriseVideo;
    }
    public boolean isLoading() {
        return isLoading;
    }


    public VideoDetailsPresenter(Activity context){
        this.context=context;
    }

    private String tempVideoID;
    /**
     * 获取视频评论列表
     * @param videoID
     * @param page
     * @param pageSize
     */
    @Override
    public void getComentList(String videoID, String page, String pageSize) {
        if(isLoading) return;
        isLoading=true;
        this.tempVideoID=videoID;
        Map<String,String> params=new HashMap<>();
        params.put("video_id",videoID);
        params.put("page",page);
        params.put("page_size",pageSize);
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "comments", ComentList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ComentList>() {
            @Override
            public void call(ComentList data) {
                isLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getComment_list()&&data.getData().getComment_list().size()>0){
                    if(null!=mView) mView.showComentList(tempVideoID,data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getComment_list()&&data.getData().getComment_list().size()<=0){
                    if(null!=mView) mView.showComentListEmpty("没有更多了");
                }else{
                    if(null!=mView) mView.showComentListError();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 增加留言
     * @param userID
     * @param video_id
     * @param wordsmMessage
     */
    @Override
    public void addComentMessage(String userID, String video_id, String wordsmMessage,String toUserID) {

        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",video_id);
        params.put("comment",wordsmMessage);
        params.put("to_user_id",toUserID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "add_comment", SingComentInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SingComentInfo>() {
            @Override
            public void call(SingComentInfo data) {
                if(null!=data&&null!=data.getData()&&null!=data.getData().getInfo()){
                    if(null!=mView) mView.showAddComentRelult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }



    /**
     * 对视频点赞
     * @param videoID
     * @param fansUserID
     */
    @Override
    public void onPriseVideo(String videoID, String fansUserID) {
        if(isPriseVideo) return;
        isPriseVideo=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",fansUserID);
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "collect", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isPriseVideo=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showPriseResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 关注某个用户
     * @param userID
     * @param fansUserID
     */
    @Override
    public void onFollowUser(String userID, String fansUserID) {
        if(isFollowUser)return;
        isFollowUser=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("fans_user_id",fansUserID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "follow", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isFollowUser=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showFollowUserResult(data);
                }else{
                    if(null!=mView)  mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 播统计放次数
     * @param userID
     * @param videoID
     */
    @Override
    public void postPlayCount(String userID, String videoID) {
        Map<String,String> params=new HashMap<>();
        params.put("video_id",videoID);
        params.put("user_id",userID);
        params.put("imeil", VideoApplication.mUuid);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "play_record", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showPostPlayCountResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 根据视频ID获取视频详细信息
     * @param visitUserID
     * @param videoID
     */
    @Override
    public void getVideoInfo(String visitUserID, String videoAuthorID,String videoID) {

        Map<String,String> params=new HashMap<>();
        params.put("visit_user_id",visitUserID);
        params.put("video_id",videoID);
        params.put("user_id",videoAuthorID);//作者ID
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "video_info", VideoInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<VideoInfo>() {
            @Override
            public void call(VideoInfo data) {
                if(null!=data&&1==data.getCode()&&null!=data.getData()){
                    if(null!=mView) mView.showVideoInfoResult(data);
                }else{
                    if(null!=mView) mView.showLoadVideoInfoError();
                }
            }
        });
        addSubscrebe(subscribe);
    }


    /**
     * 举报某个用户
     * @param userID
     * @param accuseUserId
     */
    @Override
    public void onReportUser(String userID, String accuseUserId) {
        if(isReportUser) return;
        isReportUser=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("accuse_user_id",accuseUserId);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "accuse_user", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isReportUser=false;
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
     * 举报某个视频
     * @param userID
     * @param videoID
     */
    @Override
    public void onReportVideo(String userID, String videoID) {
        if(isReportVideo) return;
        isReportVideo=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("source_user_id",VideoApplication.getLoginUserID());
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "accuse_video", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isReportVideo=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showReportVideoResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }

            }
        });
        addSubscrebe(subscribe);
    }


    /**
     * 删除某个视频
     * @param userID
     * @param videoID
     */
    @Override
    public void deleteVideo(String userID, String videoID) {
        if(isDeteleVideo) return;
        isDeteleVideo=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "video_del", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isDeteleVideo=false;
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
     * 公开、隐藏 某个视频
     * @param videoID
     * @param userID
     */
    //set_not_private
    @Override
    public void setVideoPrivateState(String videoID, String userID) {
        if(isPrivateVideo) return;
        isPrivateVideo=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "set_private", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isPrivateVideo=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView)  mView.showSetVideoPrivateStateResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 改变某个视频的下载权限
     * @param videoID
     * @param userID
     */
    @Override
    public void changeVideoDownloadPermission(String videoID, String userID) {

        if(isDownloadPermiss) return;
        isDownloadPermiss=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",videoID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "download_permiss", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isDownloadPermiss=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showChangeVideoDownloadPermissionResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

}
