package com.video.newqu.base;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.video.newqu.R;
import com.video.newqu.contants.Constant;
import com.video.newqu.databinding.BasePagerBinding;
import com.video.newqu.listener.SnackBarListener;
import com.video.newqu.util.ToastUtils;
import java.security.InvalidParameterException;

/**
 * TinyHung@Outlook.com
 * 2017/9/11
 * 视频编辑界面的Pager基类
 */

public abstract class BasePager <T extends ViewDataBinding>{

    protected T bindingView;
    private BasePagerBinding baseBindingView;
    protected final Activity mContext;

    public BasePager(Activity context){
        this.mContext=context;
        boolean flag=context instanceof Activity;
        if(!flag){
            throw new InvalidParameterException("请传入Activity类型的上下文");
        }
    }

    /**
     * 设置LayoutID
     * @param layoutID
     */
    public void setContentView(int layoutID){
        //父View
        baseBindingView = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.base_pager, null, false);
        //子View
        bindingView = DataBindingUtil.inflate(mContext.getLayoutInflater(),layoutID, (ViewGroup) baseBindingView.getRoot().getParent(), false);
        //父内容容器
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        baseBindingView.viewContent.addView(bindingView.getRoot());//添加至父容器
        initViews();
        initData();
    }

    public View getView() {
        return baseBindingView.getRoot();
    }

    public abstract void initViews();
    public abstract void initData();
    public void onDestroy(){}
    public void onResume(){}
    public void onPause(){}

    /**
     * 失败吐司
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showErrorToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=mContext&&!mContext.isFinishing()){
            ToastUtils.showSnackebarStateToast(mContext.getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
        }
    }

    /**
     * 统一的网络设置入口
     */
    protected void showNetWorkTips(){
        showErrorToast("网络设置", new SnackBarListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                mContext.startActivity(intent);
            }
        }, "没有可用的网络链接");
    }

    /**
     * 成功吐司
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showFinlishToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=mContext&&!mContext.isFinishing()){
            ToastUtils.showSnackebarStateToast(mContext.getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE,message);
        }
    }
}
