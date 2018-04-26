package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.LoginXinQuContract;
import org.json.JSONException;
import org.json.JSONObject;
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

public class LoginXinQuPresenter extends RxPresenter<LoginXinQuContract.View> implements LoginXinQuContract.Presenter<LoginXinQuContract.View> {

    private final Context context;
    private boolean isLogin;

    public boolean isLogin() {
        return isLogin;
    }

    public LoginXinQuPresenter(Context context){
        this.context=context;
    }


    /**
     * 使用账号密码登录
     * @param countryCode
     * @param account
     * @param password
     */

    @Override
    public void userLogin(String countryCode, String account, String password) {
        if(isLogin) return;
        isLogin=true;
        Map<String,String> params=new HashMap<>();
        params.put("imeil", VideoApplication.mUuid);
        params.put("phone",account);
        params.put("password",password);
        params.put("zone",countryCode);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "phone_login", String.class, params, true, true, true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isLogin=false;
                if(!TextUtils.isEmpty(data)){
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        if(null!=jsonObject&&jsonObject.length()>0){
                            if(null!=jsonObject&&1==jsonObject.getInt("code")){
                                MineUserInfo mineUserInfo = new Gson().fromJson(data, MineUserInfo.class);
                                if(null!=mineUserInfo&&null!=mineUserInfo.getData()&&null!=mineUserInfo.getData().getInfo()){

                                    if(null!=mView) mView.showLoginFinlish(mineUserInfo);
                                }else{
                                    if(null!=mView) mView.showLoginError(mineUserInfo.getMsg());
                                }
                            }else{
                                if(null!=mView) mView.showLoginError(jsonObject.getString("msg"));
                            }
                        }else{
                            if(null!=mView) mView.showLoginError("登录失败");
                        }
                    } catch (JSONException e) {
                        if(null!=mView) mView.showLoginError(e.getMessage());
                        e.printStackTrace();
                    }
                }else{
                    if(null!=mView) mView.showLoginError("登录失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
