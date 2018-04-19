package com.video.newqu.manager;

import android.content.Context;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.UpdataApkInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.listener.OnUpdataStateListener;
import com.video.newqu.util.Utils;
import java.util.HashMap;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/8/14
 * 版本更新
 */

public class APKUpdataManager  {

    private final String mUrl= NetContants.BASE_HOST + "version_compare";
    private final Context mContext;
    private OnUpdataStateListener mOnUpdataStateListener;

    /**
     * 构造传值
     */
    public APKUpdataManager(Context context) {
        this.mContext=context;
    }

    /**
     * 检测版本更新
     * @param onUpdataStateListener
     */
    public void checkedBuild(OnUpdataStateListener onUpdataStateListener){

        mOnUpdataStateListener = onUpdataStateListener;
        Map<String,String> params=new HashMap<>();
        params.put("user_id", VideoApplication.getLoginUserID());
        params.put("version_code",Utils.getVersionCode()+"");

        HttpCoreEngin.get(mContext).rxpost(mUrl, UpdataApkInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<UpdataApkInfo>() {
            @Override
            public void call(UpdataApkInfo data) {
                //需要更新
                if(null!=data&&1==data.getCode()&&null!=data.getData().getVersion_code()&&Integer.parseInt(data.getData().getVersion_code())> Utils.getVersionCode()){
                    if(null!=mOnUpdataStateListener){
                        mOnUpdataStateListener.onNeedUpdata(data);
                    }
                }else if(null!=data&&0==data.getCode()&& TextUtils.equals("don't need update",data.getMsg())){
                    if(null!=mOnUpdataStateListener){
                        mOnUpdataStateListener.onNotUpdata("已是最新版本");
                    }
                }else{
                    if(null!=mOnUpdataStateListener){
                        mOnUpdataStateListener.onUpdataError("检测更新失败!请重试");
                    }
                }
            }
        });
    }
}
