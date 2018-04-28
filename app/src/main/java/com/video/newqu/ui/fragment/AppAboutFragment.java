package com.video.newqu.ui.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.base.BaseFragment;
import com.video.newqu.bean.UpdataApkInfo;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.databinding.FragmentAboutBinding;
import com.video.newqu.listener.OnUpdataStateListener;
import com.video.newqu.manager.APKUpdataManager;
import com.video.newqu.service.DownLoadService;
import com.video.newqu.ui.activity.WebViewActivity;
import com.video.newqu.ui.dialog.BuildManagerDialog;
import com.video.newqu.ui.presenter.MainPresenter;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import jp.wasabeef.glide.transformations.BlurTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2018/4/28
 * 关于
 */

public class AppAboutFragment extends BaseFragment<FragmentAboutBinding,MainPresenter>{

    private String mImageURL;

    @Override
    protected void initViews() {
        bindingView.tvVerstion.setText("当前版本："+ Utils.getVersion());
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                        getActivity().finish();
                        break;
                    //检查版本更新
                    case R.id.bt_checked_upload:
                        checkedUpRefreshAPK();
                        break;
                    //点击了二维码头像
                    case R.id.iv_icon:
                        MobclickAgent.onEvent(getActivity(), "click_follow_wechat");
                        Utils.copyString("新趣小视频助手");
                        ToastUtils.showCenterToast("已复制微信号");
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
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
                        break;
                    case R.id.btn_policy:
                        loadServiceClause();
                        break;
                }
            }
        };
        bindingView.btnPolicy.setOnClickListener(onClickListener);
        bindingView.btCheckedUpload.setOnClickListener(onClickListener);
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.ivIcon.setOnClickListener(onClickListener);
        bindingView.btnPolicy.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageURL=new Cheeses().createAboveBGS()[Utils.getRandomNum(0, 29)];
        setHeaderImage();
    }

    private void loadServiceClause() {
        WebViewActivity.loadUrl(getActivity(),"http://v.nq6.com/user_services.html","新趣服务条款");
    }

    /**
     * 设置头部背景封面
     */
    private void setHeaderImage() {
        Glide.with(this).load(mImageURL)
                .placeholder(R.drawable.iv_mine_bg)
                .error(R.drawable.iv_mine_bg)
                .bitmapTransform(new BlurTransformation(getActivity(), 19, 6)).listener(new RequestListener<String, GlideDrawable>() {//半径：23 抽样：4
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                bindingView.imgItemBg.setImageAlpha(250);
                return false;
            }
        }).into(bindingView.imgItemBg);
    }

    /**
     * 检查版本更新
     */
    private void checkedUpRefreshAPK() {
        showProgressDialog("检查新版本中,请稍后..",true);
        new APKUpdataManager(getActivity()).checkedBuild(new OnUpdataStateListener() {
            @Override
            public void onNeedUpdata( UpdataApkInfo updataApkInfo) {
                closeProgressDialog();
                final UpdataApkInfo.DataBean dataBean = updataApkInfo.getData();
                if(null!=dataBean){
                    BuildManagerDialog buildManagerDialog =new BuildManagerDialog(getActivity());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
