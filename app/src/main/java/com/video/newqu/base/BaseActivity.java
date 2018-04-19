package com.video.newqu.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.video.newqu.R;
import com.video.newqu.base.back.SwipeBackActivityBase;
import com.video.newqu.base.back.SwipeBackActivityHelper;
import com.video.newqu.databinding.ActivityBaseBinding;
import com.video.newqu.manager.ActivityCollectorManager;
import com.video.newqu.manager.StatusBarManager;
import com.video.newqu.ui.dialog.LoadingProgressView;
import com.video.newqu.util.CommonUtils;
import com.video.newqu.util.Utils;
import com.video.newqu.view.layout.SwipeBackLayout;

/**
 * TinyHung@outlook.com
 * 2017/3/19 14:51
 * 所有Activity的父类
 */

public abstract  class BaseActivity<SV extends ViewDataBinding> extends TopBaseActivity implements SwipeBackActivityBase {

    // 布局view
    protected SV bindingView;
    protected LoadingProgressView mLoadingProgressedView;
    private boolean isDrawStauBar=true;//是否渲染状态栏？
    private ActivityBaseBinding mBaseBinding;
    private SwipeBackActivityHelper mHelper;

    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollectorManager.addActivity(this);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        swipeBackLayout.setScrollThresHold(0.8f);
        //子类若不想绘制状态栏，由父类来绘制全屏
        if(!isDrawStauBar){
            //顶部透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mBaseBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null, false);
        bindingView = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, false);
        //content
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        FrameLayout mContainer = (FrameLayout) mBaseBinding.getRoot().findViewById(R.id.container);
        mContainer.addView(bindingView.getRoot());
        getWindow().setContentView(mBaseBinding.getRoot());
        if(isDrawStauBar){
            //6.0隐藏状态栏，使用反转颜色
            mBaseBinding.viewStateBar.setVisibility(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M?View.GONE:View.VISIBLE);
            //反转颜色为白色
            StatusBarManager.getInstance().init(this,  CommonUtils.getColor(R.color.white), 0,true);
        }
        mBaseBinding.groupBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initViews();
        initData();
    }


    public abstract void initViews();
    public abstract void initData();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }


    /**
     * 子类可以请求是否需要绘制状态栏
     * @param flag 默认true
     */
    protected void requstDrawStauBar(boolean flag) {
        this.isDrawStauBar =flag;
    }
    /**
     * 是否显示标题栏
     * @param flag
     */
    protected void showToolBar(boolean flag){
        if(null!=mBaseBinding) mBaseBinding.guoupAppBar.setVisibility(flag?View.VISIBLE:View.GONE);
    }

    public void replaceFragment(@IdRes int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(id, fragment).commitAllowingStateLoss();
    }


    /**
     * 设置进度框文字
     * @param message
     */
    public void setProgressDialogMessage(String message){
        if(null!=mLoadingProgressedView&&mLoadingProgressedView.isShowing()){
            mLoadingProgressedView.setMessage(message);
        }
    }

    protected void setTitle(String title){
        if(null!=mBaseBinding) mBaseBinding.groupTitle.setText(title);
    }

    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    public void showProgressDialog(String message,boolean isProgress,boolean isCancle){
        if(!BaseActivity.this.isFinishing()){
            if(null==mLoadingProgressedView){
                mLoadingProgressedView = new LoadingProgressView(this,isProgress);
            }
            mLoadingProgressedView.setCancelable(isCancle);
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }
    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    public void showProgressDialog(String message,boolean isProgress){
        if(!BaseActivity.this.isFinishing()){
            if(null==mLoadingProgressedView){
                mLoadingProgressedView = new LoadingProgressView(this,isProgress);
            }
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }
    /**
     * 关闭进度框
     */
    public void closeProgressDialog(){
        try {
            if(!BaseActivity.this.isFinishing()){
                if(null!=mLoadingProgressedView&&mLoadingProgressedView.isShowing()){
                    mLoadingProgressedView.dismiss();
                    mLoadingProgressedView=null;
                }
            }
        }catch (Exception e){

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ActivityCollectorManager.removeActivity(this);
    }
}
