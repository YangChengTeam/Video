package com.video.newqu.util.attach;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.contants.Constant;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.manager.DBScanWeiCacheManager;
import com.video.newqu.ui.dialog.WinXinVideoListDialog;
import com.video.newqu.util.Logger;
import com.video.newqu.util.ScanWeixin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/7/17 11:13
 * 微信视频扫描
 */

public class ScanWeChatDirectoryVideoTask extends AsyncTask<String,Void,List<WeiXinVideo>> {

    private  int mMaxVideoNum=9;//一次最大展示扫描结果视频长度
    private Context mContext;
    private ScanWeixin mScanWeixin;

    public ScanWeChatDirectoryVideoTask(Context context, int maxVideoNum){
        this.mContext=context;
        this.mMaxVideoNum=maxVideoNum;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(null==mScanWeixin) mScanWeixin = new ScanWeixin();
    }

    @Override
    protected List<WeiXinVideo> doInBackground(String... params) {
        try {
            if(null!=params&&params.length>0){
                if(null!=mScanWeixin){
                    mScanWeixin.setExts("mp4");
                    mScanWeixin.setScanEvent(true);
                    mScanWeixin.setEvent(false);
                    mScanWeixin.setMinDurtion(3);
                    mScanWeixin.setMaxDurtion(Constant.MEDIA_VIDEO_EDIT_MAX_DURTION);
                    List<WeiXinVideo> weiXinVideos =mScanWeixin.scanFiles(params[0]);
                    List<WeiXinVideo> newVideoList;
                    if (null != weiXinVideos && weiXinVideos.size() > 0) {
                        //对视频时间进行倒序排序
                        Collections.sort(weiXinVideos, new Comparator<WeiXinVideo>() {
                            @Override
                            public int compare(WeiXinVideo o1, WeiXinVideo o2) {
                                return o2.getVideoCreazeTime().compareTo(o1.getVideoCreazeTime());
                            }
                        });
                        DBScanWeiCacheManager DBScanWeiCacheManager = new DBScanWeiCacheManager(mContext);
                        newVideoList = new ArrayList<>();
                        //只保留9个最新视频,且与上次不能重复
                        List<WeiXinVideo> locationVideoList = DBScanWeiCacheManager.getUploadVideoList();//之前扫描的所有记录
                        if (null != locationVideoList && locationVideoList.size() > 0) {
                            for (int i = 0; i < weiXinVideos.size(); i++) {
                                if (newVideoList.size() >= mMaxVideoNum) {
                                    break;
                                }
                                WeiXinVideo weiXinVideo = weiXinVideos.get(i);
                                boolean flag = false;
                                for (int j = 0; j < locationVideoList.size(); j++) {
                                    WeiXinVideo locationVideo = locationVideoList.get(j);
                                    if (TextUtils.equals(weiXinVideo.getFileName(), locationVideo.getFileName())) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if(!flag) {
                                    newVideoList.add(weiXinVideo);
                                }
                            }
                        } else {
                            for (int i = 0; i < weiXinVideos.size(); i++) {
                                if (newVideoList.size() >= mMaxVideoNum) {
                                    break;
                                }
                                newVideoList.add(weiXinVideos.get(i));
                            }
                        }
                        if(null!=newVideoList&&newVideoList.size()>0){
                            for (int i = 0; i < newVideoList.size(); i++) {
                                WeiXinVideo weiXinVideo = newVideoList.get(i);
                                DBScanWeiCacheManager.insertNewUploadVideoInfo(weiXinVideo);
                            }
                        }
                        return newVideoList;
                    } else {
                        return null;
                    }
                }
            }
        }catch (Exception e){

        }
        return null;
    }

    @Override
    protected void onPostExecute(List<WeiXinVideo> weiXinVideos) {
        super.onPostExecute(weiXinVideos);
        if(null!=weiXinVideos&&weiXinVideos.size()>0){
            Activity activity = VideoApplication.getInstance().getRunActivity();
            if(null!=activity&&!activity.isFinishing()){
                WinXinVideoListDialog dialog =new WinXinVideoListDialog(activity);
                dialog.setOnDialogUploadListener(new WinXinVideoListDialog.OnDialogUploadListener() {
                    @Override
                    public void onUpload() {
                        //用户确定了上传事件
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_SCANWEIXIN_VIDEO_FINLISH);
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_ADD_UPLOAD_TAKS);
                        mContext=null;mScanWeixin=null;mMaxVideoNum=0;
                    }
                    @Override
                    public void onDissmiss() {
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_SCANWEIXIN_VIDEO_FINLISH);//告诉首页，弹窗已经关闭了
                    }
                });
                dialog.show();
                dialog.setData(weiXinVideos);
            }
        }
    }
}
