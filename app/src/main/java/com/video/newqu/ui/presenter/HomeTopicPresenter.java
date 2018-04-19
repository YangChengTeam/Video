
package com.video.newqu.ui.presenter;

import android.content.Context;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.FindVideoListInfo;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.HomeTopicContract;
import com.video.newqu.util.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/24 09:13
 * 首页话题
 */

public class HomeTopicPresenter extends RxPresenter<HomeTopicContract.View> implements HomeTopicContract.Presenter<HomeTopicContract.View> {

    private final Context context;
    private boolean isLoading=false;

    public HomeTopicPresenter(Context context){
        this.context=context;
    }

    public boolean isLoading() {
        return isLoading;
    }


    /**
     * 获取推荐的话题列表
     * @param userID
     * @param page
     * @param pageSize
     */
    @Override
    public void getHomeTopicDataList(String userID, String page, String pageSize) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("page",page);
        params.put("page_size","10");
        params.put("user_id",userID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "discover",FindVideoListInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FindVideoListInfo>() {
            @Override
            public void call(FindVideoListInfo data) {
                isLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()>0&&null!=data.getData().get(0)){
                    if(null!=mView) mView.showHomeTopicDataList(data);
                }else if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().size()<=0){
                    if(null!=mView) mView.showHomeTopicDataEmpty("没有更多数据");
                }else{
                    if(null!=mView) mView.showHomeTopicDataError("加载错误");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
