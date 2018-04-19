
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.SearchVideoContract;

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
public class SearchVideoPresenter extends RxPresenter<SearchVideoContract.View> implements SearchVideoContract.Presenter<SearchVideoContract.View> {

    private final Context context;

    public SearchVideoPresenter(Context context){
        this.context=context;
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
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 加载更多视频
     */
    @Override
    public void getMoreVideoList(String key,String type,String page,String pageSize) {

        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("keyword",key);
        params.put("type",type);
        params.put("page",page);
        params.put("page_size",pageSize);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "search", SearchResultInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SearchResultInfo>() {
            @Override
            public void call(SearchResultInfo data) {
                if(null!=data&&null!=data.getData()&&null!=data.getData().getVideo_list()&&data.getData().getVideo_list().size()>0) {
                    if(null!=mView) mView.showMoreVideoList(data);
                }else if(null!=data&&null!=data.getData()&&null!=data.getData().getVideo_list()&&data.getData().getVideo_list().size()<=0){
                    if(null!=mView) mView.showMoreVideoListEmpty("没有更多视频");
                }else{
                    if(null!=mView) mView.showMoreVideoListError();
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
