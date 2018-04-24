package com.video.newqu.util;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.contants.Constant;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.manager.DBScanWeiCacheManager;
import com.video.newqu.ui.activity.MainActivity;
import com.video.newqu.ui.fragment.WinXinVideoListFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/7/17 11:13
 * 微信视频扫描
 */

public class ScanWeChatDirectoryTask extends AsyncTask<String,Void,List<WeiXinVideo>> {

    private  MainActivity mContext;
    private ScanWeixin mScanWeixin;

    public  ScanWeChatDirectoryTask(MainActivity context){
        this.mContext=context;
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
                    List<WeiXinVideo> newVideoList=null;
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
                                if (newVideoList.size() >= 9) {
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
                                if (newVideoList.size() >= 9) {
                                    break;
                                }
                                newVideoList.add(weiXinVideos.get(i));
                            }
                        }
//                        if(null!=newVideoList&&newVideoList.size()>0){
//                            for (int i = 0; i < newVideoList.size(); i++) {
//                                WeiXinVideo weiXinVideo = newVideoList.get(i);
//                                DBScanWeiCacheManager.insertNewUploadVideoInfo(weiXinVideo);
//                            }
//                        }
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
            if(null!=mContext){
                WinXinVideoListFragment fragment = WinXinVideoListFragment.newInstance();
                fragment.setOnDialogUploadListener(new WinXinVideoListFragment.OnDialogUploadListener() {
                    @Override
                    public void onUpload() {
                        //用户确定了上传事件
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_ADD_UPLOAD_TAKS);
                    }
                });
                FragmentManager supportFragmentManager = mContext.getSupportFragmentManager();
                fragment.show(supportFragmentManager,"winxin_video");
                fragment.setData(weiXinVideos);
            }
        }
    }
}
