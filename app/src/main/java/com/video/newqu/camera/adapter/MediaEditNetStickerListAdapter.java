package com.video.newqu.camera.adapter;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.video.newqu.R;
import com.video.newqu.bean.StickerDataBean;
import com.video.newqu.camera.model.StickerViewHolder;
import com.video.newqu.comadapter.BaseQuickAdapter;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.OnMediaStickerListener;
import com.video.newqu.util.FileUtils;
import com.video.newqu.util.Logger;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TinyHung@Outlook.com
 * 2017/9/11.
 * 视频编辑界面贴纸适配器
 */

public class MediaEditNetStickerListAdapter extends BaseQuickAdapter<StickerDataBean,StickerViewHolder> {

    private final int mItemWidth;
    private  File mStickerImagePath;
    private final OnMediaStickerListener mOnMediaStickerListener;
    private final ExecutorService mThreadPool;
    private boolean isDownload=true;

    public MediaEditNetStickerListAdapter(List<StickerDataBean> data, OnMediaStickerListener onMediaStickerListener) {
        super(R.layout.re_media_netsticker_item_layout,data);
        mItemWidth = (ScreenUtils.getScreenWidth()- Utils.dip2px(60))/5;
        this.mOnMediaStickerListener=onMediaStickerListener;
        mThreadPool = Executors.newCachedThreadPool();
        //将素材存储在内部缓存
        mStickerImagePath = new File(Constant.BASE_CACHE_PATH+"/Sticker");
        if(!mStickerImagePath.exists()){
            mStickerImagePath.mkdirs();
        }
    }

    @Override
    protected void convert(StickerViewHolder helper, StickerDataBean item) {
        if(item==null||null==helper) return;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) helper.re_item_sticker.getLayoutParams();
        layoutParams.height=mItemWidth;
        layoutParams.width= LinearLayout.LayoutParams.MATCH_PARENT;
        helper.re_item_sticker.setLayoutParams(layoutParams);
        String cover=item.getSrc();
        if(item.getSrc().endsWith(".webp")){
            cover=item.getSrc();
        }
        //设置缩略图
        Glide.with(mContext)
                .load(cover)
                .error(R.drawable.iv_media_sticker_min_error)
                .thumbnail(0.1f)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存结果
                .skipMemoryCache(true)//跳过内存缓存
                .into(helper.iv_item_sticker);

        helper.circle_progressbar.setMaxProgress(100);
        helper.circle_progressbar.setProgress(0);

        helper.setVisible(R.id.iv_gif_tips,!TextUtils.isEmpty(item.getSrc())&&item.getSrc().endsWith(".png")?false:true);
        ImageView view = helper.getView(R.id.iv_gif_tips);
        view.setImageResource(view.getVisibility()==View.VISIBLE?R.drawable.gif_tips:0);
        //是否已下载
        File file =new File(mStickerImagePath, item.getId()+"_"+FileUtils.getFileName(item.getSrc()));
        //已经下载了该素材
        if(file.exists()&&file.isFile()){
            helper.setVisible(R.id.iv_download_icon,false);
        }else{
            helper.setVisible(R.id.iv_download_icon,item.getIsDownloading()?false:true);
        }

        //是否正在下载
        helper.re_progress.setVisibility(item.getIsDownloading()?View.VISIBLE:View.GONE);
        //下载的点击事件
        helper.itemView.setOnClickListener(new OnItemClickListener(helper,file,item,helper.getAdapterPosition()));
    }

    public void stopDownload() {
        this.isDownload=false;
    }
    public void pause(boolean isPause) {
        if(isPause){
            this.isDownload=false;
        }else{
            this.isDownload=true;
        }
    }

    /**
     * 处理点击素材的点击事件
     */
    private class OnItemClickListener implements View.OnClickListener{
        private final StickerDataBean data;
        private final StickerViewHolder itemView;
        private final File filePath;
        public OnItemClickListener(StickerViewHolder helper, File filePath, StickerDataBean item, int adapterPosition) {
            this.itemView=helper;
            this.data=item;
            this.filePath=filePath;
        }
        @Override
        public void onClick(View v) {
            if(null!=data&&null!=data.getSrc()&&data.getSrc().endsWith(".png")){
                if(null!=filePath&&filePath.exists()&&filePath.isFile()){
                    saveLocationList(data);
                    if(null!=mOnMediaStickerListener){
                        mOnMediaStickerListener.onStickerAddItemClick(filePath.getAbsolutePath());
                    }
                    return;
                }else if(null!=mStickerImagePath){
                    if(!mStickerImagePath.exists()){
                        mStickerImagePath.mkdirs();
                    }
                    //本地不存在，下载
                    new DownloadFileTask(itemView,data).executeOnExecutor(mThreadPool,data.getSrc());
                }else{
                    mStickerImagePath = new File(Constant.BASE_CACHE_PATH+"/Sticker");
                    if(!mStickerImagePath.exists()){
                        mStickerImagePath.mkdirs();
                    }
                    //本地不存在，下载
                    new DownloadFileTask(itemView,data).executeOnExecutor(mThreadPool,data.getSrc());
                }
            }else{
                if(!Utils.isCheckNetwork()){
                    ToastUtils.showCenterToast("请先开启网络!");
                    return;
                }
                if(null!=mOnMediaStickerListener){
                    mOnMediaStickerListener.onStickerAddItemClick(data.getSrc());
                }

                if(null!=filePath&&filePath.exists()&&filePath.isFile()){
                    saveLocationList(data);
                    return;
                }else if(null!=mStickerImagePath){
                    if(!mStickerImagePath.exists()){
                        mStickerImagePath.mkdirs();
                    }
                    //本地不存在，下载
                    new DownloadFileTask(itemView,data).executeOnExecutor(mThreadPool,data.getSrc());
                }else{
                    mStickerImagePath = new File(Constant.BASE_CACHE_PATH+"/Sticker");
                    if(!mStickerImagePath.exists()){
                        mStickerImagePath.mkdirs();
                    }
                    //本地不存在，下载
                    new DownloadFileTask(itemView,data).executeOnExecutor(mThreadPool,data.getSrc());
                }
            }
        }
    }

    /**
     * 保存使用记录
     * @param data
     */
    private void saveLocationList(StickerDataBean data) {
        if(null!=data){
            data.setUpdataTime(System.currentTimeMillis());
            boolean stickerInfo = ApplicationManager.getInstance().getStickerDB().insertNewStickerInfo(data);
            if(stickerInfo){
                ApplicationManager.getInstance().observerUpdataToMusic(Constant.OBSERVABLE_ACTION_STICKER_CHANGED);
            }
        }
    }

    /**
     * 素材下载
     */
    private class DownloadFileTask extends AsyncTask<String,Integer,File>{
        private int laterate = 0;//当前已读字节
        private final StickerViewHolder itemView;
        private final StickerDataBean data;

        public DownloadFileTask(StickerViewHolder itemView, StickerDataBean data) {
            this.itemView=itemView;
            this.data=data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(null!=data){
                data.setIsDownloading(true);
            }
            if(null!=itemView){
                itemView.circle_progressbar.setVisibility(View.GONE);
                itemView.iv_download_icon.setVisibility(View.GONE);
                itemView.re_progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected File doInBackground(String... params) {
            String name=data.getId()+"_"+FileUtils.getFileName(params[0]);
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
                    File outPutPath = new File(mStickerImagePath.getAbsolutePath());
                    if (!outPutPath.exists()) {
                        outPutPath.mkdirs();
                    }
                    File apkDownloadPath = new File(outPutPath, name);
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
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(null!=itemView&&null!=itemView.circle_progressbar){
                if(itemView.circle_progressbar.getVisibility()!=View.VISIBLE){
                    itemView.circle_progressbar.setVisibility(View.VISIBLE);
                }
                itemView.circle_progressbar.setProgressNotInUiThread(values[0]);
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            //下载成功
            if(null!=file&&file.exists()){
                if(null!=data){
                    data.setIsDownloading(false);
                }
                if(null!=itemView){
                    itemView.circle_progressbar.setVisibility(View.GONE);
                    itemView.iv_download_icon.setVisibility(View.GONE);
                    itemView.re_progress.setVisibility(View.GONE);
                }
            }else{
                if(null!=itemView){
                    itemView.re_progress.setVisibility(View.GONE);
                    itemView.circle_progressbar.setVisibility(View.GONE);
                    itemView.iv_download_icon.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
