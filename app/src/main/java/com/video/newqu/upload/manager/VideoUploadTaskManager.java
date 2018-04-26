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
import com.blankj.utilcode.util.LogUtils;
import com.kk.securityhttp.net.utils.OKHttpUtil;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.UploadVideoInfo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.upload.PauseableUploadRequest;
import com.video.newqu.upload.PauseableUploadResult;
import com.video.newqu.upload.PauseableUploadTask;
import com.video.newqu.upload.bean.UploadDeteleTaskInfo;
import com.video.newqu.upload.bean.UploadParamsConfig;
import com.video.newqu.upload.listener.VideoUploadListener;
import com.video.newqu.util.Logger;
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

/**
 * TinyHung@Outlook.com
 * 2017/8/17.
 * OSS文件分片上传任务管理,支持批量添加上传任务，追加单个上传任务，暂停单个上传任务，取消单个上传任务，暂停所有上传任务
 */

public class VideoUploadTaskManager {

    public static final String TAG = "VideoUploadTaskManager";
    private static VideoUploadTaskManager mInstance;
    private UploadParamsConfig uploadParamsConfig;
    private OSS oss;
    private Map<Long,PauseableUploadTask> ossTaskMap;//管理上传任务的任务栈
    public VideoUploadListener mVideoUploadListener;//监听器
    public static final int PART_SIZE = 128 * 1024; // 设置分片大小

    /**
     * 构造函数
     */
    public VideoUploadTaskManager() {
        if(null==VideoUploadTaskManager.this.uploadParamsConfig){
            uploadParamsConfig =new UploadParamsConfig();
            uploadParamsConfig.setBucket(Constant.STS_BUCKET);
            uploadParamsConfig.setCallbackAddress(Constant.STS_CALLBACKADDRESS);
            uploadParamsConfig.setEndpoint(Constant.STS_ENDPOINT);
            uploadParamsConfig.setStsServer(Constant.STS_SERVER);
            uploadParamsConfig.setEncryptResponse(true);
        }
        //构建上传Client
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(20 * 1000); // 连接超时
        conf.setSocketTimeout(20 * 1000); // socket超时
        conf.setMaxConcurrentRequest(3); // 最大并发上传任务数量，3个
        conf.setMaxErrorRetry(5); // 失败后最大重试次数
        oss = new OSSClient(VideoApplication.getInstance(), uploadParamsConfig.getEndpoint(), credetialProvider, conf);
    }



    /**
     * 实时获取上传Token
     */
    OSSCredentialProvider credetialProvider = new OSSFederationCredentialProvider() {

        @Override
        public OSSFederationToken getFederationToken() {
            String stsJson;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(uploadParamsConfig.getStsServer()).build();
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
     * 构造实例--单例模式
     * @return
     */
    public static synchronized VideoUploadTaskManager getInstance() {
        if(null==mInstance){
            synchronized (VideoUploadTaskManager.class){
                mInstance=new VideoUploadTaskManager();
            }
        }
        return mInstance;
    }

    /**
     * 批量添加上传任务
     * @param data
     * @return
     */
    public VideoUploadTaskManager addUploadTaskAndExcute(final List<UploadVideoInfo> data) {
        if(null==ossTaskMap) ossTaskMap=new HashMap<>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(null!=data&&data.size()>0){
                    for (int i = 0; i < data.size(); i++) {
                        UploadVideoInfo uploadVideoInfo = data.get(i);
                        if(null!=uploadVideoInfo){
                            if(null!=ossTaskMap&&ossTaskMap.size()>0){
                                Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = ossTaskMap.entrySet().iterator();
                                boolean isExit=false;
                                while (iterator.hasNext()) {
                                    Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                                    if(uploadVideoInfo.getId()==next.getKey()){
                                        isExit=true;
                                        break;
                                    }
                                }
                                if(!isExit){
                                    new UploadVideoAsyncTask(uploadVideoInfo).execute();
                                }
                            }else{
                                new UploadVideoAsyncTask(uploadVideoInfo).execute();
                            }
                        }
                    }
                }
            }
        }.start();
        return mInstance;
    }



    /**
     * 暂停单个上传任务=
     * @param data
     * @return
     */
    public boolean pauseUploadTask(UploadVideoInfo data) {
        if(null==ossTaskMap) ossTaskMap=new HashMap<>();
        if(null!=ossTaskMap&&ossTaskMap.size()>0){
            Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = ossTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                if(data.getId()==next.getKey()){
                    PauseableUploadTask value = next.getValue();
                    if(null!=value){
                        value.pause();
                        if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(next.getKey());
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 追加单个上传任务,避免重复上传,并立即执行上传任务
     * @param data
     * @return
     */
    public VideoUploadTaskManager addUploadTaskAndExcute(final UploadVideoInfo data) {
        if(null==ossTaskMap) ossTaskMap=new HashMap<>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(null!=ossTaskMap&&ossTaskMap.size()>0){
                    Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = ossTaskMap.entrySet().iterator();
                    boolean isExit=false;
                    while (iterator.hasNext()) {
                        Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                        if(data.getId()==next.getKey()){
                            isExit=true;
                            break;
                        }
                    }
                    if(!isExit){

                        new UploadVideoAsyncTask(data).execute();
                    }
                }else{

                    new UploadVideoAsyncTask(data).execute();
                }
            }
        }.start();
        return mInstance;
    }


    /**
     * 暂停所有的上传任务
     */
    public void pauseUploadTask(){
        if(null!=ossTaskMap&&ossTaskMap.size()>0){
            Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = ossTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                next.getValue().pause();
                if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(next.getKey());
            }
        }
    }


    /**
     * 取消单个上传任务，并删除所有分片信息
     */
    public UploadDeteleTaskInfo canelSingleTask(UploadVideoInfo videoInfo) {

        UploadDeteleTaskInfo taskInfo=new UploadDeteleTaskInfo();

        if(null==videoInfo){
            taskInfo.setCancel(false);
            taskInfo.setMessage("要取消的视频参数为空");
            return taskInfo;
        }

        if(TextUtils.isEmpty(videoInfo.getUploadID())) {
            if(null==videoInfo){
                taskInfo.setCancel(false);
                taskInfo.setMessage("要取消的上传任务不存在");
                return taskInfo;
            }
        }
        //先暂停上传的任务
        String objectKey=videoInfo.getVideoFileKey();
        AbortMultipartUploadRequest abort = new AbortMultipartUploadRequest(Constant.STS_BUCKET, "Video/"+objectKey+".mp4", videoInfo.getUploadID());
        if(null!=ossTaskMap&&ossTaskMap.size()>0){
            Iterator<Map.Entry<Long, PauseableUploadTask>> iterator = ossTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, PauseableUploadTask> next = iterator.next();
                if(videoInfo.getId()==next.getKey()){
                    next.getValue().pause();
                    taskInfo.setCancel(true);
                    if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(videoInfo.getId());
                }
            }
        }
        if(null!=oss){
            try {
                //若无异常删除成功
                AbortMultipartUploadResult result = oss.abortMultipartUpload(abort);// 若无异常抛出说明删除成功
                if(null!=result){
                    if (204 == result.getStatusCode()) {
                        taskInfo.setCancel(true);
                        taskInfo.setMessage("取消上传成功");
                        return taskInfo;
                    }
                }
            } catch (ClientException e) {
                e.printStackTrace();
                taskInfo.setCancel(false);
                taskInfo.setMessage("取消失败，请检查您的网络连接");
                return taskInfo;

            } catch (ServiceException e) {
                taskInfo.setCancel(false);
                taskInfo.setMessage("取消失败，服务器无响应");
                return taskInfo;
            }
        }
        taskInfo.setCancel(false);
        taskInfo.setMessage("取消失败，未知原因");
        return taskInfo;
    }

    /**
     * 设置监听器
     * @param listener
     */
    public VideoUploadTaskManager setUploadListener(VideoUploadListener listener) {
        this.mVideoUploadListener=listener;
        return mInstance;
    }

    public void removeUploadListener(){
        this.mVideoUploadListener=null;
    }


    /**
     * 开始上传任务
     */
    private class UploadVideoAsyncTask {

        private final UploadVideoInfo mUploadInfo;

        public UploadVideoAsyncTask(UploadVideoInfo data) {
            this.mUploadInfo=data;
        }

        //执行上传任务
        public void execute() {
            if(null!=mUploadInfo){
                LogUtils.i(TAG,"文件地址："+mUploadInfo.getFilePath());
                //上传构造请求
                PauseableUploadRequest request = new PauseableUploadRequest(Constant.STS_BUCKET, "Video/"+mUploadInfo.getVideoFileKey()+".mp4", mUploadInfo.getFilePath(), PART_SIZE);
                //上传进度监听
                request.setProgressCallback(new OSSProgressCallback<PauseableUploadRequest>() {
                    @Override
                    public void onProgress(PauseableUploadRequest request, long currentSize, long totalSize) {
                        int progress = (int) (100 * currentSize / totalSize);
                        if(null!=mUploadInfo) mUploadInfo.setUploadProgress(progress);
                        if(null!=mVideoUploadListener) mVideoUploadListener.uploadProgress(mUploadInfo);
                    }
                });

                //上传状态监听
                PauseableUploadTask pauseableUploadTask = new PauseableUploadTask(oss, request, new OSSCompletedCallback<PauseableUploadRequest, PauseableUploadResult>() {
                    /**
                     * 上传成功
                     * @param request
                     * @param result
                     */
                    @Override
                    public void onSuccess(PauseableUploadRequest request, PauseableUploadResult result) {
                        //移除上传任务
                        if(null!=ossTaskMap&&ossTaskMap.size()>0){
                            ossTaskMap.remove(mUploadInfo.getId());
                        }
                        //上传成功
                        mUploadInfo.setUploadProgress(100);
                        ApplicationManager.getInstance().getVideoUploadDB().deleteUploadVideoInfo(mUploadInfo);
                        if(null!=mVideoUploadListener) mVideoUploadListener.uploadSuccess(mUploadInfo,null!=request?result.getServerCallbackReturnBody():null);
                    }

                    /**
                     * 上传失败
                     * @param request
                     * @param clientException
                     * @param serviceException
                     *  上传失败有几种状态
                     *  error_code:1:本地文件不存在或读取SD卡权限被拒  2：客户端网络不可用 3：服务端接受文件失败或者参数错误
                     */
                    @Override
                    public void onFailure(PauseableUploadRequest request, ClientException clientException, ServiceException serviceException) {
                        if(null!=serviceException){
                            LogUtils.i(TAG,"getErrorCode="+serviceException.getErrorCode());
                            LogUtils.i(TAG,"getHostId="+serviceException.getHostId());
                            LogUtils.i(TAG,"getRawMessage="+serviceException.getRawMessage());
                            LogUtils.i(TAG,"getRequestId="+serviceException.getRequestId());
                            LogUtils.i(TAG,"getMessage="+serviceException.getMessage());
                            LogUtils.i(TAG,"getLocalizedMessage="+serviceException.getLocalizedMessage());
                            LogUtils.i(TAG,"getStatusCode="+serviceException.getStatusCode());
                        }
                        if(null!=clientException){
                            LogUtils.i(TAG,"ClientException--getMessage="+clientException.getMessage());
                        }
                        //移除上传任务
                        if(null!=ossTaskMap&&ossTaskMap.size()>0)ossTaskMap.remove(mUploadInfo.getId());
                        File file = new File(mUploadInfo.getFilePath());
                        LogUtils.i(TAG,"上传失败--文件地址："+mUploadInfo.getFilePath());
                        if(null!=file&&file.exists()&&file.isFile()){
                            if(null!=clientException){
                                if(null!=mVideoUploadListener){
                                    mVideoUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_CLIENTEXCEPTION,"上传失败");
                                }
                                return;
                            }else if(null!=serviceException){
                                if(null!=mVideoUploadListener){
                                    if(404==serviceException.getStatusCode()){
                                        ApplicationManager.getInstance().getVideoUploadDB().deleteUploadVideoInfo(mUploadInfo);
                                    }
                                    mVideoUploadListener.uploadFail(mUploadInfo,serviceException.getStatusCode(),Constant.UPLOAD_ERROR_CODE_SERVICEEXCEPTION,"上传失败");
                                }
                                return;
                            }else{
                                if(null!=mVideoUploadListener){
                                    mVideoUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_OTHER,"上传失败");
                                }
                                return;
                            }
                        }else{
                            //文件不存在
                            if(null!=mVideoUploadListener){
                                ApplicationManager.getInstance().getVideoUploadDB().deleteUploadVideoInfo(mUploadInfo);
                                mVideoUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_FILE_NOTFIND,"要上传的视频未找到或者要读取SD卡权限被拒！");
                            }
                            return;
                        }
                    }
                });
                //设置上传参数,由阿里云回调给后台
                setUserPrams(pauseableUploadTask,mUploadInfo);
                try {
                    //新的上传任务
                    String uploadID=null;
                    if(TextUtils.isEmpty(mUploadInfo.getUploadID())){
                        uploadID = pauseableUploadTask.initUpload();//生成上传任务ID
                        mUploadInfo.setUploadID(uploadID);
                    }else{
                        uploadID=mUploadInfo.getUploadID();
                    }
                    //添加进任务管理实例中
                    if(null!=ossTaskMap){
                        ossTaskMap.put(mUploadInfo.getId(), pauseableUploadTask);
                    }
                    if(null!=mVideoUploadListener){
                        mVideoUploadListener.uploadStart(mUploadInfo);
                    }
                    pauseableUploadTask.upload(uploadID);
                }
                catch (ServiceException e) {
                    e.printStackTrace();
                    if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(mUploadInfo.getId());//移除上传任务栈中的元素
                    if(404==e.getStatusCode()){
                        ApplicationManager.getInstance().getVideoUploadDB().deleteUploadVideoInfo(mUploadInfo);
                    }
                    if(null!=mVideoUploadListener){
                        mVideoUploadListener.uploadFail(mUploadInfo,e.getStatusCode(),Constant.UPLOAD_ERROR_CODE_SERVICEEXCEPTION,"上传失败-"+e.getMessage());
                    }
                }
                catch (ClientException e) {
                    e.printStackTrace();
                    if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(mUploadInfo.getId());//移除上传任务栈中的元素
                    if(null!=mVideoUploadListener){
                        mVideoUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_CLIENTEXCEPTION,"上传失败-"+e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(null!=ossTaskMap&&ossTaskMap.size()>0) ossTaskMap.remove(mUploadInfo.getId());//移除上传任务栈中的元素
                    if(null!=mVideoUploadListener){
                        ApplicationManager.getInstance().getVideoUploadDB().deleteUploadVideoInfo(mUploadInfo);
                        mVideoUploadListener.uploadFail(mUploadInfo,0,Constant.UPLOAD_ERROR_CODE_FILE_NOTFIND,"上传失败-要上传的视频未找到或者要读取SD卡权限被拒！");
                    }
                }
            }
        }
    }

    /**
     * 设置自定义参数
     * @param pauseableUploadTask
     * @param videoInfo
     */
    private void setUserPrams(PauseableUploadTask pauseableUploadTask, UploadVideoInfo videoInfo) {
        String[] locationID =VideoApplication.getInstance().getLocations();
        Map<String,String> mParams = new HashMap<>();
        mParams.put("callbackUrl",uploadParamsConfig.getCallbackAddress());
        mParams.put("callbackHost",uploadParamsConfig.getCallBackHost());
        mParams.put("callbackBodyType",uploadParamsConfig.getCallBackType());
        mParams.put("callbackBody","{\"bucket\":${bucket},\"object\":${object},\"mimeType\":${mimeType},\"size\":${size},\"filename\":${object},\"imeil\":${x:imeil},\"user_id\":${x:user_id},\"desp\":${x:desp},\"cur_frame\":${x:cur_frame},\"is_private\":${x:is_private}," +
                "\"video_id\":${x:video_id},\"video_width\":${x:video_width},\"video_height\":${x:video_height},\"frame_num\":${x:frame_num},\"code_rate\":${x:code_rate},\"video_durtion\":${x:video_durtion},\"device_net_ip\":${x:device_net_ip},\"device_longitude\":${x:device_longitude},\"device_latitude\":${x:device_latitude},\"upload_type\":${x:upload_type},\"music_id\":${x:music_id},\"download_permiss\":${x:download_permiss}}");

        Map<String,String>mParamVars = new HashMap<>();
        mParamVars.put("x:imeil", VideoApplication.mUuid);
        mParamVars.put("x:user_id",VideoApplication.getLoginUserID());
        mParamVars.put("x:desp",videoInfo.getVideoDesp());//视频详情介绍
        mParamVars.put("x:cur_frame",videoInfo.getVideoCoverFps()+"");//封面
        mParamVars.put("x:is_private",videoInfo.getIsPrivate()?"1":"0");//是否隐私
        mParamVars.put("x:video_id",videoInfo.getId()+videoInfo.getVideoFileKey());//视频ID
        mParamVars.put("x:video_width",videoInfo.getVideoWidth()+"");//视频宽
        mParamVars.put("x:video_height",videoInfo.getVideoHeight()+"");//视频高
        mParamVars.put("x:frame_num",videoInfo.getVideoFps()+"");//视频帧数量
        mParamVars.put("x:code_rate",videoInfo.getVideoBitrate()+"");//码率
        mParamVars.put("x:video_durtion",videoInfo.getVideoDurtion()+"");//时长
        mParamVars.put("x:upload_type",videoInfo.getSourceType()+"");//上传类型
        String locastHostIP = SystemUtils.getLocastHostIP();
        mParamVars.put("x:device_net_ip",TextUtils.isEmpty(locastHostIP)?"0":locastHostIP );//IP
        mParamVars.put("x:device_longitude",null!=locationID&&locationID.length>0?locationID[0]:"0");//经度
        mParamVars.put("x:device_latitude",null!=locationID&&locationID.length>1?locationID[1]:"0");//纬度
        mParamVars.put("x:music_id",videoInfo.getMusicID());
        mParamVars.put("x:download_permiss",videoInfo.getDownloadPermiss());
        pauseableUploadTask.setUserPrams(mParams,mParamVars);
    }
}
