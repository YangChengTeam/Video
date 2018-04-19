
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.SearchResultInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.SearchUserContract;
import com.video.newqu.util.ToastUtils;

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
public class SearchUserPresenter extends RxPresenter<SearchUserContract.View> implements SearchUserContract.Presenter<SearchUserContract.View> {

    private final Context context;
    public SearchUserPresenter(Context context){
        this.context=context;
    }


    /**
     * 加载更多用户
     */
    @Override
    public void getMoreUserList(String key,String type,String page,String pageSize) {

        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("keyword",key);
        params.put("type",type);
        params.put("page",page);
        params.put("page_size",pageSize);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "search", SearchResultInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SearchResultInfo>() {
            @Override
            public void call(SearchResultInfo data) {
                if(null!=data&&null!=data.getData()&&null!=data.getData().getUser_list()&&data.getData().getUser_list().size()>0) {
                    if(null!=mView) mView.showMoreUserList(data);
                }else if(null!=data&&null!=data.getData()&&null!=data.getData().getUser_list()&&data.getData().getUser_list().size()<=0){
                    if(null!=mView) mView.showMoreUserListEmpty("没有更多视频");
                }else{
                    if(null!=mView) mView.showMoreUserListError();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 关注
     * @param userID
     * @param fansUserID
     */
    @Override
    public void onFollowUser(String userID, String fansUserID) {
        Map<String,String> params=new HashMap<>();

        params.put("user_id",userID);
        params.put("fans_user_id",fansUserID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "follow", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showFollowUser(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
