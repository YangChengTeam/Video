
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.kk.securityhttp.net.entry.UpFileInfo;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.UserEditContract;
import com.video.newqu.util.ImageUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/6/1 15:00
 * 用户编辑资料上传
 */
public class UserEditPresenter extends RxPresenter<UserEditContract.View> implements UserEditContract.Presenter<UserEditContract.View> {

    private final Context context;
    public UserEditPresenter(Context context){
        this.context=context;
    }

    /**
     * 提交用户基本信息
     * @param userID
     * @param nikeName
     * @param sex
     * @param desp
     */
    @Override
    public void onPostUserData(String userID, String nikeName, String sex, String desp) {
        Map<String,String> params=new HashMap<>();
        params.put("id",userID);
        params.put("nickname",nikeName);
        params.put("gender",sex);
        params.put("signature",desp);
        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "user_edit", String.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String data) {
                if(!TextUtils.isEmpty(data)){
                    if(null!=mView) mView.showPostUserDataResult(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 上传用户头像
     * @param userID
     * @param filePath
     */
    @Override
    public void onPostImagePhoto(String userID,String filePath) {
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        new CompressAsyncTask(params).execute(filePath);
    }

    /**
     * 异步裁剪图片，并上传
     */
    private class CompressAsyncTask extends AsyncTask<String,Void,String>{
        private final Map<String, String> params;

        public CompressAsyncTask(Map<String, String> params) {
            this.params=params;
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
            Subscription subscribe = HttpCoreEngin.get(context).rxuploadFile(NetContants.BASE_HOST + "user_logo",String.class, upFileInfo, params, true).subscribe(new Action1<String>() {
                @Override
                public void call(String data) {
                    if(null!=mView) mView.showPostImagePhotoResult(data);
                }
            });
            addSubscrebe(subscribe);

//            UploadUtil uploadUtil = UploadUtil.getInstance();
//            String key = Md5.md5(Utils.getFileName(filePath));
//            uploadUtil.uploadFile(filePath,key,NetContants.BASE_HOST + "user_logo",params);
//            uploadUtil.setOnUploadProcessListener(new UploadUtil.OnUploadProcessListener() {
//                @Override
//                public void onUploadDone(int responseCode, String response) {
//                    if(responseCode==UploadUtil.UPLOAD_SUCCESS_CODE){
//                        Log.d(TAG, "onUploadDone: 上传成功");
//                    }
//                }
//
//                @Override
//                public void onUploadProcess(int uploadSize) {
//
//                }
//
//                @Override
//                public void initUpload(int fileSize) {
//
//                }
//            });
        }
    }
}
