package com.video.newqu.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.UpdataApkInfo;
import com.video.newqu.listener.OnDownloadListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TinyHung@Outlook.com
 * 2017/8/31.
 * 下载APK文件
 */

public class DownloadApkTask {

    public  static DownloadApkTask mInstance;
    private  Context mContext;
    private boolean isDownload;//是否正在下载
    protected int laterate = 0;//当前已读字节
    private String mOutPath;//目标存储位置
    protected boolean exit=true;//是否停止下载
    private CanceledReceiver mNotificationBroad;
    private File mApkFile;
    private String mUrl;
    public static final String INTENT_ACTION_DOWNLOAD="action_download";//点击事件Action
    private static final int NOTIFACATION_ID_UPDATA = 123;//下载完成的通知栏ID
    private NotificationManager manager ;
    private NotificationCompat.Builder builder;
    private OnDownloadListener onDownloadListener;
    private String mNetVerstionCode;


    public DownloadApkTask(Context context){
        this.mContext=context;
        manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        registerBroader();
    }


    public static synchronized DownloadApkTask getInstance(Context context) {
        synchronized (DownloadApkTask.class){
            if(null==mInstance){
                mInstance=new DownloadApkTask(context);
            }
        }
        return mInstance;
    }


    public void onDestroy(){
        exit=false;
        unRegisterBroader();
        if(null!=manager){
            manager.cancel(NOTIFACATION_ID_UPDATA);
            manager.cancelAll();
        }
    }


    /**
     * 注册广播
     */
    public void registerBroader() {
        if(null==mContext) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ACTION_DOWNLOAD);//点击通知栏
        mNotificationBroad = new CanceledReceiver();
        mContext.registerReceiver(mNotificationBroad, filter);
    }


    public void unRegisterBroader(){
        if(null!=mContext&&null!=mNotificationBroad){
            mContext.unregisterReceiver(mNotificationBroad);//AppLicationContet
        }
    }

    /**
     * 刷新通知栏
     * @param progress
     */
    private void upldataProgress(int progress){
        if(null!=builder){
            builder.setContentText("已下载："+progress+"%");
            builder.setProgress(100, progress, false);
            Notification no = builder.build();
            if(null!=manager) manager.notify(NOTIFACATION_ID_UPDATA,no);
        }
    }

    /**
     * 创建通知栏
     * @param ticker
     * @param title
     * @param content
     * @param progress
     * @param isDownloadFinlish
     */
    private void onResetNotifaction(String ticker,String title,String content,int progress,boolean isDownloadFinlish) {
        if(null==manager) return;
        builder = new NotificationCompat.Builder(mContext);
        builder.setPriority(Notification.PRIORITY_MAX);//最大优先级
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher));
        Intent intentClick = new Intent(INTENT_ACTION_DOWNLOAD);
        intentClick.putExtra(INTENT_ACTION_DOWNLOAD,INTENT_ACTION_DOWNLOAD);
        PendingIntent canceledPendingIntent = PendingIntent.getBroadcast(mContext, 1, intentClick, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(canceledPendingIntent);
        builder.setTicker(ticker);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setProgress(100, progress, isDownloadFinlish);
        Notification notification = builder.build();
        notification.defaults=Notification.DEFAULT_ALL;
        if(null!=manager)  manager.notify(NOTIFACATION_ID_UPDATA, notification);
    }


    /**
     * 监听重新下载的点击事件
     */
    class CanceledReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_DOWNLOAD.equals(intent.getStringExtra(INTENT_ACTION_DOWNLOAD))) {
                if(!isDownload){
                    //本地存在安装包
                    if (null!=mApkFile&&mApkFile.exists() && mApkFile.isFile()&& Utils.getAPKPathVerstion(mContext, mApkFile)==Integer.parseInt(mNetVerstionCode)) {

                        installApk(mApkFile);
                        //本地不存在安装包
                    } else {

                        if(!TextUtils.isEmpty(mUrl)){
                            new DownloadApkAsyncTask().execute(mUrl);
                        }
                    }
                }else{
                    ToastUtils.showCenterToast("安装包正在下载中");
                }
            }
        }
    }





    /**
     * 开始下载
     */
    public void download(UpdataApkInfo.DataBean data,String outPutPath,OnDownloadListener onDownloadListener){
        if(isDownload) {
            if(null!=onDownloadListener){
                onDownloadListener.onDownloading();
            }
            return;
        }
        this.onDownloadListener=onDownloadListener;
        if(!TextUtils.isEmpty(data.getDownload())){
            this.mOutPath=outPutPath;
            this.mUrl=data.getDownload();
            this.mNetVerstionCode=data.getVersion_code();
            new DownloadApkAsyncTask().execute(data.getDownload());
        }
    }

    /**
     * 下载APK
     */
    private class DownloadApkAsyncTask extends AsyncTask<String, Integer, File> {

        @Override
        protected void onPreExecute() {
            isDownload=true;
            onResetNotifaction("软件下载","安装包下载","安装包下载中",0,false);
        }

        /**
         * 下载中
         * @param params
         * @return
         */
        @Override
        protected File doInBackground(String... params) {
            if (!TextUtils.isEmpty(params[0])) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String apkName = Utils.getFileName(params[0]);//文件名，从网络端获取
                    try {
                        URL url = new URL(params[0]);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(900000);
                        conn.setConnectTimeout(900000);
                        conn.setDoInput(true);
                        conn.setRequestMethod("GET");
                        if(conn.getResponseCode()==200){
                            int length = conn.getContentLength();
                            int count = 0;
                            File apkPathFile = new File(mOutPath);
                            if (!apkPathFile.exists()) {
                                apkPathFile.mkdirs();
                            }
                            File apkDownloadPath = new File(apkPathFile, apkName);
                            InputStream in = conn.getInputStream();
                            FileOutputStream os = new FileOutputStream(apkDownloadPath);
                            byte[] buffer = new byte[1024];
                            do {
                                int numread = in.read(buffer);
                                count += numread;
                                int progress = (int) (((float) count / length) * 100);// 得到当前进度
                                if (progress >= laterate + 1) {// 只有当前进度比上一次进度大于等于1，才可以更新进度
                                    laterate = progress;
                                    this.publishProgress(progress);
                                }
                                if (numread <= 0) {//下载完毕
                                    break;
                                }
                                os.write(buffer, 0, numread);
                            } while (exit);
                            in.close();
                            os.close();
                            return apkDownloadPath;
                        }else{
                            Log.d("下载更新", "doInBackground: conn.getResponseCode()="+conn.getResponseCode());
                            return null;
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return null;
                    } catch (IOException e) {
                        e.toString();
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    ToastUtils.showCenterToast("下载安装包失败，请检查SD卡");
                    return null;
                }
            }else{
                return null;
            }
        }

        /**
         * 更新进度条
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            upldataProgress(values[0]);
        }

        /**
         * 下载完成
         * @param result
         */
        @Override
        protected void onPostExecute(File result) {
            isDownload=false;
            if(null!=result&&result.exists()&&result.isFile()&&0!=Utils.getAPKPathVerstion(VideoApplication.getInstance(), result)){
                mApkFile=result;
                onResetNotifaction("软件下载","安装包下载完成","安装已下载完成，点击安装",0,false);
                if(null!=onDownloadListener){
                    onDownloadListener.onDownloadFinlish();
                }
                installApk(mApkFile);
            }else{
                if(null!=onDownloadListener){
                    onDownloadListener.onDownloadError("下载失败");
                }
            }
        }
    }

    /**
     * 安装apk
     */
    public void installApk(File filePath) {
        if(null!=filePath&&filePath.exists()&&filePath.isFile()&&0!=Utils.getAPKPathVerstion(VideoApplication.getInstance(), filePath)){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.parse("file://" + filePath.toString()),
                    "application/vnd.android.package-archive");
            if(null!=mContext) mContext.startActivity(intent);
        }
    }
}
