package com.video.newqu.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.base.BaseLightWeightFragment;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.UpdataApkInfo;
import com.video.newqu.bean.UserData;
import com.video.newqu.manager.ApplicationManager;
import com.video.newqu.contants.ConfigSet;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.FragmentSettingBinding;
import com.video.newqu.helper.TagAliasOperatorHelper;
import com.video.newqu.listener.OnUpdataStateListener;
import com.video.newqu.manager.APKUpdataManager;
import com.video.newqu.service.DownLoadService;
import com.video.newqu.ui.activity.AppAboutActivity;
import com.video.newqu.ui.activity.PhoneChangedActivity;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.dialog.BuildManagerDialog;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.util.FileSizeUtil;
import com.video.newqu.util.SystemUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import java.io.File;
import java.util.Map;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2017/10/12.
 * 设置中心
 */

public class SettingFragment extends BaseLightWeightFragment<FragmentSettingBinding,MainPresenter> {

    private double mFileOrFilesSize;
    private ContentFragmentActivity mFragmentActivity;
    private String mTitle;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentActivity = (ContentFragmentActivity) context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkedCacheSize();
    }

    @Override
    protected void initViews() {
        //是否显示联系客服按钮
        bindingView.llMessage.setVisibility(null== VideoApplication.getInstance().getUserData()?View.GONE:View.VISIBLE);
        //是否显示注销按钮
        bindingView.reUnlogin.setVisibility(null== VideoApplication.getInstance().getUserData()?View.GONE:View.VISIBLE);
        bindingView.bottomView.setVisibility(null== VideoApplication.getInstance().getUserData()?View.VISIBLE:View.GONE);
        if(null!=VideoApplication.getInstance().getUserData()){
            bindingView.tvPhoneNumber.setText(Utils.getBindPhoneNumber(VideoApplication.getInstance().getUserData().getPhone()));
            mTitle = "更换";
            if(TextUtils.isEmpty(VideoApplication.getInstance().getUserData().getPhone())){
                mTitle ="绑定";
            }
            bindingView.tvChangedPhone.setText(mTitle);
        }
        //先测量目标组件的宽高
        int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        bindingView.reWatermark.measure(width,width);
        int viewHeight=bindingView.reWatermark.getMeasuredHeight();
        //播放器样式选择条目高度
        LinearLayout.LayoutParams PlayerModelLayoutParams = (LinearLayout.LayoutParams) bindingView.rePlayerModel.getLayoutParams();
        PlayerModelLayoutParams.height=viewHeight;bindingView.rePlayerModel.setLayoutParams(PlayerModelLayoutParams);
        //自动播放设置条目高度
        LinearLayout.LayoutParams authoLayoutParams = (LinearLayout.LayoutParams) bindingView.rePlayerAuth.getLayoutParams();
        authoLayoutParams.height=viewHeight; bindingView.rePlayerAuth.setLayoutParams(authoLayoutParams);
        //自动循环播放条目高度
        LinearLayout.LayoutParams PlayerLoopLayoutParams = (LinearLayout.LayoutParams) bindingView.rePlayerLoop.getLayoutParams();
        PlayerLoopLayoutParams.height=viewHeight; bindingView.rePlayerLoop.setLayoutParams(PlayerLoopLayoutParams);
        bindingView.tvVerstionCode.setText("当前版本 "+ Utils.getVersion());
        //回显状态
        bindingView.swMobileUpload.setChecked(ConfigSet.getInstance().isMobileUpload());
        bindingView.swMobilePlayer.setChecked(ConfigSet.getInstance().isMobilePlayer());
        bindingView.swPlayerAuth.setChecked(ConfigSet.getInstance().isWifiAuthPlayer());
        bindingView.swPlayerLoop.setChecked(ConfigSet.getInstance().isPalyerLoop());
        bindingView.swWatermark.setChecked(ConfigSet.getInstance().isAddWatermark());
        bindingView.swSaveVideo.setChecked(ConfigSet.getInstance().isSaveVideo());
        //播放器窗口样式
        int which=0;
        which=ConfigSet.getInstance().isPlayerModel()?0:1;
        bindingView.tvPlayerModel.setText(getResources().getStringArray(R.array.setting_dialog_video_player_style_choice)[which]);

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switch (compoundButton.getId()) {
                    case R.id.sw_mobile_upload:
                        ConfigSet.getInstance().setMobileUpload(isChecked);
                        break;
                    case R.id.sw_mobile_player:
                        ConfigSet.getInstance().setMobilePlayer(isChecked);
                        break;
                    case R.id.sw_player_auth:
                        ConfigSet.getInstance().setWifiAuthPlayer(isChecked);
                        break;
                    case R.id.sw_player_loop:
                        ConfigSet.getInstance().setPalyerLoop(isChecked);
                        break;
                    case R.id.sw_watermark:
                        ConfigSet.getInstance().setAddWatermark(isChecked);
                        break;
                    case R.id.sw_save_video:
                        ConfigSet.getInstance().setSaveVideo(isChecked);
                        break;
                }
            }
        };
        bindingView.swMobileUpload.setOnCheckedChangeListener(onCheckedChangeListener);
        bindingView.swMobilePlayer.setOnCheckedChangeListener(onCheckedChangeListener);
        bindingView.swPlayerAuth.setOnCheckedChangeListener(onCheckedChangeListener);
        bindingView.swPlayerLoop.setOnCheckedChangeListener(onCheckedChangeListener);
        bindingView.swWatermark.setOnCheckedChangeListener(onCheckedChangeListener);
        bindingView.swSaveVideo.setOnCheckedChangeListener(onCheckedChangeListener);

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.re_mobile_upload:
                        bindingView.swMobileUpload.setChecked(!bindingView.swMobileUpload.isChecked());
                        break;
                    case R.id.re_mobile_player:
                        bindingView.swMobilePlayer.setChecked(!bindingView.swMobilePlayer.isChecked());
                        break;
                    case R.id.re_player_auth:
                        bindingView.swPlayerAuth.setChecked(!bindingView.swPlayerAuth.isChecked());
                        break;
                    case R.id.re_player_loop:
                        bindingView.swPlayerLoop.setChecked(!bindingView.swPlayerLoop.isChecked());
                        break;
                    case R.id.re_watermark:
                        bindingView.swWatermark.setChecked(!bindingView.swWatermark.isChecked());
                        break;
                    case R.id.re_save_video:
                        bindingView.swSaveVideo.setChecked(!bindingView.swSaveVideo.isChecked());
                        break;
                    //播放器窗口样式
                    case R.id.re_player_model:
                        showVideoPlayerStyleChose();
                        break;
                    //检查更新
                    case R.id.re_checked_upload:
                        checkedUpRefreshAPK();
                        break;
                    //清除缓存
                    case R.id.re_clean_cache:
                        emptyCache();
                        break;
                    //分享
                    case R.id.re_share:
                        if(null!=mFragmentActivity&&!mFragmentActivity.isFinishing()){
                            ShareInfo shareInfo=new ShareInfo();
                            shareInfo.setDesp("短视频笑不停，年轻人都在看! 地球人都关注的短视频神器!");
                            shareInfo.setTitle("新趣小视频");
                            shareInfo.setUrl("http://v.nq6.com");
                            shareInfo.setVideoID("");
                            mFragmentActivity.onShare(shareInfo);
                        }
                        break;
                    //打分
                    case R.id.re_grade:
                        try {
                            Uri uri = Uri.parse("market://details?id="+ Utils.getAppProcessName(getActivity()));
                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }catch (Exception e){
                            Log.d("SettingsFragment",e.toString());
                        }
                        break;

                    //意见反馈
                    case R.id.re_message:
                        if(null!=mFragmentActivity&&!mFragmentActivity.isFinishing()){
                            mFragmentActivity.addReplaceFragment(new ServiceMessageFragment(),"反馈留言");
                        }
                        break;
                    //关于我们
                    case R.id.re_above:
                        AppAboutActivity.start(getActivity());
                        break;
                    //注销登录
                    case R.id.tv_unlogin:
                        loginOut();
                        break;
                    //更换手机号码
                    case R.id.tv_changed_phone:
                        Intent intent = new Intent(getActivity(), PhoneChangedActivity.class);
                        intent.putExtra(Constant.KEY_TITLE,mTitle+"手机号码");
                        if(TextUtils.equals("绑定",mTitle)&&TextUtils.isEmpty(VideoApplication.getInstance().getUserData().getPhone())){
                            intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_PHONE_BINDING);
                        }else{
                            intent.putExtra(Constant.KEY_FRAGMENT_TYPE,Constant.FRAGMENT_TYPE_PHONE_CHECKED);
                            intent.putExtra(Constant.KEY_PHONE,VideoApplication.getInstance().getUserData().getPhone());
                        }
                        intent.putExtra(Constant.KEY_CONTENT_EXTRA,"");
                        startActivityForResult(intent,Constant.MEDIA_BINDING_PHONE_REQUEST);
                        getActivity().overridePendingTransition(R.anim.menu_enter, 0);//进场动画
                        break;
                    //隐私设置
//                    case R.id.re_private_set:
//                        if(null!=mFragmentActivity&&!mFragmentActivity.isFinishing()){
//                            mFragmentActivity.addReplaceFragment(new PrivateSettingFragment(),"隐私设置");
//                        }
//                        break;
                }
            }
        };
        bindingView.reMobileUpload.setOnClickListener(onClickListener);
        bindingView.reMobilePlayer.setOnClickListener(onClickListener);
        bindingView.rePlayerAuth.setOnClickListener(onClickListener);
        bindingView.rePlayerLoop.setOnClickListener(onClickListener);
        bindingView.reWatermark.setOnClickListener(onClickListener);
        bindingView.reSaveVideo.setOnClickListener(onClickListener);
        bindingView.rePlayerModel.setOnClickListener(onClickListener);
        bindingView.reCheckedUpload.setOnClickListener(onClickListener);
        bindingView.reCleanCache.setOnClickListener(onClickListener);
        bindingView.reShare.setOnClickListener(onClickListener);
        bindingView.reGrade.setOnClickListener(onClickListener);
        bindingView.reMessage.setOnClickListener(onClickListener);
        bindingView.reAbove.setOnClickListener(onClickListener);
        bindingView.tvUnlogin.setOnClickListener(onClickListener);
        bindingView.tvChangedPhone.setOnClickListener(onClickListener);
        bindingView.rePrivateSet.setOnClickListener(onClickListener);
    }

    /**
     * 弹出播放器样式选择模式
     */
    private void showVideoPlayerStyleChose() {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setTitle("视频播放器窗口样式选择")
                .setSingleChoiceItems(getResources().getStringArray(R.array.setting_dialog_video_player_style_choice),
                        ConfigSet.getInstance().isPlayerModel()? 0 : 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConfigSet.getInstance().setPlayerModel(0==which?true:false);
                                bindingView.tvPlayerModel.setText(getResources().getStringArray(R.array.setting_dialog_video_player_style_choice)[which]);
                                dialog.dismiss();
                            }
                        })
                .create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        alertDialog.show();
    }

    /**
     *检查缓存大小
     */
    private void checkedCacheSize() {
        //检查SD读写权限
        RxPermissions.getInstance(getActivity()).request(Manifest.permission.READ_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mFileOrFilesSize = FileSizeUtil.getFileOrFilesSize(ApplicationManager.getInstance().getVideoCacheDir(), 3);
                            mHandler.sendEmptyMessage(101);
                        }
                    }).start();
                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle("权限申请失败")
                            .setMessage("权限被拒绝！请授予应用读写SD卡权限！是否现在去设置？");
                    builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(getActivity(),123);
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //删除用户所有资料完成
            if(100==msg.what){
                unLoginFinlish();
                //检查缓存完成
            } else if(101==msg.what){
                bindingView.tvCacheCount.setText(mFileOrFilesSize+"M");
                //清理缓存完成
            }else if(102==msg.what){
                showFinlishToast(null,null,"缓存清理完成");
                checkedCacheSize();
            }
        }
    };

    /**
     * 清空缓存
     */
    private void emptyCache() {
        //检查SD读写权限
        RxPermissions.getInstance(getActivity()).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(null!=aBoolean&&aBoolean){
                    if(mFileOrFilesSize>0){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.deleteAllFiles(new File(ApplicationManager.getInstance().getVideoCacheDir()));
                                mHandler.sendEmptyMessage(102);
                            }
                        }).start();
                    }else{
                        showErrorToast(null,null,"没有缓存可清理");
                    }
                }else{
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle("权限申请失败")
                            .setMessage("权限被拒绝！请授予应用读写SD卡权限！是否现在去设置？");
                    builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SystemUtils.getInstance().startAppDetailsInfoActivity(getActivity(),123);
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    /**
     * 检查更新
     */
    private void checkedUpRefreshAPK() {
        showProgressDialog("检查新版本中,请稍后..",true);
        new APKUpdataManager(getActivity()).checkedBuild(new OnUpdataStateListener() {
            @Override
            public void onNeedUpdata( UpdataApkInfo updataApkInfo) {
                closeProgressDialog();
                final UpdataApkInfo.DataBean dataBean = updataApkInfo.getData();
                if(null!=dataBean){
                    BuildManagerDialog buildManagerDialog =new BuildManagerDialog(getActivity(), R.style.UpdataDialogAnimation);
                    buildManagerDialog.setUpdataData(dataBean);
                    buildManagerDialog.setOnUpdataListener(new BuildManagerDialog.OnUpdataListener() {
                        @Override
                        public void onUpdata() {
                            //检查SD卡权限
                            RxPermissions.getInstance(getActivity()).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if(null!=aBoolean&&aBoolean){
                                        Intent service = new Intent(getActivity(), DownLoadService.class);
                                        service.putExtra("downloadurl", dataBean.getDownload());
                                        if(1==Utils.getNetworkType()){
                                            ToastUtils.showCenterToast("正在下载中");
                                        }else{
                                            ToastUtils.showCenterToast("下载任务将在连接WIFI后自动开始,请不要关闭本软件");
                                        }
                                        getActivity().startService(service);
                                    }else{
                                        ToastUtils.showCenterToast("下载失败！SD卡下载权限被拒绝");
                                    }
                                }
                            });
                        }
                    });
                    buildManagerDialog.show();
                }
            }

            @Override
            public void onNotUpdata(String data) {
                closeProgressDialog();
                ToastUtils.showCenterToast(data);
            }

            @Override
            public void onUpdataError(String data) {
                closeProgressDialog();
                ToastUtils.showCenterToast(data);
            }
        });
    }


    /**
     * 账号登出
     */
    private void loginOut() {

        UserData.DataBean.InfoBean userData = VideoApplication.getInstance().getUserData();
        if(null==userData){
            return;
        }
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
            return;
        }

        showProgressDialog("正在注销登录...",true);


        TagAliasOperatorHelper.getInstance().setOnAliasChangeListener(new TagAliasOperatorHelper.OnAliasChangeListener() {
            @Override
            public void onChange(JPushMessage jPushMessage) {
            }
        });
        //先注销极光推送
        JPushInterface.deleteAlias(getActivity(), (int) System.currentTimeMillis());
        JPushInterface.setAlias(getActivity(), (int) System.currentTimeMillis(),"");
        int loginType = Integer.parseInt(userData.getLogin_type());//当前登录的类型

        SHARE_MEDIA platformInfo=null;
        switch (loginType){
            //QQ
            case 1:

                platformInfo= SHARE_MEDIA.QQ;
                break;
            //微信
            case 2:

                platformInfo= SHARE_MEDIA.WEIXIN;
                break;
            //微博用户
            case 3:

                platformInfo= SHARE_MEDIA.SINA;
                break;
            //手机号用户
            case 4:

                platformInfo=null;
                break;
        }
        if(null!=platformInfo){
            boolean isauth = UMShareAPI.get(getActivity()).isAuthorize(getActivity(), platformInfo);//判断当前APP有没有授权登录
            if(isauth){
                UMShareAPI.get(getActivity()).deleteOauth(getActivity(), platformInfo, deleteAuthListener);//删除登录授权
            }else{
                deleteUserData();
            }
        }else{
            deleteUserData();
        }
    }

    /**
     * 删除所有用户信息和缓存
     */
    private void deleteUserData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                VideoApplication.getInstance().setUserData(null,true);
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_USER_DATA);//我的界面用户资料
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_FOOLOW_VIDEO_LIST);//关注列表
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_WORKS);//我的作品
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FOLLOW_VIDEO_LIST);//我的收藏
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FANS_LIST);//我的粉丝
                ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_MINE_FOLLOW_USER_LIST);//我的关注人列表
                mHandler.sendEmptyMessage(100);
            }
        }.start();
    }



    /**
     * 注销完成
     */
    private void unLoginFinlish() {
        closeProgressDialog();
        if(null!=bindingView) bindingView.llUserPhoneView.setVisibility(View.GONE);
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVABLE_ACTION_UNLOGIN);
        if(null!=mFragmentActivity&&!mFragmentActivity.isFinishing()){
            mFragmentActivity.finish();
        }
    }

    /**
     * 删除回调
     */
    private UMAuthListener deleteAuthListener=new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

            deleteUserData();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            ToastUtils.showCenterToast("注销失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constant.MEDIA_BINDING_PHONE_REQUEST && resultCode == Constant.MEDIA_BINDING_PHONE_RESULT){
            if(null!=data){
                mTitle="更换";
                bindingView.tvPhoneNumber.setText(Utils.getBindPhoneNumber(data.getStringExtra("phone")));
            }
        }
    }
}
