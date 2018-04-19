
package com.video.newqu.ui.presenter;

import android.content.Context;

import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.SearchContract;

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
public class SearchPresenter extends RxPresenter<SearchContract.View> implements SearchContract.Presenter<SearchContract.View> {

    private final Context context;

    public SearchPresenter(Context context){
        this.context=context;
    }

    private boolean outIsSearch =false;
    private boolean isSearch =false;

    /**
     * 自动匹配搜索关键字
     * @param key
     */
    @Override
    public void getAutoSearchReachResult(String key) {
        if(outIsSearch) return;
        outIsSearch =true;
        //当前用户是否登录，没登录使用游客ID
        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("keyword",key);
        params.put("type","0");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "search", SearchResultInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SearchResultInfo>() {
            @Override
            public void call(SearchResultInfo data) {
                outIsSearch =false;
                if(null!=data&&null!=data.getData()){
                    if(null!=mView) mView.showAutoSearcRelsult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取搜索结果
     * @param key
     * @param type
     * @param page
     * @param pageSize
     */
    @Override
    public void getSearchReachResult(String key,String type,String page,String pageSize) {
        if(isSearch)return;
        isSearch=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("keyword",key);
        params.put("type",type);
        params.put("page",page);
        params.put("page_size",pageSize);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "search", SearchResultInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SearchResultInfo>() {
            @Override
            public void call(SearchResultInfo data) {
                isSearch=false;
                if(null!=data&&null!=data.getData()) {
                    if(null!=mView) mView.showSearcRelsult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    public boolean isSearch(){
        return isSearch;
    }
    public boolean isOutIsSearch() {
        return outIsSearch;
    }
}
