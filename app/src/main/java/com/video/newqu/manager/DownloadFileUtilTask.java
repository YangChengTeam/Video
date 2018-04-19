package com.video.newqu.manager;

import android.os.AsyncTask;

import com.video.newqu.util.FileUtils;
import com.video.newqu.util.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * TinyHung@Outlook.com
 * 2017/11/9
 * 下载音乐工具类
 */

public class DownloadFileUtilTask extends AsyncTask<String,Integer,File> {

    public static final String TAG=DownloadFileUtilTask.class.getSimpleName();

    private final String targetPath;
    private int laterate = 0;//当前已读字节
    private boolean isDownload=true;//是否下载


    public interface OnDownloadListener{
        void onStartDownload();
        void onDownloadError(String e);
        void onDownloadProgress(int progress);
        void onWownloadFilish(File file);
    }

    private OnDownloadListener mOnDownloadListener;


    public DownloadFileUtilTask(String targetPath,OnDownloadListener onDownloadListener) {
        this.targetPath=targetPath;
        this.mOnDownloadListener=onDownloadListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(null!=mOnDownloadListener){
            mOnDownloadListener.onStartDownload();
        }
    }

    @Override
    protected File doInBackground(String... params) {
        String fileName=FileUtils.getFileName(params[0]);
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
                File outPutPath = new File(targetPath);
                if (!outPutPath.exists()) {
                    outPutPath.mkdirs();
                }
                File apkDownloadPath = new File(outPutPath, fileName);
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
                } while (isDownload);
                in.close();
                os.close();
                return apkDownloadPath;
            }else{
                if(null!=mOnDownloadListener){
                    mOnDownloadListener.onDownloadError("下载失败！请检查网络连接！");
                }
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if(null!=mOnDownloadListener){
                mOnDownloadListener.onDownloadError("下载失败！"+e.getMessage());
            }
            return null;
        } catch (IOException e) {
            e.toString();
            e.printStackTrace();
            if(null!=mOnDownloadListener){
                mOnDownloadListener.onDownloadError(e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(null!=mOnDownloadListener){
            mOnDownloadListener.onDownloadProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if(null!=mOnDownloadListener){
            mOnDownloadListener.onWownloadFilish(file);
        }
    }
}
