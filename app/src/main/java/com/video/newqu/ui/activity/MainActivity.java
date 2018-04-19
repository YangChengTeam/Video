package com.video.newqu.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.tencent.bugly.Bugly;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sina.helper.MD5;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.adapter.XinQuFragmentPagerAdapter;
import com.video.newqu.base.TopBaseActivity;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.WeiChactVideoInfo;
import com.video.newqu.bean.WeiXinVideo;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.contants.NetContants;
import com.video.newqu.databinding.ActivityMainBinding;
import com.video.newqu.event.MessageEvent;
import com.video.newqu.manager.ActivityCollectorManager;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.manager.DBScanWeiCacheManager;
import com.video.newqu.manager.ThreadManager;
import com.video.newqu.ui.contract.MainContract;
import com.video.newqu.ui.dialog.ExitAppDialog;
import com.video.newqu.ui.dialog.FollowWeiXnDialog;
import com.video.newqu.ui.dialog.LocationVideoUploadDialog;
import com.video.newqu.ui.dialog.StoreGradeDialog;
import com.video.newqu.ui.dialog.TakePicturePopupWindow;
import com.video.newqu.ui.fragment.HomeFragment;
import com.video.newqu.ui.fragment.MineFragment;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.upload.manager.BatchFileUploadManager;
import com.video.newqu.util.DateParseUtil;
import com.video.newqu.util.KSYAuthorPermissionsUtil;
import com.video.newqu.util.ScanWeixin;
import com.video.newqu.util.ShareUtils;
import com.video.newqu.util.SharedPreferencesUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.util.VideoComposeProcessor;
import com.video.newqu.view.widget.HomeTabItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import cn.jpush.android.api.JPushInterface;

/**
 * TinyHung@outlook.com
 * 2017/5/20 10:20
 * 主页
 */

public class MainActivity extends TopBaseActivity implements MainContract.View, Observer {

    private List<Fragment> mFragments=null;
    private ActivityMainBinding bindingView;
    private boolean isLogin=false;//登录成功后是否显示我的界面
    private ScanWeixin mScanWeixin;
    private WeakReference<List<WeiXinVideo>> mListWeakReference=null;
    private WeakReference<BatchFileUploadManager> mUploadManagerWeakReference=null;
    private MainPresenter mMainPresenter;
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoApplication.mBuildChanleType = SystemUtils.getPublishChannel();//渠道ID
        SharedPreferencesUtil.getInstance().putBoolean(Constant.KEY_MAIN_INSTANCE,true);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        EventBus.getDefault().register(this);
        mMainPresenter = new MainPresenter(this);
        initWidgets();
        ApplicationManager.getInstance().addObserver(this);
        //刷新提示，每次安装了新版本都提示
        if(SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_START_DATE)!=Utils.getVersionCode()){
            if(bindingView.tvTipsMineMessage.getVisibility()!=View.VISIBLE){
                bindingView.tvTipsMineMessage.setVisibility(View.VISIBLE);
                bindingView.tvTipsMineMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bindingView.tvTipsMineMessage.setVisibility(View.GONE);
                        //第一次使用弹出使用提示
                        if(1!=SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_MAIN_CODE)){
                            bindingView.tvTipsMessage.setVisibility(View.VISIBLE);
                            bindingView.tvTipsMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                                }
                            });
                            SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_MAIN_CODE,1);
                        }
                    }
                });
                SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_START_DATE,Utils.getVersionCode());
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initWidgets() {
        if(null==mFragments) mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new MineFragment());
        bindingView.vpView.setAdapter(new XinQuFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        bindingView.llBottomMenu.setDoubleRefresh(true);//启用双击刷新
        bindingView.llBottomMenu.setOnTabChangeListene(new HomeTabItem.OnTabChangeListene() {
            //界面切换
            @Override
            public void onChangeed(int index) {
                //如果未登录，拦截
                if(1==index&&null==VideoApplication.getInstance().getUserData()){
                    isLogin=true;
                    login();
                    return;
                }
                bindingView.vpView.setCurrentItem(index);
            }

            //刷新
            @Override
            public void onRefresh(int index) {
                if(bindingView.tvTipsMineMessage.getVisibility()!=View.GONE) {
                    bindingView.tvTipsMineMessage.setVisibility(View.GONE);
                    if(1!=SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_MAIN_CODE)){
                        bindingView.tvTipsMessage.setVisibility(View.VISIBLE);
                        bindingView.tvTipsMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bindingView.tvTipsMessage.setVisibility(View.GONE);
                            }
                        });
                        SharedPreferencesUtil.getInstance().putInt(Constant.TIPS_MAIN_CODE,1);
                    }
                }
                updataChildView(index);
            }

            //拍摄
            @Override
            public void onTakePicture() {
                MobclickAgent.onEvent(MainActivity.this, "click_home_menu");
                if(null!=bindingView.tvTipsMessage&&bindingView.tvTipsMessage.getVisibility()==View.VISIBLE){
                    bindingView.tvTipsMessage.setVisibility(View.GONE);
                }
                //必须登录才能使用拍摄和编辑功能
                if(null==VideoApplication.getInstance().getUserData()){
                    login();
                    return;
                }
                TakePicturePopupWindow picturePopupWindow=new TakePicturePopupWindow(MainActivity.this);
                picturePopupWindow.setOnTakePictureListener(new TakePicturePopupWindow.OnTakePictureListener() {
                    @Override
                    public void onClick(int type) {
                        if(1==type){
                            MobclickAgent.onEvent(MainActivity.this, "start_record");
                            Intent intent = new Intent(MainActivity.this, MediaRecordActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.menu_enter, 0);//进场动画
                        }else if(2==type){
                            MobclickAgent.onEvent(MainActivity.this, "start_video_list");
                            MediaLocationVideoListActivity.start(MainActivity.this);
                        }

                    }
                });
                picturePopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM,0,0);
            }
        });
        Bugly.init(this, "2f71d3ad00", false);

        //不是第一次启动并且如果刚好是一个礼拜了
        if(1==SharedPreferencesUtil.getInstance().getInt(Constant.TIPS_MAIN_CODE)&&SharedPreferencesUtil.getInstance().getInt(Constant.SETTING_TODAY_WEEK_SUNDY)==DateParseUtil.getTodayWeekSundy()){
            //如果今天未扫描视频
            if(!SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_DAY)){
                File filePath = new File(NetContants.WEICHAT_VIDEO_PATH);
                //如果微信聊天文件夹存在
                if(filePath.exists()){
                    new ScanWeChatDirectoryTask().execute(filePath.getAbsolutePath());
                    //不存在微信文件夹，检查更新
                }else{
                    checkedUploadVideoEvent();//检查上传任务
                }
            }else{
                checkedUploadVideoEvent();//检查上传任务
            }
        }else{
            //如果不是刚好一个礼拜，还原扫描状态为未扫描
            SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_DAY,false);//标记为今天已扫描
            checkedUploadVideoEvent();//检查上传任务
        }
        try {
            if(!Utils.isCheckNetwork()) return;
            //为用户的设备绑定新的TAG，TAG为用户当前安装的程序版本号，绑定前，应该移除旧的版本号
            int oldVersionCode = SharedPreferencesUtil.getInstance().getInt(Constant.NOTIFACTION_BUILD_CODE,0);
            int newVersionCode = Utils.getVersionCode();
            if(0!=oldVersionCode&&0!=newVersionCode){
                Set<String> oldTags=new HashSet<>();
                oldTags.add(oldVersionCode+"");
                JPushInterface.deleteTags(this, (int)System.currentTimeMillis(),oldTags);//注销旧的版本号TAG标识
                Set<String> newTags=new HashSet<>();
                newTags.add(newVersionCode+"");
                JPushInterface.setTags(this, (int)System.currentTimeMillis(),newTags);//设置新的版本号TAG标识
                SharedPreferencesUtil.getInstance().putInt(Constant.NOTIFACTION_BUILD_CODE,newVersionCode);//覆盖最新的版本号
            }else{
                //防止重复注册
//                if(oldVersionCode==newVersionCode) return;
                Set<String> newTags=new HashSet<>();
                newTags.add(newVersionCode+"");
                JPushInterface.setTags(this, (int)System.currentTimeMillis(),newTags);//设置新的版本号TAG标识
                SharedPreferencesUtil.getInstance().putInt(Constant.NOTIFACTION_BUILD_CODE,newVersionCode);//覆盖最新的版本号
            }
        }catch (Exception e){
        }
        if(!SharedPreferencesUtil.getInstance().getBoolean(Constant.REGISTER_OPEN_APP)){
            //检查是否具备权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                }else{
                    //注册
                    if (null!=mMainPresenter&&!mMainPresenter.isResqust()) {
                        mMainPresenter.registerApp(false);
                    }
                }
            } else {
                //注册
                if (null!=mMainPresenter&&!mMainPresenter.isResqust()) {
                    mMainPresenter.registerApp(false);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We now have permission to use the location
                ToastUtils.showCenterToast("权限检查:GPS授权成功");
                if (null!=mMainPresenter&&!mMainPresenter.isResqust()) {
                    mMainPresenter.registerApp(false);
                }
            }
        }
    }

    /**
     * 拦截的刷新事件
     * @param poistion
     */
    private void updataChildView(int poistion) {
        if(null!=mFragments&&mFragments.size()>0){
            Fragment fragment = mFragments.get(poistion);
            if(null!=fragment){
                if(fragment instanceof HomeFragment){
                    ((HomeFragment) fragment).fromMainUpdata();
                }else if(fragment instanceof MineFragment){
                    ((MineFragment) fragment).fromMainUpdata();
                }
            }
        }
    }

    /**
     * 扫描本地视频
     */
    private class ScanWeChatDirectoryTask extends AsyncTask<String,Void,List<WeiXinVideo>>{

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
                            DBScanWeiCacheManager DBScanWeiCacheManager = new DBScanWeiCacheManager(MainActivity.this);
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
                try {
                    //当前运行的是自己
                    if(null!=SystemUtils.getTopActivity()){
                        if(!MainActivity.this.isFinishing()&&TextUtils.equals("com.video.newqu.ui.activity.MainActivity", SystemUtils.getTopActivity())){
                            LocationVideoUploadDialog locationVideoUploadDialog = new LocationVideoUploadDialog(MainActivity.this);
                            locationVideoUploadDialog.setData(weiXinVideos);
                            locationVideoUploadDialog.setOnDialogUploadListener(new LocationVideoUploadDialog.OnDialogUploadListener() {
                                @Override
                                public void onUploadVideo() {
                                    checkedUploadVideoEvent();
                                }
                            });
                            locationVideoUploadDialog.show();
                        }else{
                            mListWeakReference = new WeakReference<List<WeiXinVideo>>(weiXinVideos);
                        }
                    }else{
                        LocationVideoUploadDialog locationVideoUploadDialog = new LocationVideoUploadDialog(MainActivity.this);
                        locationVideoUploadDialog.setData(weiXinVideos);
                        locationVideoUploadDialog.setOnDialogUploadListener(new LocationVideoUploadDialog.OnDialogUploadListener() {
                            @Override
                            public void onUploadVideo() {
                                checkedUploadVideoEvent();
                            }
                        });
                        locationVideoUploadDialog.show();
                    }
                }catch (Exception e){
                    mListWeakReference = new WeakReference<List<WeiXinVideo>>(weiXinVideos);
                }
            }
        }
    }
    public void onResume() {
        super.onResume();
        if(JPushInterface.isPushStopped(MainActivity.this)){
            JPushInterface.resumePush(MainActivity.this);
        }
        if(null==VideoApplication.getInstance().getUserData()){
            setCureenIndex(0);
        }
        //微信发现时回聘了且未弹过窗
        if (null!=mListWeakReference&&null!=mListWeakReference.get()&&mListWeakReference.get().size()>0&&!MainActivity.this.isFinishing()) {
            LocationVideoUploadDialog locationVideoUploadDialog = new LocationVideoUploadDialog(MainActivity.this);
            locationVideoUploadDialog.setData(mListWeakReference.get());
            locationVideoUploadDialog.setOnDialogUploadListener(new LocationVideoUploadDialog.OnDialogUploadListener() {
                @Override
                public void onUploadVideo() {
                    if(null!=mListWeakReference.get()){
                        mListWeakReference.get().clear();
                        mListWeakReference.clear();
                    }
                    checkedUploadVideoEvent();
                }
            });
            locationVideoUploadDialog.show();
        //微信关注,用户未关注过、当天未弹窗、播放视频数量达到标准，弹出关注微信弹窗
        }else if(!SharedPreferencesUtil.getInstance().getBoolean(Constant.FOLLOW_WEIXIN)&&SharedPreferencesUtil.getInstance().getInt(Constant.MAIN_FOLLOW_WEIXIN_TODAY, 0)!=VideoApplication.mToday&&SharedPreferencesUtil.getInstance().getInt(Constant.GRADE_PLAYER_VIDEO_COUNT)>=3){
            SharedPreferencesUtil.getInstance().putInt(Constant.MAIN_FOLLOW_WEIXIN_TODAY, VideoApplication.mToday);//今天已经提示过了
            FollowWeiXnDialog followWeiXnDialog=new FollowWeiXnDialog(MainActivity.this);
            followWeiXnDialog.setOnItemClickListener(new FollowWeiXnDialog.OnItemClickListener() {
                @Override
                public void onFollow() {
                    MobclickAgent.onEvent(MainActivity.this, "click_follow_wechat");
//                    SharedPreferencesUtil.getInstance().putBoolean(Constant.FOLLOW_WEIXIN,true);
//                    Intent intent= new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri contentUrl = Uri.parse("http://jump.hupeh.cn/xqsp1223.php");
//                    intent.setData(contentUrl);
//                    startActivity(intent);
                    Utils.copyString("新趣小视频助手");
                    ToastUtils.showCenterToast("已复制微信号");
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("新趣小视频助手")
                            .setMessage(getResources().getString(R.string.open_weixin_tips));
                    builder.setNegativeButton("算了", null);
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            try {
                                Uri uri = Uri.parse("weixin://");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } catch (Exception e) {
                                //若无法正常跳转，在此进行错误处理
                                ToastUtils.showCenterToast("无法跳转到微信，请检查设备是否安装了微信！");
                            }
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            });
            followWeiXnDialog.show();
        //最后去初始化评分
        }else{
            showGradDialog();
        }
    }

    private void showGradDialog() {
        //如果是新版本&&用户没有拒绝过&&非第一次打开程序
        if(SharedPreferencesUtil.getInstance().getInt(Constant.GRADE_VERSTION_CODE)!=Utils.getVersionCode()
                &&!SharedPreferencesUtil.getInstance().getBoolean(Constant.GRADE_CATION,false)
                &&SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_FIRST_START_GRADE, false)){
            StoreGradeDialog storeGradeDialog=new StoreGradeDialog(MainActivity.this);
            storeGradeDialog.setOnItemClickListener(new StoreGradeDialog.OnItemClickListener() {

                @Override
                public void onCancel() {
                    //今后永久不再提示
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.GRADE_CATION,true);
                }

                @Override
                public void onService() {
                    MobclickAgent.onEvent(MainActivity.this, "start_servicer_addmsg");
                    Intent intent=new Intent(MainActivity.this, ContentFragmentActivity.class);
                    intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.KEY_FRAGMENT_SERVICES);
                    intent.putExtra(Constant.KEY_TITLE,"反馈中心");
                    startActivity(intent);
                }

                @Override
                public void onGood() {
                    MobclickAgent.onEvent(MainActivity.this, "start_market_score");
                    try {
                        Uri uri = Uri.parse("market://details?id="+ Utils.getAppProcessName(MainActivity.this));
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }catch (Exception e){

                    }
                }
            });
            storeGradeDialog.show();
            SharedPreferencesUtil.getInstance().putInt(Constant.GRADE_VERSTION_CODE,Utils.getVersionCode());//这个版本不会再提示了
        }
    }

    /**
     * 显示某个界面
     * @param index
     */
    private void setCureenIndex(int index) {
        if(null!=bindingView){
            if(null!=mFragments&&mFragments.size()>0){
                if(index!=bindingView.vpView.getCurrentItem()){
                    bindingView.vpView.setCurrentItem(index);
                }
                bindingView.llBottomMenu.setCurrentIndex(index);
            }
        }
    }


    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    /**
     * 结束APP
     */
    private void destoryApp() {
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        ActivityCollectorManager.finlishAllActivity();
        finish();
    }

    /**
     * 设置首页 "我的" 消息数量
     *
     * @param count
     */
    public void setMessageCount(int count) {
        if (count<=0) {
            bindingView.llBottomMenu.setMessageVisibility(false);
            bindingView.llBottomMenu.setMessageContent(count + "");
        } else {
            bindingView.llBottomMenu.setMessageVisibility(true);
            bindingView.llBottomMenu.setMessageContent(count + "");
        }
    }

    /**
     * 刷新首页的新消息数量
     * @param count
     */
    public void showNewMessageDot(int count) {
        if(null!=mFragments&&mFragments.size()>0){
            Fragment fragment = mFragments.get(0);
            if(null!=fragment&&fragment instanceof HomeFragment){
                ((HomeFragment) fragment).showNewMessageDot(count);
            }
        }
    }

    /**
     * 拦截返回和菜单事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        ExitAppDialog exitAppDialog=new ExitAppDialog(MainActivity.this);
        exitAppDialog.setOnDialogClickListener(new ExitAppDialog.OnDialogClickListener() {
            @Override
            public void onExitApp() {
                destoryApp();
            }
        });
        exitAppDialog.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止视频扫描，如果在扫描的话
        if(null!=mScanWeixin){
            mScanWeixin.setScanEvent(false);//停止扫描
        }
        if (null!=mUploadManagerWeakReference&&null != mUploadManagerWeakReference.get()){
            mUploadManagerWeakReference.get().pause();
            mUploadManagerWeakReference.clear();
        }
        if(null!=mMainPresenter){
            mMainPresenter.detachView();
        }
        if(null!=bindingView) bindingView.llBottomMenu.onDestroy();
        SharedPreferencesUtil.getInstance().putBoolean(Constant.KEY_MAIN_INSTANCE,false);
        EventBus.getDefault().unregister(this);
        JPushInterface.stopPush(getApplicationContext());
        VideoComposeProcessor.getInstance().stopAllCompose();
        ApplicationManager.getInstance().removeAllObserver();
        ApplicationManager.getInstance().onDestory();
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_FIRST_START_GRADE, true);//已经启动过App了
        Runtime.getRuntime().gc();
    }

    /**
     * 提供给子界面的登录方法
     */
    public void login() {
        Intent intent = new Intent(MainActivity.this, LoginGroupActivity.class);
        startActivityForResult(intent, Constant.INTENT_LOGIN_EQUESTCODE);
        overridePendingTransition( R.anim.menu_enter,0);//进场动画
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
        //这个时候DialogFragment无法回调onActivityResult方法.由父窗体来回调结果到子View中,发出订阅的事件，如果用户信息编辑的界面已经初始化可以收到订阅消息
        MessageEvent messageEvent=new MessageEvent();
        messageEvent.setData(data);
        messageEvent.setMessage("CAMERA_REQUEST");
        messageEvent.setRequestCode(requestCode);
        messageEvent.setResultState(resultCode);
        EventBus.getDefault().post(messageEvent);
        //登录意图，需进一步确认
        if (Constant.INTENT_LOGIN_EQUESTCODE == requestCode && resultCode == Constant.INTENT_LOGIN_RESULTCODE) {
            if (null != data) {
                if(isLogin){
                    setCureenIndex(1);
                }
                //登录成功,判断用户有没有绑定手机号码
                if (null!=VideoApplication.getInstance().getUserData()&&!VideoApplication.getInstance().userIsBinDingPhone()) {
                    binDingPhoneNumber();
                }
            }
        }else if(requestCode==0xa1){
            if(null!=mFragments&&mFragments.size()>0){
                Fragment fragment = mFragments.get(1);
                if(null!=fragment&&fragment instanceof MineFragment){
                    ((MineFragment)fragment).clippingPictures();
                }
            }
        //用户信息不补全界面
        }else if(requestCode==0xa2){
            EventBus.getDefault().post(new MessageEvent("clip_pic"));
        //申请位置权限处理
        }
        isLogin=false;//不管取消登录还是登录成功，重置
    }

    /**
     * 分享
     * @param shareInfo
     * @param poistion
     */
    public void shareIntent(ShareInfo shareInfo, int poistion) {
        if(null== shareInfo)return;
        if(TextUtils.isEmpty(shareInfo.getVideoID())) return;
        String url = "http://app.nq6.com/home/show/index?id=" + shareInfo.getVideoID();
        String token = MD5.hexdigest(url + "xinqu_123456");
        shareInfo.setUrl(url+"&token=" + token);//+"&share_type="+"1"
        share(shareInfo,poistion);
    }

    /**
     * 分享
     */
    protected void share(ShareInfo shareInfo,int pistion) {
        if(null==shareInfo) return;
        switch (pistion) {
            case 0:
                ShareUtils.share(MainActivity.this,shareInfo, SHARE_MEDIA.WEIXIN,this);
                break;
            case 1:
                ShareUtils.share(MainActivity.this,shareInfo,SHARE_MEDIA.QQ,this);
                break;
            case 2:
                ShareUtils.share(MainActivity.this,shareInfo,SHARE_MEDIA.SINA,this);
                break;
            case 3:
                ShareUtils.share(MainActivity.this,shareInfo,SHARE_MEDIA.WEIXIN_CIRCLE,this);
                break;
            case 4:
                ShareUtils.share(MainActivity.this,shareInfo,SHARE_MEDIA.QZONE,this);
                break;
            case 5:
                shareOther(shareInfo);
                break;
            default:
                shareOther(shareInfo);
        }
    }

    /**
     * 刷新通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (null != event) {
            //收到了动态消息
            if (TextUtils.equals(Constant.EVENT_NEW_MESSAGE, event.getMessage())) {
                int extar = event.getExtar();
                setMessageCount(extar);
                EventBus.getDefault().post(new MessageEvent(Constant.EVENT_UPDATA_MESSAGE_UI));
             //网络发生了变化
            } else if (TextUtils.equals("event_home_updload_weicacht", event.getMessage())) {
                if(null!=mFragments&&mFragments.size()>0){
                    Fragment fragment = mFragments.get(0);
                    if(null!=fragment&&fragment instanceof HomeFragment){
                        ((HomeFragment) fragment).changeUploadVideoState();
                    }
                }
                checkedUploadVideoEvent();
                //金山云鉴权
                if(!AuthInfoManager.getInstance().getAuthState()){
                    ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //在这里与金山云通信获得授权
                            KSYAuthorPermissionsUtil.init();
                        }
                    });
                }
            }
        }
    }

    /**
     * 检查本地上传列表中是否未完成的上传任务,微信的
     */
    private void checkedUploadVideoEvent() {
        List<WeiChactVideoInfo> uploadVideoList = ApplicationManager.getInstance().getWeiXinVideoUploadDB().getUploadVideoList();
        if (null != uploadVideoList && uploadVideoList.size() > 0) {
            //WIFI网络自动下载
            if (1 == Utils.getNetworkType()) {
                if(null!=mUploadManagerWeakReference&&null!=mUploadManagerWeakReference.get()){
                    mUploadManagerWeakReference.get().upload(uploadVideoList);
                }else{
                    BatchFileUploadManager.Builder builder = new BatchFileUploadManager.Builder();
                    mUploadManagerWeakReference = new WeakReference<BatchFileUploadManager>(builder.build());
                    mUploadManagerWeakReference.get().upload(uploadVideoList);
                }
            } else {
                if(ConfigSet.getInstance().isMobileUpload()){
                    if(null!=mUploadManagerWeakReference&&null!=mUploadManagerWeakReference.get()){
                        mUploadManagerWeakReference.get().upload(uploadVideoList);
                    }else{
                        BatchFileUploadManager.Builder builder = new BatchFileUploadManager.Builder();
                        mUploadManagerWeakReference = new WeakReference<BatchFileUploadManager>(builder.build());
                        mUploadManagerWeakReference.get().upload(uploadVideoList);
                    }
                }else{
                    if (null!=mUploadManagerWeakReference&&null != mUploadManagerWeakReference.get()) mUploadManagerWeakReference.get().puseAllUploadTask();
                }
            }
        }
    }

    /**
     * 切换首页第几个Fragment的第几个childFragment
     * @param groupIndex 父Fragment(MainActivity的直接子Fragment)索引
     * @param childIndex 子Fragment(MainActivity的直接子Fragment的嵌套的子Fragment)索引
     */
    public void currentHomeFragmentChildItemView(int groupIndex,int childIndex) {
        if(null==mFragments||mFragments.size()<=0) return;
        if(groupIndex<0||groupIndex>=mFragments.size()) return;

        if(null!=mFragments&&mFragments.size()>0&&mFragments.size()>0){

            if(bindingView.vpView.getCurrentItem()!=groupIndex){
                bindingView.vpView.setCurrentItem(groupIndex);
            }
            bindingView.llBottomMenu.setCurrentIndex(groupIndex);
            Fragment fragment = mFragments.get(groupIndex);
            if(null!=fragment){
                if(fragment instanceof HomeFragment){
                    ((HomeFragment) fragment).currentChildView(childIndex);
                }else if(fragment instanceof MineFragment){
                    ((MineFragment) fragment).currentChildView(childIndex);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            Integer action= (Integer) arg;
            switch (action) {
                //登录
                case Constant.OBSERVABLE_ACTION_LOGIN:
                    break;
                //登出
                case Constant.OBSERVABLE_ACTION_UNLOGIN:
                    setMessageCount(0);
                    setCureenIndex(0);
                    break;
                //用户添加的视频任务
                case Constant.OBSERVABLE_ACTION_ADD_VIDEO_TASK:
                    setCureenIndex(0);
                    break;
            }
        }
    }
}