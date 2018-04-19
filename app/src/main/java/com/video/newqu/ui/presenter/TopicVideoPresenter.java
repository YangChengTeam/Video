
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.TopicVideoList;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.TopicVideoContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 话题列表
 */
public class TopicVideoPresenter extends RxPresenter<TopicVideoContract.View> implements TopicVideoContract.Presenter<TopicVideoContract.View> {

    private final Context context;
    private boolean isLoading=false;

    public TopicVideoPresenter(Context context){
        this.context=context;
    }

    public boolean isLoading() {
        return false;
    }


    @Override
    public void getTopicVideoList(String userID, String topic,String page) {
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
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });

        addSubscrebe(subscribe);
    }
}
