
package com.video.newqu.ui.presenter;

import android.content.Context;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.UserData;
import com.video.newqu.bean.UserDataInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.LoginContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 账号登录
 */

public class LoginPresenter extends RxPresenter<LoginContract.View> implements LoginContract.Presenter<LoginContract.View> {

    private final Context context;

    private boolean isLogin;

    public boolean isLogin() {
        return isLogin;
    }

    public LoginPresenter(Context context){
        this.context=context;
    }

    /**
     * 第三方账号登录 QQ WeiChat,WeiBo
     * @param userDataInfo
     */
    @Override
    public void qqAndWeichatLogin(UserDataInfo userDataInfo) {
        if(isLogin) return;
        isLogin=true;
        Map<String,String> params=new HashMap<>();
        params.put("imeil",userDataInfo.getIemil());
        params.put("nickname",userDataInfo.getNickname());
        params.put("gender",userDataInfo.getGender());
        params.put("province",userDataInfo.getProvince());
        params.put("city",userDataInfo.getCity());
        params.put("logo",userDataInfo.getFigureurl_qq_2());
        params.put("open_id",userDataInfo.getOpenid());
        params.put("login_type",userDataInfo.getLoginType());
        // TODO: 2017/6/22 可将微博背景图片录入
        Subscription subscribe = HttpCoreEngin.get(context).rxpost( NetContants.BASE_HOST+"qqwx_login", UserData.class, params, true, true, true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<UserData>() {
            @Override
            public void call(UserData data) {
                isLogin=false;
                if(null!=data&&null!=data.getData()&&null!=data.getData().getInfo()){
                    if(null!=mView) mView.showQQWeichatUserData(data);
                }else{
                    if(null!=mView) mView.showLoginError();
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
