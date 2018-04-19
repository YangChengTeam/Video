
package com.video.newqu.ui.presenter;

import android.content.Context;
import android.os.AsyncTask;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.kk.securityhttp.net.entry.UpFileInfo;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.MineUserInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.UserMineContract;
import com.video.newqu.util.ImageUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 获取用户基本信息
 */
public class UserInfoPresenter extends RxPresenter<UserMineContract.View> implements UserMineContract.Presenter<UserMineContract.View> {

    private final Context context;
    private boolean isLoading;

    public UserInfoPresenter(Context context){
        this.context=context;
    }



    public boolean isLoading() {
        return isLoading;
    }


    @Override
    public void getUserInfo(String userID) {
        if(isLoading) return;
        isLoading=true;
        Map<String,String> params=new HashMap<>();
        params.put("visit_user_id",userID);
        params.put("user_id",userID);

        Subscription subscribe = HttpCoreEngin.get(context).rxpost(NetContants.BASE_HOST + "user_info", MineUserInfo.class, params,true,true,true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MineUserInfo>() {
            @Override
            public void call(MineUserInfo data) {
                isLoading=false;
                if(null!=data&&1==data.getCode()&&null!=data.getData()&&null!=data.getData().getInfo()){
                    if(null!=mView) mView.showUserInfo(data);
                }else{
                    if(null!=mView) mView.showErrorView();
                }
            }
        });
        addSubscrebe(subscribe);
    }


    /**
     * 上传用户背景封面
     * @param userID
     * @param filePath
     */
    @Override
    public void onPostImageBG(String userID, String filePath) {
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        new CompressAsyncTask(params).execute(filePath);
    }



    /**
     * 异步裁剪图片，并上传
     */
    private class CompressAsyncTask extends AsyncTask<String,Void,String> {
        private final Map<String, String> params;


        public CompressAsyncTask(Map<String, String> params) {
            this.params=params;
        }

        //异步裁剪
        @Override
        protected String doInBackground(String... params) {
            return ImageUtils.changeFileSizeByLocalPath1080(params[0]);
        }

        @Override
        protected void onPostExecute(String filePath) {
            super.onPostExecute(filePath);
            //上传文件
            UpFileInfo upFileInfo = new UpFileInfo();
            upFileInfo.file = new File(filePath);
            upFileInfo.filename = upFileInfo.file.getName();
            upFileInfo.name = upFileInfo.file.getName();
            Subscription subscribe = HttpCoreEngin.get(context).rxuploadFile(NetContants.BASE_HOST + "user_image_bg",String.class, upFileInfo, params, true).subscribe(new Action1<String>() {
                @Override
                public void call(String data) {
                    if(null!=mView) mView.showPostImageBGResult(data);
                }
            });
            addSubscrebe(subscribe);

        }
    }
}
