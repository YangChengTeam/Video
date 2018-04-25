package com.video.newqu.upload.manager;

import android.text.TextUtils;
import android.util.Log;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.AbortMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.AbortMultipartUploadResult;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.kk.securityhttp.net.utils.OKHttpUtil;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.bean.WeiChactVideoInfo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.video.newqu.upload.PauseableUploadRequest;
import com.video.newqu.upload.PauseableUploadResult;
import com.video.newqu.upload.PauseableUploadTask;
import com.video.newqu.upload.listener.UploadStsStateListener;
import com.video.newqu.util.Logger;
import com.video.newqu.util.StringUtils;
import com.video.newqu.util.SystemUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OS批量文件上传文件分片上传任务管理
 */

public class BatchFileUploadManager {

    public static final String TAG = BatchFileUploadManager.class.getSimpleName();
    private static BatchFileUploadManager mInstance;
    private OSS oss;
    private Map<Long,PauseableUploadTask> ossTaskMap;//管理上传任务的任务栈
    private UploadStsStateListener mUploadStsStateListener;//监听器
    public static final int PART_SIZE = 128 * 1024; // 设置分片大小
    private PauseableUploadTask mPauseableUploadTask;


    public BatchFileUploadManager(Builder builder) {
        //构建上传Client
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(20 * 1000); // 连接超时
        conf.setSocketTimeout(20 * 1000); // socket超时
        conf.setMaxConcurrentRequest(3); // 最大并发上传任务数量，3个
        conf.setMaxErrorRetry(5); // 失败后最大重试次数
        oss = new OSSClient(VideoApplication.getInstance(), ApplicationManager.getInstance().getUploadConfig().getEndpoint(), credetialProvider, conf);
    }

    /**
     * 实时获取上传Token
     */
    OSSCredentialProvider credetialProvider = new OSSFederationCredentialProvider() {
        @Override
        public OSSFederationToken getFederationToken() {
            String stsJson;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(ApplicationManager.getInstance().getUploadConfig().getStsServer()).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    if(true){
                        stsJson=OKHttpUtil.decodeBody(response.body().byteStream());
                    }else{
                        stsJson = response.body().string();
                    }
                } else {
                    throw new IOException(TAG+"getFederationToken()" + response);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                return null;
            }
            try {
                JSONObject jsonObjs = new JSONObject(stsJson);
                String ak = jsonObjs.getString("AccessKeyId");
                String sk = jsonObjs.getString("AccessKeySecret");
                String token = jsonObjs.getString("SecurityToken");
                String expiration = jsonObjs.getString("Expiration");
                return new OSSFederationToken(ak, sk, token, expiration);
            }
            catch (JSONException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                return null;
            }
        }
    };


    /**
     * 开始上传
     * @param uploadVideoList
     */
    public void upload(List<WeiChactVideoInfo> uploadVideoList){

        if(null==oss||null==uploadVideoList||uploadVideoList.size()<=0){
            new IllegalThreadStateException("OSS is not initialize or upload task is null!");
            return;
        }

        //如果有任务在上传
        puseAllUploadTask();

        if(null==ossTaskMap){
            ossTaskMap=new HashMap<>();
        }

        if(null!=uploadVideoList&&uploadVideoList.size()>0){
            for (int i = 0; i < uploadVideoList.size(); i++) {
                WeiChactVideoInfo weiChactVideoInfo = uploadVideoList.get(i);
                if(null!=weiChactVideoInfo&&new File(weiChactVideoInfo.getFilePath()).exists()){
                    new UploadFileAsyncTask(weiChactVideoInfo).execute();
                }else{
                    ApplicationManager.getInstance().getWeiXinVideoUploadDB().deleteUploadVideoInfo(weiChactVideoInfo);
                }
            }
        }
    }

    public void pause() {
        if(null!=mPauseableUploadTask&&!mPauseableUploadTask.isPause()){
            mPauseableUploadTask.pause();
        }
    }


    /**
     * 上传任务栈
     */

    private class UploadFileAsyncTask {

        private final WeiChactVideoInfo videoInfo;

        public UploadFileAsyncTask(WeiChactVideoInfo uploadVideoInfo) {
            this.videoInfo=uploadVideoInfo;
        }

        public void execute(){

            String durtionMD5 = StringUtils.stringToMd5(videoInfo.getVideo_durtion());
            Map<String, String> serviceParams = new HashMap<>();
            serviceParams.put("object", "Video/" + durtionMD5+".mp4");

            HttpCoreEngin.get(VideoApplication.getInstance()).rxpost(NetContants.BASE_HOST + "object_is_exist", String.class, serviceParams, true, true, true).observeOn(Schedulers.io()).subscribe(new Action1<String>() {
                @Override
                    public void call(String data) {
                    if(!TextUtils.isEmpty(data)){
                        try {
                            JSONObject jsonObject=new JSONObject(data);

                            if(null!=jsonObject&&jsonObject.length()>0){

                                if (0 == jsonObject.getInt("code")) {
                                    String durtionMD5 = StringUtils.stringToMd5(videoInfo.getVideo_durtion());
                                    //上传构造请求
                                    PauseableUploadRequest request = new PauseableUploadRequest(Constant.STS_BUCKET, "Video/"+durtionMD5+".mp4", videoInfo.getFilePath(), PART_SIZE);
                                    //上传进度监听
                                    request.setProgressCallback(new OSSProgressCallback<PauseableUploadRequest>() {
                                        @Override
                                        public void onProgress(PauseableUploadRequest request, long currentSize, long totalSize) {
                                            int progress = (int) (100 * currentSize / totalSize);
                                            Logger.d(TAG,"progress="+progress);
                                        }
                                    });

                                    mPauseableUploadTask = new PauseableUploadTask(oss, request, new OSSCompletedCallback<PauseableUploadRequest, PauseableUploadResult>() {
                                        @Override
                                        public void onSuccess(PauseableUploadRequest request, PauseableUploadResult result) {
                                            if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(videoInfo.getID());
//                                            if(null!=result){
//                                                Logger.d(TAG,"getResponseHeader="+result.getResponseHeader().toString());
//                                                Logger.d(TAG,"getServerCallbackReturnBody="+result.getServerCallbackReturnBody());
//                                                Logger.d(TAG,"getRequestId="+result.getRequestId());
//                                                Logger.d(TAG,"getStatusCode="+result.getStatusCode());
//                                                Logger.d(TAG,"getObjectKey="+result.getObjectKey());
//                                            }
                                            ApplicationManager.getInstance().getWeiXinVideoUploadDB().deleteUploadVideoInfo(videoInfo);
                                        }

                                        @Override
                                        public void onFailure(PauseableUploadRequest request, ClientException clientException, ServiceException serviceException) {
                                            if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(videoInfo.getID());//移除上传任务栈中的元
                                            File file = new File(videoInfo.getFilePath());
                                            if(null!=file&&file.exists()&&file.isFile()){
                                                if(null!=serviceException){
                                                    if(404==serviceException.getStatusCode()){
                                                        ApplicationManager.getInstance().getWeiXinVideoUploadDB().deleteUploadVideoInfo(videoInfo);
                                                    }
                                                }
                                            }else{
                                                ApplicationManager.getInstance().getWeiXinVideoUploadDB().deleteUploadVideoInfo(videoInfo);
                                                return;
                                            }
                                        }
                                    });
                                    setUserPrams(mPauseableUploadTask,videoInfo);
                                    try {
                                        //新的上传任务
                                        String uploadID=null;
                                        if(TextUtils.isEmpty(videoInfo.getUploadID())){
                                            uploadID = mPauseableUploadTask.initUpload();
                                            videoInfo.setUploadID(uploadID);
                                            ApplicationManager.getInstance().getWeiXinVideoUploadDB().updateUploadVideoInfo(videoInfo);//刷新上传ID
                                        }else{
                                            uploadID=videoInfo.getUploadID();

                                        }
                                        mPauseableUploadTask.upload(uploadID);
                                        ossTaskMap.put(videoInfo.getID(), mPauseableUploadTask);

                                    }
                                    catch (ServiceException e) {
                                        if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(videoInfo.getID());//移除上传任务栈中的元素
                                        if(404==e.getStatusCode()){
                                            ApplicationManager.getInstance().getWeiXinVideoUploadDB().deleteUploadVideoInfo(videoInfo);
                                        }
                                        e.printStackTrace();
                                    }
                                    catch (ClientException e) {
                                        if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(videoInfo.getID());//移除上传任务栈中的元素
                                        e.printStackTrace();
                                    }
                                    catch (IOException e) {
                                        if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(videoInfo.getID());//移除上传任务栈中的元素
                                        ApplicationManager.getInstance().getWeiXinVideoUploadDB().deleteUploadVideoInfo(videoInfo);
                                        e.printStackTrace();
                                    }
                                    //服务端已存在此文件
                                }else{
                                    ApplicationManager.getInstance().getWeiXinVideoUploadDB().deleteUploadVideoInfo(videoInfo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            });
        }
    }


    /**
     * 暂停所有任务
     */
    public void puseAllUploadTask() {
        if(null!=ossTaskMap&&ossTaskMap.size()>0){
            Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = ossTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                next.getValue().pause();
            }
        }
        if(null!=ossTaskMap) ossTaskMap.clear();
    }


    /**
     * 是否有上传任务在进行中
     * @return
     */
    public boolean isUpload(){
        boolean isupload=false;
        if(null!=ossTaskMap&&ossTaskMap.size()>0){
            isupload=true;
        }
        return isupload;
    }


    /**
     * 取消单个上传任务，并删除所有分片信息
     */
    public AbortMultipartUploadResult canelSingleTask(UploadVideoInfo videoInfo) throws ClientException, ServiceException {
        if(TextUtils.isEmpty(videoInfo.getUploadID())) return null;
        if(null!=ossTaskMap&&ossTaskMap.size()>0){
            Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = ossTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                if(videoInfo.getId()==next.getKey()){
                    //先暂停
                    next.getValue().pause();
                    //再删除OSS上传的临时分块文件
                    String objectKey=videoInfo.getVideoDurtion()+"";
                    AbortMultipartUploadRequest abort = new AbortMultipartUploadRequest(Constant.STS_BUCKET, "Video/"+objectKey+".mp4", videoInfo.getUploadID());
                    return oss.abortMultipartUpload(abort);// 若无异常抛出说明删除成功
                }
            }
        }
        return null;
    }

    /**
     * 设置自定义参数
     * @param pauseableUploadTask
     * @param videoInfo
     */
    private void setUserPrams(PauseableUploadTask pauseableUploadTask, WeiChactVideoInfo videoInfo) {

        String[] locationID = VideoApplication.getInstance().getLocations();
        Map<String,String> mParams = new HashMap<>();

        mParams.put("callbackUrl",ApplicationManager.getInstance().getUploadConfig().getCallbackAddress());
        mParams.put("callbackHost",ApplicationManager.getInstance().getUploadConfig().getCallBackHost());
        mParams.put("callbackBodyType",ApplicationManager.getInstance().getUploadConfig().getCallBackType());

        mParams.put("callbackBody","{\"bucket\":${bucket},\"object\":${object},\"mimeType\":${mimeType},\"size\":${size},\"filename\":${object},\"imeil\":${x:imeil},\"user_id\":${x:user_id},\"desp\":${x:desp},\"cur_frame\":${x:cur_frame},\"is_private\":${x:is_private}," +
                "\"video_id\":${x:video_id},\"video_width\":${x:video_width},\"video_height\":${x:video_height},\"frame_num\":${x:frame_num},\"code_rate\":${x:code_rate},\"video_durtion\":${x:video_durtion},\"device_net_ip\":${x:device_net_ip},\"device_longitude\":${x:device_longitude},\"device_latitude\":${x:device_latitude},\"upload_type\":${x:upload_type},\"music_id\":${x:music_id},\"download_permiss\":${x:download_permiss}}");

        Map<String,String>mParamVars = new HashMap<>();
        mParamVars.put("x:imeil", VideoApplication.mUuid);
        mParamVars.put("x:user_id",VideoApplication.getLoginUserID());
        mParamVars.put("x:desp","");//视频详情介绍
        mParamVars.put("x:cur_frame","1");//帧数
        mParamVars.put("x:is_private","0");//是否隐私
        mParamVars.put("x:video_id",videoInfo.getID()+"");//视频ID
        mParamVars.put("x:video_width",videoInfo.getVideo_width());//视频宽
        mParamVars.put("x:video_height",videoInfo.getVideo_height());//视频高
        mParamVars.put("x:frame_num", TextUtils.isEmpty(videoInfo.getFrame_num())?"20":videoInfo.getFrame_num());//视频帧数量
        mParamVars.put("x:code_rate", TextUtils.isEmpty(videoInfo.getCode_rate())?"2000":videoInfo.getCode_rate());//码率
        mParamVars.put("x:video_durtion",videoInfo.getVideo_durtion());//时长
        mParamVars.put("x:upload_type",videoInfo.getSourceType()+"");//上传渠道类型
        String locastHostIP = SystemUtils.getLocastHostIP();
        mParamVars.put("x:device_net_ip",TextUtils.isEmpty(locastHostIP)?"0":locastHostIP );//IP
        mParamVars.put("x:device_longitude",null!=locationID&&locationID.length>0?locationID[0]:"0");//经度
        mParamVars.put("x:device_latitude",null!=locationID&&locationID.length>1?locationID[1]:"0");//纬度
        mParamVars.put("x:music_id","0");
        mParamVars.put("x:download_permiss","0");
        pauseableUploadTask.setUserPrams(mParams,mParamVars);
    }

    /**
     * 注册监听器
     * @param uploadStsStateListener
     */
    public void registerUploadListener(UploadStsStateListener uploadStsStateListener){
        this.mUploadStsStateListener=uploadStsStateListener;
    }

    /**
     * 注销监听器
     */
    public void unRegisterUploadListener(){
        if(null!=mUploadStsStateListener)  mUploadStsStateListener=null;
    }


    public void onDestory(){
        if(null!=ossTaskMap){
            ossTaskMap.clear();
        }
    }
    /**
     * 初始化构造参数
     */
    public static class Builder {

//        private UploadParamsConfig uploadParamsConfig;
//        public Builder setParamsConfig(UploadParamsConfig uploadParamsConfig) {
//            this.uploadParamsConfig=uploadParamsConfig;
//            return this;
//        }

        public BatchFileUploadManager build(){
            if(null==mInstance){
                synchronized (BatchFileUploadManager.class){
                    mInstance=new BatchFileUploadManager(Builder.this);
                }
            }
            return mInstance;
        }
    }


}
