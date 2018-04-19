
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.FollowVideoList;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.AuthorDetailContract;
import com.video.newqu.util.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;
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
public class AuthorDetailPresenter extends RxPresenter<AuthorDetailContract.View> implements AuthorDetailContract.Presenter<AuthorDetailContract.View> {

    private final Context context;
    private boolean loadInfo=false;
    private boolean isFollow=false;
    private String mUserID=null;

    public AuthorDetailPresenter(Context context){
        this.context=context;
    }


    public boolean isLoadUserInfo() {
        return loadInfo;
    }

    public boolean isFollow() {
        return isFollow;
    }

    /**
     * 获取用户详细信息
     * @param userID
     */
    @Override
    public void getUserInfo(String userID) {
        if(loadInfo) return;
        loadInfo=true;
        this.mUserID=userID;
        Map<String,String> params=new HashMap<>();
        params.put("visit_user_id", VideoApplication.getLoginUserID());
        params.put("user_id",userID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "user_info", MineUserInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MineUserInfo>() {
            @Override
            public void call(MineUserInfo data) {
                loadInfo=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getInfo()){
                    if(null!=mView) mView.showUserInfo(data,mUserID);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取用户已发布的视频
     * @param userID
     * @param fansID
     * @param page
     * @param pageSize
     */
    @Override
    public void getUpLoadVideoList(String userID,String fansID, String page, String pageSize) {

        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("page",page);
        params.put("page_size",pageSize);
        params.put("visit_user_id",fansID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_VIDEO_HOST + "list_byUserId", FollowVideoList.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FollowVideoList>() {
            @Override
            public void call(FollowVideoList data) {
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&data.getData().getLists().size()>0){
                    if(null!=mView) mView.showUpLoadVideoList(data);
                }else if(null!=data&&1==data.getCode()&&null!=data&&null!=data.getData()&&data.getData().getLists().size()<=0){
                    if(null!=mView) mView.showUpLoadVideoListEmpty("没有更多数据");
                }else{
                    if(null!=mView) mView.showUpLoadVideoListError("加载错误");
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
                    if(null!=mView)  mView.showReportUserResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }



    /**
     * 关注用户
     * @param userID
     * @param fansUserID
     */
    @Override
    public void onFollowUser(String userID, String fansUserID) {
        if(isFollow) return;
        isFollow=true;
        Map<String,String> params=new HashMap<>();

        params.put("user_id",userID);
        params.put("fans_user_id",fansUserID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "follow", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isFollow=false;
                Boolean isFollow=null;
                if(!TextUtils.isEmpty(data)){
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        if(1==jsonObject.getInt("code")){
                            //关注成功
                            if(TextUtils.equals(Constant.FOLLOW_SUCCESS,jsonObject.getString("msg"))){
                                isFollow=true;
                            }else if(TextUtils.equals(Constant.FOLLOW_UNSUCCESS,jsonObject.getString("msg"))){
                                isFollow=false;
                            }
                            if(null!=mView) mView.showFollowUser(isFollow,jsonObject.getString("msg"));
                        }else{
                            ToastUtils.showCenterToast("关注失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if(null!=mView) mView.showErrorView();
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
