package com.video.newqu.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.video.newqu.R;
import com.video.newqu.contants.Constant;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.dialog.LoadingProgressView;
import com.video.newqu.listener.PerfectClickListener;
import com.video.newqu.listener.SnackBarListener;
import com.video.newqu.util.ToastUtils;

/**
 * TinyHung@outlook.com
 * 2017/3/17 15:46
 * 片段基类
 */

public abstract class BaseFragment<VS extends ViewDataBinding> extends Fragment {

    // 子布局view
    protected VS bindingView;
    protected LoadingProgressView mLoadingProgressedView;
    //数据加载失败界面
    private LinearLayout mLl_error_view;
    //数据加载中界面
    private LinearLayout mLl_loading_view;
    //加载中动画
    private AnimationDrawable mAnimationDrawable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_base, null);
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), getLayoutId(), null, false);
        if(null!=bindingView){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bindingView.getRoot().setLayoutParams(params);
            FrameLayout contentView = (FrameLayout) ll.findViewById(R.id.content_view);
            contentView.addView(bindingView.getRoot());
        }
        return ll;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        mLl_error_view = getView(R.id.ll_error_view);
        mLl_loading_view = getView(R.id.ll_loading_view);
        ImageView iv_loading_icon = getView(R.id.iv_loading_icon);
        mAnimationDrawable = (AnimationDrawable) iv_loading_icon.getDrawable();
        mLl_error_view.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                onRefresh();
            }
        });
        //默认显示加载中的状态
        showLoadingView("初始化中...");
    }

    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType) {
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        getActivity().startActivity(intent);
    }

    /**
     * 显示加载中
     */
    protected void showLoadingView(String message){
        if(null==bindingView) return;
        if(null!=bindingView.getRoot()&&bindingView.getRoot().getVisibility()!=View.GONE){
            bindingView.getRoot().setVisibility(View.GONE);
        }

        if(null!=mLl_error_view&&mLl_error_view.getVisibility()!=View.GONE){
            mLl_error_view.setVisibility(View.GONE);
        }

        if(null!=mLl_loading_view&&mLl_loading_view.getVisibility()!=View.VISIBLE){
            mLl_loading_view.setVisibility(View.VISIBLE);
        }


        if(null!=mAnimationDrawable&&!getActivity().isFinishing()&&!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }
    }


    /**
     * 显示界面内容
     */
    protected void showContentView() {
        if(null==bindingView) return;
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
            mAnimationDrawable.stop();
        }

        if(null!=mLl_loading_view&&mLl_loading_view.getVisibility()!=View.GONE){
            mLl_loading_view.setVisibility(View.GONE);
        }

        if(null!=mLl_error_view&&mLl_error_view.getVisibility()!=View.GONE){
            mLl_error_view.setVisibility(View.GONE);
        }

        if(null!=bindingView&&null!=bindingView.getRoot()&&bindingView.getRoot().getVisibility()!=View.VISIBLE){
            bindingView.getRoot().setVisibility(View.VISIBLE);
        }
    }


    /**
     * 显示加载失败
     */
    protected void showLoadingErrorView() {
        if(null==bindingView) return;
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
            mAnimationDrawable.stop();
        }

        if(null!=mLl_loading_view&&mLl_loading_view.getVisibility()!=View.GONE){
            mLl_loading_view.setVisibility(View.GONE);
        }

        if(null!=bindingView&&null!=bindingView.getRoot()&&bindingView.getRoot().getVisibility()!=View.GONE){
            bindingView.getRoot().setVisibility(View.GONE);
        }

        if(null!=mLl_error_view&&mLl_error_view.getVisibility()!=View.VISIBLE){
            mLl_error_view.setVisibility(View.VISIBLE);
        }
    }

    protected abstract void initViews();

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onInvisible() {

    }

    protected void onVisible() {

    }


    protected <T extends View> T getView(int id) {
        if(null==getView()) return null;
        return (T) getView().findViewById(id);
    }

    /**
     * 布局
     */
    public abstract int getLayoutId();

    /**
     * 加载失败后点击后的操作
     */
    protected void onRefresh() {

    }

    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    protected void showProgressDialog(String message,boolean isProgress){
        if(null==mLoadingProgressedView){
            mLoadingProgressedView = new LoadingProgressView(getActivity(),isProgress);
        }
        if(!getActivity().isFinishing()){
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }

    /**
     * 关闭进度框
     */
    protected void closeProgressDialog(){
        if(null!=mLoadingProgressedView&&mLoadingProgressedView.isShowing()&&!getActivity().isFinishing()){
            mLoadingProgressedView.dismiss();
            mLoadingProgressedView=null;
        }
    }

    /**
     * 失败吐司
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showErrorToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=getActivity()){
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
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
                startActivity(intent);
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
        if(null!=getActivity()){
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT),action,snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE,message);
        }
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
