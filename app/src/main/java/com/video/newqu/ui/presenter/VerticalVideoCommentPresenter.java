
package com.video.newqu.ui.presenter;

import android.app.Activity;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.ComentList;
import com.video.newqu.bean.SingComentInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.VerticalVideoCommentContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 留言相关
 */

public class VerticalVideoCommentPresenter extends RxPresenter<VerticalVideoCommentContract.View> implements VerticalVideoCommentContract.Presenter<VerticalVideoCommentContract.View> {

    private final Activity context;
    private boolean isLoading;
    private boolean isAddComment;

    public boolean isAddComment() {
        return isAddComment;
    }

    public boolean isLoading() {
        return isLoading;
    }


    public VerticalVideoCommentPresenter(Activity context){
        this.context=context;
    }


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
        Map<String,String> params=new HashMap<>();
        params.put("video_id",videoID);
        params.put("page",page);
        params.put("page_size",pageSize);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "comments", ComentList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ComentList>() {
            @Override
            public void call(ComentList data) {
                isLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().getComment_list().size()>0){
                    if(null!=mView) mView.showComentList(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().getComment_list().size()<=0){
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
        if(isAddComment) return;
        isAddComment=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("video_id",video_id);
        params.put("comment",wordsmMessage);
        params.put("to_user_id",toUserID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "add_comment", SingComentInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SingComentInfo>() {
            @Override
            public void call(SingComentInfo data) {
                isAddComment=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getInfo()){
                    if(null!=mView) mView.showAddComentRelult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
