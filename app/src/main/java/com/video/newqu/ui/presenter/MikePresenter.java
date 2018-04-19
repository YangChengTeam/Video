
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.VideoListInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.MakePasswordContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 用户收藏列表
 */
public class MikePresenter extends RxPresenter<MakePasswordContract.View> implements MakePasswordContract.Presenter<MakePasswordContract.View> {

    private final Context context;
    private boolean isEdit;
    public MikePresenter(Context context){
        this.context=context;
    }


    public boolean isEdit() {
        return isEdit;
    }

    /**
     * 修改用户密码
     * @param imeil
     * @param account
     * @param passsword
     * @param code
     * @param countryCode
     */
    @Override
    public void makePassword(String imeil, String account, String passsword, String code, String countryCode) {
        if(isEdit) return;
        isEdit=true;
        final Map<String,String> params=new HashMap<>();
        params.put("imeil",imeil);
        params.put("phone",account);
        params.put("password",passsword);
        params.put("code",code);
        params.put("zone",countryCode);


        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "forgot_pwd", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isEdit=false;
                if(!TextUtils.isEmpty(data)){
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        if(jsonObject.length()>0){
                            //修改密码成功
                            if(1==jsonObject.getInt("code")){
                                if(null!=mView) mView.makePasswordFinlish(jsonObject.getString("msg")+",请使用新密码登录");
                            //修改密码失败
                            }else{
                                if(null!=mView) mView.makePasswordError(jsonObject.getString("msg"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
