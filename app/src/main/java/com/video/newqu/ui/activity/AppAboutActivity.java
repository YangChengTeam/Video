package com.video.newqu.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.video.newqu.R;
import com.video.newqu.base.BaseActivity;
import com.video.newqu.bean.ShareInfo;
import com.video.newqu.bean.UpdataApkInfo;
import com.video.newqu.contants.Cheeses;
import com.video.newqu.databinding.ActivityAboutBinding;
import com.video.newqu.service.DownLoadService;
import com.video.newqu.ui.dialog.BuildManagerDialog;
import com.video.newqu.listener.OnUpdataStateListener;
import com.video.newqu.manager.APKUpdataManager;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.StatusBarUtil;
import com.video.newqu.util.StatusBarUtils;
import com.video.newqu.util.ToastUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.ZoomScrollView;
import java.lang.reflect.Method;
import jp.wasabeef.glide.transformations.BlurTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017-06-09 19:38
 * 关于App
 */

public class AppAboutActivity extends BaseActivity<ActivityAboutBinding> {

    private String mImageURL;
    private int slidingDistance;
    private int imageBgHeight;


    public static void start(Context context) {
        context.startActivity(new Intent(context,AppAboutActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requstDrawStauBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        showToolBar(false);
        mImageURL=new Cheeses().createAboveBGS()[Utils.getRandomNum(0, 29)];
        initSlideShapeTheme(mImageURL,bindingView.imgItemBg);
        setHeaderImage();
    }


    @Override
    public void initViews() {
        bindingView.tvVerstion.setText("当前版本："+ Utils.getVersion());
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //检查版本更新
                    case R.id.bt_checked_upload:
                        checkedUpRefreshAPK();
                        break;
                    case R.id.ivBack:
                        onBackPressed();
                        break;
                    //点击了二维码头像
                    case R.id.iv_icon:
                        MobclickAgent.onEvent(AppAboutActivity.this, "click_follow_wechat");
//                        try {
//                            Intent intent= new Intent();
//                            intent.setAction("android.intent.action.VIEW");
//                            Uri content_url = Uri.parse("http://jump.hupeh.cn/xqsp1223.php");
//                            intent.setData(content_url);
//                            startActivity(intent);
//                        }catch (Exception e){
//                            ToastUtils.showCenterToast("处理失败："+e.getMessage());
//                        }
                        Utils.copyString("新趣小视频助手");
                        ToastUtils.showCenterToast("已复制微信号");
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AppAboutActivity.this)
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
                }
            }
        };
        bindingView.btCheckedUpload.setOnClickListener(onClickListener);
        bindingView.ivBack.setOnClickListener(onClickListener);
        bindingView.ivIcon.setOnClickListener(onClickListener);
        setToolBar();
    }

    @Override
    public void initData() {

    }


    /**
     * 设置toolbar
     */
    protected void setToolBar() {

        setSupportActionBar(bindingView.tbBaseTitle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        // 手动设置才有效果
        bindingView.tbBaseTitle.setTitleTextAppearance(this, R.style.ToolBar_Title);
        bindingView.tbBaseTitle.setSubtitleTextAppearance(this, R.style.Toolbar_SubTitle);
        bindingView.tbBaseTitle.inflateMenu(R.menu.about_header_menu);
        bindingView.tbBaseTitle.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_white));
        bindingView.tbBaseTitle.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    //服务条款
                    case R.id.actionbar_service:
                        loadServiceClause();
                        break;
                    //分享
                    case R.id.actionbar_share:
                        shareIntent();
                        break;
                }
                return false;
            }
        });
    }


    private void loadServiceClause() {
        WebViewActivity.loadUrl(AppAboutActivity.this,"http://v.nq6.com/user_services.html","新趣服务条款");
    }


    // TODO: 2017/7/30官网
    private void shareIntent() {
        ShareInfo shareInfo=new ShareInfo();
        shareInfo.setDesp("短视频笑不停，年轻人都在看! 地球人都关注的短视频神器!");
        shareInfo.setTitle("新趣小视频");
        shareInfo.setUrl("http://v.nq6.com");
        shareInfo.setVideoID("");
        onShare(shareInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_header_menu, menu);
        return true;
    }

    /**
     * 显示popu内的图片
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    /**
     * *** 初始化滑动渐变 一定要实现 ******
     *
     * @param imgUrl    header头部的高斯背景imageUrl
     * @param mHeaderBg header头部高斯背景ImageView控件
     */
    protected void initSlideShapeTheme(Object imgUrl, ImageView mHeaderBg) {
        setImgHeaderBg(imgUrl);
        // toolbar 的高
        int toolbarHeight = bindingView.tbBaseTitle.getLayoutParams().height;
        final int headerBgHeight = toolbarHeight + StatusBarUtil.getStatusBarHeight(this);

        // 使背景图向上移动到图片的最低端，保留（bindingView+statusbar）的高度
        ViewGroup.LayoutParams params = bindingView.ivBaseTitlebarBg.getLayoutParams();
        ViewGroup.MarginLayoutParams ivTitleHeadBgParams = (ViewGroup.MarginLayoutParams) bindingView.ivBaseTitlebarBg.getLayoutParams();
        int marginTop = params.height - headerBgHeight;
        ivTitleHeadBgParams.setMargins(0, -marginTop, 0, 0);

        bindingView.ivBaseTitlebarBg.setImageAlpha(0);
        StatusBarUtils.setTranslucentImageHeader(this, 0, bindingView.tbBaseTitle);

        // 上移背景图片，使空白状态栏消失(这样下方就空了状态栏的高度)
        if (mHeaderBg != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mHeaderBg.getLayoutParams();
            layoutParams.setMargins(0, -StatusBarUtil.getStatusBarHeight(this), 0, 0);

            ViewGroup.LayoutParams imgItemBgparams = mHeaderBg.getLayoutParams();
            // 获得高斯图背景的高度
            imageBgHeight = imgItemBgparams.height;
        }
        // 变色
        initScrollViewListener();
        initNewSlidingParams();
    }


    /**
     * 加载titlebar背景
     */
    private void setImgHeaderBg(Object imgUrl) {
        // 高斯模糊背景 原来 参数：12,5  23,4
        Glide.with(this).load(imgUrl)
                .bitmapTransform(new BlurTransformation(this, 23, 4)).listener(new RequestListener<Object, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                bindingView.tbBaseTitle.setBackgroundColor(Color.TRANSPARENT);
                bindingView.ivBaseTitlebarBg.setImageAlpha(0);
                bindingView.ivBaseTitlebarBg.setVisibility(View.VISIBLE);
                return false;
            }
        }).into(bindingView.ivBaseTitlebarBg);
    }


    private void initScrollViewListener() {
        ((ZoomScrollView) findViewById(R.id.scrollview)).setScrollViewListener(new ZoomScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ZoomScrollView scrollView, int x, int y, int oldx, int oldy) {
                scrollChangeHeader(y);
            }
        });
    }

    private void initNewSlidingParams() {
        int titleBarAndStatusHeight = (int) (CommonUtils.getDimens(R.dimen.nav_bar_height) + StatusBarUtil.getStatusBarHeight(this));
        // 减掉后，没到顶部就不透明了
        slidingDistance = imageBgHeight - titleBarAndStatusHeight - (int) (CommonUtils.getDimens(R.dimen.base_header_activity_slide_more));
    }

    /**
     * 根据页面滑动距离改变Header方法
     */
    private void scrollChangeHeader(int scrolledY) {
        if (scrolledY < 0) {
            scrolledY = 0;
        }
        float alpha = Math.abs(scrolledY) * 1.0f / (slidingDistance);

        Drawable drawable = bindingView.ivBaseTitlebarBg.getDrawable();

        if (drawable == null) {
            return;
        }
        if (scrolledY <= slidingDistance) {
            // title部分的渐变
            drawable.mutate().setAlpha((int) (alpha * 255));
            bindingView.ivBaseTitlebarBg.setImageDrawable(drawable);
            bindingView.tvTitleUserName.setTextColor(Color.argb((int) (int) (alpha * 255), 255, 255, 255));//标题文字颜色
        } else {
            drawable.mutate().setAlpha(255);
            bindingView.ivBaseTitlebarBg.setImageDrawable(drawable);
            bindingView.tvTitleUserName.setTextColor(Color.argb((int) 255, 255, 255, 255));//标题文字颜色
        }
    }



    /**
     * 设置头部背景封面
     */
    private void setHeaderImage() {
        Glide.with(this).load(mImageURL)
                .placeholder(R.drawable.iv_mine_bg)
                .error(R.drawable.iv_mine_bg)
                .bitmapTransform(new BlurTransformation(this, 19, 6)).listener(new RequestListener<String, GlideDrawable>() {//半径：23 抽样：4
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
        new APKUpdataManager(AppAboutActivity.this).checkedBuild(new OnUpdataStateListener() {
            @Override
            public void onNeedUpdata( UpdataApkInfo updataApkInfo) {
                closeProgressDialog();
                final UpdataApkInfo.DataBean dataBean = updataApkInfo.getData();
                if(null!=dataBean){
                    BuildManagerDialog buildManagerDialog =new BuildManagerDialog(AppAboutActivity.this, R.style.UpdataDialogAnimation);
                    buildManagerDialog.setUpdataData(dataBean);
                    buildManagerDialog.setOnUpdataListener(new BuildManagerDialog.OnUpdataListener() {
                        @Override
                        public void onUpdata() {
                            //检查SD卡权限
                            RxPermissions.getInstance(AppAboutActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if(null!=aBoolean&&aBoolean){
                                        Intent service = new Intent(AppAboutActivity.this, DownLoadService.class);
                                        if(1==Utils.getNetworkType()){
                                            ToastUtils.showCenterToast("正在下载中");
                                        }else{
                                            ToastUtils.showCenterToast("下载任务将在连接WIFI后自动开始,请不要关闭本软件");
                                        }
                                        startService(service);
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




    @Override
    public void showErrorView() {
        closeProgressDialog();
    }

    @Override
    public void complete() {
        closeProgressDialog();
    }
}
