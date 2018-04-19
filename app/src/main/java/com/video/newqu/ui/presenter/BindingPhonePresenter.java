
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.BindingPhoneContract;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 手机号码验证
 */

public class BindingPhonePresenter extends RxPresenter<BindingPhoneContract.View> implements BindingPhoneContract.Presenter<BindingPhoneContract.View> {

    private final Context context;

    public boolean isSanLogin() {
        return isSanLogin;
    }

    private boolean isSanLogin;

    public BindingPhonePresenter(Context context){
        this.context=context;
    }


    @Override
    public void bindingPhone(String phoneNumber, String code) {
        if(isSanLogin) return;
        isSanLogin=true;
        Map<String,String> params=new HashMap<>();
        params.put("user_id",VideoApplication.getLoginUserID());
        params.put("phone",phoneNumber);
        params.put("zone","86");
        params.put("code",code);
        // TODO: 2017/6/22 可将微博背景图片录入
        Subscription subscribe = HttpCoreEngin.get(context).rxpost( NetContants.BASE_HOST+"bing_sim", String.class, params, true, true, true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isSanLogin=false;
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showBindingPhoneResult(data);
                }else{
                    if(null!=mView) mView.showBindingPhoneError("绑定失败!");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
