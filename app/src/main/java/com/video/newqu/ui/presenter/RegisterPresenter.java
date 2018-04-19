
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.kk.securityhttp.net.entry.UpFileInfo;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.RegisterContract;
import com.video.newqu.util.ImageUtils;
import com.video.newqu.util.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
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
public class RegisterPresenter extends RxPresenter<RegisterContract.View> implements RegisterContract.Presenter<RegisterContract.View> {

    private final Context context;

    private boolean isRegister=false;

    public boolean isRegister() {
        return isRegister;
    }

    public RegisterPresenter(Context context){
        this.context=context;
    }


    /**
     * 用户注册，用户注册消息提交至后台，后台携带消息至MOB验证，验证成功后再进行注册逻辑
     * @param imeil 用户设备号
     * @param account 账号
     * @param passsword 密码
     * @param code 验证码
     * @param countryCode 国家区号
     * @param nickName 昵称
     * @param sex 性别
     */
    @Override
    public void register(String imeil, final String account, String passsword, String code, String countryCode, String nickName, String sex, final File filePath) {

        final Map<String,String> params=new HashMap<>();
        params.put("imeil",imeil);
        params.put("phone",account);
        params.put("nickname",nickName);
        params.put("password",passsword);
        params.put("code",code);
        params.put("zone",countryCode);
        params.put("gender",sex);
        params.put("login_type","4");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "phone_reg", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                if(!TextUtils.isEmpty(data)){
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        if(jsonObject.length()>0){
                            if(0==jsonObject.getInt("code")){
                                if(null!=mView) mView.registerResultError(jsonObject.getString("msg"));
                            }else if(1==jsonObject.getInt("code")){
                                //注册成功后检测用户是否有携带图片
                                String phoneNumber = new JSONObject(jsonObject.getString("data")).getString("phone");
                                String userID = new JSONObject(jsonObject.getString("data")).getString("user_id");
                                if(!TextUtils.isEmpty(phoneNumber)&&TextUtils.equals(account,phoneNumber)){
                                    //需要携带图片
                                    if(null!=filePath&&filePath.exists()&&filePath.isFile()){
                                        if(null!=mView) mView.needIploadImageLogo();
                                        new CompressAsyncTask(userID).execute(filePath.getAbsolutePath());
                                     //不需要携带图片,注册完成
                                    }else{
                                        if(null!=mView) mView.registerResultFinlish(data);
                                        return;
                                    }
                                }
                            }
                        }else{
                            if(null!=mView) mView.registerError();
                        }
                    } catch (JSONException e) {
                        if(null!=mView) mView.registerError();
                        e.printStackTrace();
                    }
                }else{
                    if(null!=mView)  mView.registerError();
                }
            }
        });
        addSubscrebe(subscribe);
    }


    /**
     * 用户注册，用户注册消息提交至后台，后台携带消息至MOB验证，验证成功后再进行注册逻辑
     * @param imeil
     * @param account
     * @param passsword
     * @param code
     */
    @Override
    public void register(String imeil, final String account, String passsword, String code) {
        if(isRegister) return;
        isRegister=true;
        final Map<String,String> params=new HashMap<>();
        params.put("imeil",imeil);
        params.put("phone",account);
        params.put("password",passsword);
        params.put("nickname","注册用户"+account.substring(account.length()-4,account.length()));
        params.put("code",code);
        params.put("zone","86");

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "phone_reg", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                isRegister=false;
                if(!TextUtils.isEmpty(data)){
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        if(jsonObject.length()>0){
                            if(0==jsonObject.getInt("code")){
                                if(null!=mView) mView.registerResultError(jsonObject.getString("msg"));
                            }else if(1==jsonObject.getInt("code")){
                                if(null!=mView) mView.registerResultFinlish(data);
                            }
                        }else{
                            if(null!=mView) mView.registerError();
                        }
                    } catch (JSONException e) {
                        if(null!=mView) mView.registerError();
                        e.printStackTrace();
                    }
                }else{
                    if(null!=mView) mView.registerError();
                }
            }
        });
        addSubscrebe(subscribe);
    }


    /**
     * 异步裁剪图片，并上传
     */
    private class CompressAsyncTask extends AsyncTask<String,Void,String>{

        private final String userID;

        public CompressAsyncTask( String userID) {
            this.userID=userID;
        }

        //异步裁剪
        @Override
        protected String doInBackground(String... params) {
            return ImageUtils.changeFileSizeByLocalPath(params[0]);
        }

        @Override
        protected void onPostExecute(String filePath) {
            super.onPostExecute(filePath);
            //上传文件
            UpFileInfo upFileInfo = new UpFileInfo();
            upFileInfo.file = new File(filePath);
            upFileInfo.filename = upFileInfo.file.getName();
            upFileInfo.name = upFileInfo.file.getName();
            Map<String,String> params=new HashMap<>();
            params.put("user_id",userID);
            Subscription subscribe = HttpCoreEngin.get(context).rxuploadFile(NetContants.BASE_HOST + "user_logo",String.class, upFileInfo, params, true).subscribe(new Action1<String>() {
                @Override
                public void call(String data) {
                    if(!TextUtils.isEmpty(data)){
                        try {
                            JSONObject jsonObject=new JSONObject(data);
                            if(jsonObject.length()>0){
                                //图片上传成功
                                if(1==jsonObject.getInt("code")){
                                    if(null!=mView) mView.imageUploadFinlish(jsonObject.getString("msg"));
                                    return;
                                }else{
                                    ToastUtils.showCenterToast(jsonObject.getString("msg"));
                                }
                            }else{
                                if(null!=mView) mView.imageUploadError();
                            }
                        } catch (JSONException e) {
                            if(null!=mView) mView.imageUploadError();
                            e.printStackTrace();
                        }
                    }
                }
            });
            addSubscrebe(subscribe);
        }
    }
}
