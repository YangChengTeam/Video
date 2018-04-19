package com.video.newqu.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.video.newqu.R;
import com.video.newqu.contants.Constant;
import com.video.newqu.listener.PerfectClickListener;
import com.video.newqu.listener.SnackBarListener;
import com.video.newqu.ui.activity.ContentFragmentActivity;
import com.video.newqu.ui.dialog.LoadingProgressView;
import com.video.newqu.util.ScreenUtils;
import com.video.newqu.util.ToastUtils;

/**
 * TinyHung@outlook.com
 * 2017/3/17 15:46
 * 新特性下拉弹窗Fragment
 */

public abstract class BaseDialogFragment<VS extends ViewDataBinding> extends DialogFragment {

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
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);//((ViewGroup) window.findViewById(android.R.id.content))
        View ll = inflater.inflate(R.layout.fragment_base, (ViewGroup) window.findViewById(R.id.content),false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight());//这2行,和上面的一样,注意顺序就行;
        window.setWindowAnimations(R.style.HomeItemPopupAnimation);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.dimAmount=0.0f;
        window.setAttributes(attributes);
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
    }

    protected abstract void initViews();
    public abstract int getLayoutId();


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
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType,String topicID) {
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        intent.putExtra(Constant.KEY_VIDEO_TOPIC_ID,topicID);
        getActivity().startActivity(intent);
    }

    /**
     * 显示加载中
     */
    protected void showGroupLoadingView(){
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
    protected void showGroupContentView() {
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
    protected void showGroupLoadingErrorView() {
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

    protected void onInvisible() {}
    protected void onVisible() {}
    protected void onRefresh() {}
    protected <T extends View> T getView(int id) {
        if(null==getView()) return null;
        return (T) getView().findViewById(id);
    }

    /**
     * 设置进度框文字
     * @param message
     */
    protected void setProgressDialogMessage(String message){
        if(null!=mLoadingProgressedView&&!getActivity().isFinishing()&&mLoadingProgressedView.isShowing()){
            mLoadingProgressedView.setMessage(message);
        }
    }

    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    protected void showProgressDialog(String message,boolean isProgress,boolean isCancle){
        if(null==mLoadingProgressedView){
            mLoadingProgressedView = new LoadingProgressView(getActivity(),isProgress);
        }
        if(!getActivity().isFinishing()){
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.onSetCanceledOnTouchOutside(isCancle);
            mLoadingProgressedView.show();
        }
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
        if(null!=mLoadingProgressedView&&!getActivity().isFinishing()&&mLoadingProgressedView.isShowing()){
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
