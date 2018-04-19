
package com.video.newqu.ui.presenter;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.video.newqu.base.RxPresenter;
import com.video.newqu.bean.PhotoInfo;
import com.video.newqu.contants.NetContants;
import com.video.newqu.ui.contract.ServiceMessageContract;
import com.video.newqu.util.ImageUtils;
import com.video.newqu.util.Logger;
import com.video.newqu.util.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Request;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 账号登录
 */

public class ServiceMessagePresenter extends RxPresenter<ServiceMessageContract.View> implements ServiceMessageContract.Presenter<ServiceMessageContract.View> {

    public ServiceMessagePresenter(){

    }


    @Override
    public void sendMessage(String userID, String content, String contact, List<PhotoInfo> infoList) {

        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("content",content);
        params.put("contact",contact);
        new CompressAsyncTask(params,infoList).execute();
    }


    /**
     * 异步裁剪图片，并上传
     */
    private class CompressAsyncTask extends AsyncTask<String,Void,List<File>> {

        private final Map<String, String> params;
        private final List<PhotoInfo> infoList;

        public CompressAsyncTask(Map<String, String> params,List<PhotoInfo> infoList) {
            this.params=params;
            this.infoList=infoList;
        }
        //异步裁剪
        @Override
        protected List<File> doInBackground(String... params) {
            List<File> files=new ArrayList<>();
            if(null!=infoList&&infoList.size()>0) {
                for (PhotoInfo photoInfo : infoList) {
                    files.add(new File(photoInfo.getImagePath()));
                }
            }
            List<File> files1=null;
            if(null!=files&&files.size()>0){
                if(null!=mView) mView.showCutImageIng("图片处理中...");
                files1 = ImageUtils.changeFileSize(files);
            }
            return files1;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);

            PostFormBuilder builder = OkHttpUtils.post().params(params).url(NetContants.BASE_HOST+"feedback");

            if (null!=files&&files.size()>0) {
                for (int i = 0; i < files.size(); i++) {
                    String fileName = String.valueOf(TimeUtils.getCurrentTimeInLong()) +(int)(Math.random()*(9999-1000+1))+1000 + ".jpg";
                    builder.addFile("file" + (i + 1), fileName, files.get(i));
                }
            }

            if(null!=mView) mView.showStartUpdata("反馈信息提交中...");

            builder.build().execute(new StringCallback() {

                @Override
                public void onBefore(Request request, int id) {

                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    if(null!=mView) mView.showSendMessageError(e.getMessage());
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    if(null!=mView) mView.onUpdataProgress(progress);
                }

                @Override
                public void onResponse(String response, int id) {
                    if(!TextUtils.isEmpty(response)){
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.length()>0){
                                if(1==jsonObject.getInt("code")){
                                    if(null!=mView) mView.showSendMessageResult(jsonObject.getString("msg"));
                                }else{
                                    if(null!=mView) mView.showSendMessageError(jsonObject.getString("msg"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(null!=mView) mView.showSendMessageError(e.getMessage());
                        }
                    }
                }
            });
        }
    }
}
