
package com.video.newqu.ui.presenter;

import android.os.Build;
import android.text.TextUtils;

import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.DeviceInfo;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.activity.MainActivity;
import com.video.newqu.ui.contract.MainContract;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/6/1 15:00
 * 版本更新
 */
public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter<MainContract.View> {

    private final MainActivity context;
    public MainPresenter(MainActivity context){
        this.context=  context;
    }

    private boolean isResqust;

    public boolean isResqust() {
        return isResqust;
    }

    /**
     * 注册用户信息
     */
    @Override
    public void register() {

    }

    /**
     * 统计安装信息
     * @param flag 用户是否拒绝了授权
     */
    public void registerApp(boolean flag) {
        if(isResqust) return;
        isResqust=true;
        try {
            String[] locationID = SystemUtils.getLocation();
            if(null!=locationID&&locationID.length>0){
                DeviceInfo deviceInfo=new DeviceInfo();
                deviceInfo.setLocation_longitude(locationID[0]);//经度
                deviceInfo.setLocation_latitude(locationID[1]);//纬度
                deviceInfo.setApp_ini(Utils.getVersionCode()+"");
                deviceInfo.setBrand(Build.BRAND);//手机品牌
                deviceInfo.setImeil(VideoApplication.mUuid);/// /设备号
                deviceInfo.setModel(Build.MODEL);//手机型号
                deviceInfo.setSdk_ini(Build.VERSION.RELEASE);
                Map<String,String> params=new HashMap<>();
                params.put("imeil", VideoApplication.mUuid);
                params.put("brand", deviceInfo.getBrand());
                params.put("location",TextUtils.isEmpty(deviceInfo.getLocation_longitude())?"0,0": deviceInfo.getLocation_longitude()+","+deviceInfo.getLocation_latitude());
                params.put("app_ini", deviceInfo.getApp_ini());
                params.put("model", deviceInfo.getModel());
                params.put("sdk_ini", deviceInfo.getModel());
                HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "open_app",String.class,params,false,false,false).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String data) {
                        isResqust=false;
                        if(!TextUtils.isEmpty(data)){
                            try {
                                JSONObject jsonObject=new JSONObject(data);
                                if(jsonObject.length()>0){
                                    if(1==jsonObject.getInt("code")){
                                        SharedPreferencesUtil.getInstance().putBoolean(Constant.REGISTER_OPEN_APP,true);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
