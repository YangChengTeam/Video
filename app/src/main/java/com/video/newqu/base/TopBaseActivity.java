package com.video.newqu.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sina.helper.MD5;
import com.video.newqu.R;
import com.video.newqu.VideoApplication;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.ShareMenuItemInfo;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.OnShareFinlishListener;
import com.video.newqu.ui.activity.MediaEditActivity;
import com.video.newqu.ui.activity.PhoneChangedActivity;
import com.video.newqu.ui.dialog.ShareDialog;
import com.video.newqu.listener.ShareFinlishListener;
import com.video.newqu.listener.SnackBarListener;
import com.video.newqu.ui.contract.ShareContract;
import com.video.newqu.ui.presenter.SharePresenter;
import com.video.newqu.util.ShareUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TinyHung@outlook.com
 * 2017-06-27 22:29
 * 顶部的通用BaseActivity,主要统一分享操作
 */

public class TopBaseActivity extends AppCompatActivity implements ShareContract.View ,OnShareFinlishListener {

    private ShareInfo mShareInfo;
    private ShareFinlishListener shareFinlishListener;//分享的监听，用于通知创建分享的对象
    private SharePresenter mSharePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharePresenter = new SharePresenter(this);
        mSharePresenter.attachView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Utils.isCheckNetwork()){
            showNetWorkTips();
        }
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        if(null!=mSharePresenter){
            mSharePresenter.detachView();
            mSharePresenter=null;
        }
        shareFinlishListener=null;
        mShareInfo=null;
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    /**
     * 分享用户主页
     */
    public void shareMineHome(ShareInfo shareInfo){
        if(null==shareInfo){
            ToastUtils.showCenterToast("缺少分享参数");
            return;
        }
        this.mShareInfo = shareInfo;
        shareIntent();
    }

    /**
     * 分享视频
     * 多参
     * @param shareInfo
     * @param shareFinlishListener
     */
    public void onShare(final ShareInfo shareInfo, ShareFinlishListener shareFinlishListener) {
        if(null==shareInfo){
            ToastUtils.showCenterToast("缺少分享参数");
            return;
        }
        this.mShareInfo = shareInfo;
        this.shareFinlishListener=shareFinlishListener;
        if(!TextUtils.isEmpty(mShareInfo.getVideoID())){
            String url = "http://app.nq6.com/home/show/index?id=" + mShareInfo.getVideoID();
            String token = MD5.hexdigest(url + "xinqu_123456");
            mShareInfo.setUrl(url+"&token=" + token);//+"&share_type="+"1"
        }
        shareIntent();
    }

    /**
     * 分享视频
     * @param shareInfo
     */
    public void onShare(final ShareInfo shareInfo) {
        if(null==shareInfo){
            showErrorToast(null,null,"缺少分享参数");
            return;
        }
        this.mShareInfo = shareInfo;
        if(!TextUtils.isEmpty(mShareInfo.getVideoID())){
            String url = "http://app.nq6.com/home/show/index?id=" + mShareInfo.getVideoID();
            String token = MD5.hexdigest(url + "xinqu_123456");
            mShareInfo.setUrl(url+"&token=" + token);//+"&share_type="+"1"
        }
        shareIntent();
    }

    /**
     * 分享
     */
    private void shareIntent() {
        if(null== mShareInfo){
            return;
        }
        ShareDialog shareDialog = new ShareDialog(this,mShareInfo.getShareTitle());
        shareDialog.setOnItemClickListener(new ShareDialog.OnShareItemClickListener() {
            @Override
            public void onItemClick(final ShareMenuItemInfo shareMenuItemInfo) {
                if(null!=shareMenuItemInfo){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=shareMenuItemInfo.getPlatform()){
                                if(SHARE_MEDIA.MORE==shareMenuItemInfo.getPlatform()){
                                    shareOther(mShareInfo);
                                    return;
                                }
                                ShareUtils.baseShare(TopBaseActivity.this,mShareInfo,shareMenuItemInfo.getPlatform(),TopBaseActivity.this);
                            }else{
                                Utils.copyString(mShareInfo.getUrl());
                                showFinlishToast(null,null,"已复制到粘贴板");
                            }
                        }
                    }, Constant.CLOSE_POPUPWINDOW_WAIT_TIME);
                }
            }
        });
        shareDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onShareDialogDismiss();
            }
        });
        shareDialog.show();
    }

    protected void onShareDialogDismiss(){
    }

    /**
     * 分享到其他
     * @param shareInfo
     */
    public void shareOther(ShareInfo shareInfo) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,shareInfo.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, shareInfo.getUrl());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.shared_to)));
    }

    /**
     * 统计分享次数
     * @param share_media
     */
    private void countShare(SHARE_MEDIA share_media) {
        if(!this.isFinishing()&&null!=mShareInfo&&!TextUtils.isEmpty(mShareInfo.getVideoID())){
            showFinlishToast(null,null,"分享成功");
            String shareTType="qq";
            switch (share_media) {
                case QQ:
                    shareTType="qq";
                    break;
                case WEIXIN:
                    shareTType="weixin";
                    break;
                case SINA:
                    shareTType="sina";
                    break;
                case WEIXIN_CIRCLE:
                    shareTType="weixin_circle";
                    break;
                case QZONE:
                    shareTType="qq_zone";
                    break;
            }
            mSharePresenter.shareResult(VideoApplication.getLoginUserID(), mShareInfo.getVideoID(),shareTType);
        }
    }

    /**
     * 分享开始
     * @param media
     */
    @Override
    public void onShareStart(SHARE_MEDIA media) {

    }

    /**
     * 分享结果
     * @param media
     */
    @Override
    public void onShareResult(SHARE_MEDIA media) {
        countShare(media);
    }

    /**
     * 分享取消
     * @param media
     */
    @Override
    public void onShareCancel(SHARE_MEDIA media) {

    }

    /**
     * 分享错误
     * @param media
     * @param throwable
     */
    @Override
    public void onShareError(SHARE_MEDIA media, Throwable throwable) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    /**
     * @param title 绑定手机号码
     * @param fragmentType Constant.FRAGMENT_TYPE_PHONE_BINDING
     * @param extra getResources().getString(R.string.binding_phone_tips)
     */
    public void binDingPhoneNumber(String title,int fragmentType,String extra) {
        Intent intent = new Intent(TopBaseActivity.this, PhoneChangedActivity.class);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentType);
        intent.putExtra(Constant.KEY_CONTENT_EXTRA,extra);
        startActivityForResult(intent,Constant.MEDIA_BINDING_PHONE_REQUEST);
        overridePendingTransition(R.anim.menu_enter, 0);//进场动画
    }


    /**
     * 分享成功后请求后台记录的结果
     * @param data
     */
    @Override
    public void showShareResulet(String data) {
        if(!TextUtils.isEmpty(data)){
            try {
                JSONObject jsonObject=new JSONObject(data);
                if(jsonObject.length()>0){
                    if(1==jsonObject.getInt("code")){
                        String  newShareCount= new JSONObject(jsonObject.getString("data")).getString("count");
                        String  video_id= new JSONObject(jsonObject.getString("data")).getString("video_id");
                        if(null!=shareFinlishListener){
                            shareFinlishListener.shareFinlish(video_id,newShareCount); //将最新的分享记录交给已注册的分享对象
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    public void showErrorToast(String action, SnackBarListener snackBarListener, String message){
        ToastUtils.showSnackebarStateToast(getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
    }
    public void showNetWorkTips(){
        showErrorToast("网络设置", new SnackBarListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                startActivity(intent);
            }
        }, "没有可用的网络链接");
    }
    public void showFinlishToast(String action, SnackBarListener snackBarListener, String message){
        ToastUtils.showSnackebarStateToast(getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE,message);
    }
}
