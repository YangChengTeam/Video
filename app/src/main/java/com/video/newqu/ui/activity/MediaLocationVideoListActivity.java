package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.video.newqu.R;
import com.video.newqu.adapter.MoivesListAdapter;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.ActivityMediaVideoListBinding;
import com.video.newqu.manager.StatusBarManager;
import com.video.newqu.ui.dialog.ScanVideoProgress;
import com.video.newqu.ui.fragment.ImportVideoFolderFragment;
import com.video.newqu.ui.fragment.ImportVideoSelectorFragment;
import com.video.newqu.ui.fragment.MediaVideoLocationFragment;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.ImageCache;
import com.video.newqu.util.ScanWeixin;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import org.greenrobot.eventbus.EventBus;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/8/19
 * 展示系统相册所有视频，文件夹目录和文件夹二级目录
 */

public class MediaLocationVideoListActivity extends BaseActivity<ActivityMediaVideoListBinding> {

    private MoivesListAdapter mMoivesListAdapter;
    private ScanVideoProgress mScanVideoProgress;
    private ScanWeixin mDialogScanWeixin;//弹窗扫描类型
    private MediaVideoLocationFragment mMediaVideoLocationFragment;

    public static void start(Context context) {
        context.startActivity(new Intent(context,MediaLocationVideoListActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_video_list);
        showToolBar(false);
        findViewById(R.id.view_state_bar).setVisibility(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M?View.GONE:View.VISIBLE);
        StatusBarManager.getInstance().init(this,  CommonUtils.getColor(R.color.white), 0,true);
        mMediaVideoLocationFragment = new MediaVideoLocationFragment();
        addReplaceFragment(mMediaVideoLocationFragment,"相册");
        //第一次使用弹出使用提示
        if(1!= SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_SCANVIDEO_CODE)){
            bindingView.tvTipsMessage.setVisibility(View.VISIBLE);
            bindingView.tvTipsMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                }
            });
            SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_SCANVIDEO_CODE,1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查SD读写权限
        RxPermissions.getInstance(MediaLocationVideoListActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){

                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MediaLocationVideoListActivity.this)
                            .setTitle("SD读取权限申请失败")
                            .setMessage("部分权限被拒绝，将无法使用本地视频相册功能，请先授予足够权限再使用视频扫描功能！授权成功后请重启开启本界面。是否现在去设置？");
                    builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(MediaLocationVideoListActivity.this,141);
                        }
                    });
                    builder.show();
                }
            }
        });
    }



    @Override
    public void initViews() {

        bindingView.tvTopRightTitle.setText("相册列表");
        bindingView.tvTopRightTitle.setVisibility(View.VISIBLE);
        //返回
        bindingView.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //逐个关闭Fragment
                onBackPressed();
            }
        });

        //点击了列表相册
        bindingView.tvTopRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingView.tvTopRightTitle.setVisibility(View.GONE);
                if(null!=bindingView.tvTipsMessage&&bindingView.tvTipsMessage.getVisibility()==View.VISIBLE){
                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                }
                addReplaceFragment(new ImportVideoFolderFragment(),"相册列表");
            }
        });


        //扫描不正确提示
        bindingView.tvTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.support.v7.app.AlertDialog.Builder(MediaLocationVideoListActivity.this)
                        .setTitle("视频扫描规则说明")
                        .setMessage("扫描到的视频数量和前面界面标注的数量不一样？是程序过滤了不受支持的视频格式文件。")
                        .setPositiveButton("确定", null).setCancelable(true).show();
            }
        });
    }


    private void showScanProgressDialog(){
        PopupMenu actionMenu = new PopupMenu(MediaLocationVideoListActivity.this, bindingView.getRoot(), Gravity.TOP | Gravity.RIGHT);
        actionMenu.inflate(R.menu.location_video_menu);
        actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    //全盘扫描
                    case R.id.actionbar_scan:
                        new ScanLocationVideoTask().execute();
                        break;
                }
                return false;
            }
        });
        actionMenu.show();
    }

    /**
     * 添加界面
     * @param fragment
     */
    public void addReplaceFragment(Fragment fragment,String title) {
        bindingView.tvTitle.setText(title);
        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment, title);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 打开公用的相册缩略图列表界面
     * @param path
     * @param name
     */
    public void addFolderFragment(String path, String name) {
        bindingView.tvTitle.setText(name);
        bindingView.tvTips.setVisibility(View.VISIBLE);
        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, ImportVideoSelectorFragment.newInstance(path,name), name);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }



    @Override
    public void onBackPressed() {
        //只剩下到首页一个界面了
        if(getSupportFragmentManager().getBackStackEntryCount()==1&&!MediaLocationVideoListActivity.this.isFinishing()){
            finish();
            return;
        }
        //文件夹列表
        if(getSupportFragmentManager().getBackStackEntryCount()==2&&!MediaLocationVideoListActivity.this.isFinishing()){
            bindingView.tvTitle.setText("相册");
            bindingView.tvTopRightTitle.setVisibility(View.VISIBLE);
        }
        //文件夹二级视频缩略图列表
        if(getSupportFragmentManager().getBackStackEntryCount()==3&&!MediaLocationVideoListActivity.this.isFinishing()){
            bindingView.tvTitle.setText("相册列表");
            bindingView.tvTopRightTitle.setVisibility(View.GONE);
            bindingView.tvTips.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }



    /**
     * 全盘扫描视频
     */
    private class ScanLocationVideoTask extends AsyncTask<Void,String,List<WeiXinVideo>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mScanVideoProgress = new ScanVideoProgress(MediaLocationVideoListActivity.this, R.style.UpdataDialogAnimation);
            mScanVideoProgress.setOnDissmissListener(new ScanVideoProgress.OnDissmissListener() {
                @Override
                public void onDissmiss() {
                    if(null!=mScanVideoProgress&&mScanVideoProgress.isShowing()&&!MediaLocationVideoListActivity.this.isFinishing()){
                        mScanVideoProgress.dismiss();
                    }
                    //停止扫描
                    if(null!=mDialogScanWeixin){
                        mDialogScanWeixin.setScanEvent(false);
                    }
                }
            });
            mScanVideoProgress.show();
        }

        @Override
        protected List<WeiXinVideo> doInBackground(Void... params) {
            List<WeiXinVideo> weiXinVideos=null;
            mDialogScanWeixin =new  ScanWeixin();
            mDialogScanWeixin.setExts("mp4", "3gp", "mov");
            mDialogScanWeixin.setFilterFolderPath(ApplicationManager.getInstance().getOutPutPath(0));
            mDialogScanWeixin.setListener(new ScanWeixin.OnScanListener() {
                @Override
                public void onScanProgress(String file) {
                    ScanLocationVideoTask.this.publishProgress(file);
                }
            });
            weiXinVideos= mDialogScanWeixin.scanFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
            if(null!=weiXinVideos&&weiXinVideos.size()>0){
                //降序排序
                Collections.sort(weiXinVideos, new Comparator<WeiXinVideo>() {
                    @Override
                    public int compare(WeiXinVideo o1, WeiXinVideo o2) {
                        return o2.getVideoCreazeTime().compareTo(o1.getVideoCreazeTime());
                    }
                });
            }
            return weiXinVideos;
        }


        @Override
        protected void onPostExecute(List<WeiXinVideo> weiXinVideos) {
            super.onPostExecute(weiXinVideos);
            if(null!=mScanVideoProgress&&mScanVideoProgress.isShowing()&&!MediaLocationVideoListActivity.this.isFinishing()){
                if(null!=weiXinVideos&&weiXinVideos.size()>0){
                    mScanVideoProgress.setFinlish(weiXinVideos.size());
                    //让子界面刷新数据
                    if(null!= mMediaVideoLocationFragment){
                        mMediaVideoLocationFragment.updataAdapter(weiXinVideos);
                    }
                }else{
                    mScanVideoProgress.dismiss();
                    showFinlishToast(null,null,"SD卡中未发现视频");
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(null!=mScanVideoProgress&&mScanVideoProgress.isShowing()&&!MediaLocationVideoListActivity.this.isFinishing()){
                mScanVideoProgress.setProgress(values[0]);
            }
        }
    }



    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //分段刷新
            if(10010==msg.what){
                closeProgressDialog();
                List<WeiXinVideo> weiXinVideos = (List<WeiXinVideo>) msg.obj;
                if(null!=weiXinVideos&&weiXinVideos.size()>0&&!MediaLocationVideoListActivity.this.isFinishing()){
                    if(null!=mMoivesListAdapter){
                        if(1==msg.arg1){
                            mMoivesListAdapter.setNewData(weiXinVideos);
                        }else{
                            mMoivesListAdapter.addData(weiXinVideos);
                        }
                    }
                }
            }else if(10011==msg.what){
                closeProgressDialog();
                List<WeiXinVideo> videoInfo = (List<WeiXinVideo>) msg.obj;
                if(null!=mMoivesListAdapter){
                    if(null!=videoInfo){
                        mMoivesListAdapter.setNewData(videoInfo);
                        mMoivesListAdapter.loadMoreEnd();
                    }
                }
            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onPause() {
        super.onPause();
        if(null!=mDialogScanWeixin){
            mDialogScanWeixin.setScanEvent(false);
        }
    }

    @Override
    public void initData() {

    }


    @Override
    public void onDestroy() {
        ImageCache.getInstance().recyler();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mMoivesListAdapter=null; mHandler=null;
    }
}
