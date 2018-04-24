package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import com.video.newqu.R;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.databinding.DialogProgressLayoutBinding;


/**
 * TinyHung@outlook.com
 * 2017/3/25 15:16
 * 加载进度条
 */

public class LoadingProgressView extends BaseDialog<DialogProgressLayoutBinding> {

    private boolean isBack=false;
    private  AnimationDrawable mAnimationDrawable;

    public LoadingProgressView(Activity context, boolean isShowProgress) {
        super(context, R.style.LoadingProgressDialogStyle);
        setContentView(R.layout.dialog_progress_layout);
        if(isShowProgress){
            bindingView.ivLoadingIcon.setVisibility(View.VISIBLE);
            if(null!=mAnimationDrawable&&!mAnimationDrawable.isRunning()) mAnimationDrawable.start();
        }else{
            bindingView.ivLoadingIcon.setVisibility(View.GONE);
            if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        }
//        setCancelable(false);
//        setCanceledOnTouchOutside(false);
    }

    @Override
    public void initViews() {
        mAnimationDrawable= (AnimationDrawable) bindingView.ivLoadingIcon.getDrawable();
        bindingView.finlishView.setMode(1);
    }


    @Override
    public void dismiss() {
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        if(null!=bindingView){
            bindingView.finlishView.setVisibility(View.GONE);
            bindingView.ivLoadingIcon.setVisibility(View.VISIBLE);
        }
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        bindingView.ivLoadingIcon.setVisibility(View.VISIBLE);
        if(null!=mAnimationDrawable&&!mAnimationDrawable.isRunning()) mAnimationDrawable.start();
    }

    public interface  OnDialogBackListener{
        void onBack();
    }
    private OnDialogBackListener mOnDialogBackListener;

    public void setOnDialogBackListener(OnDialogBackListener onDialogBackListener) {
        mOnDialogBackListener = onDialogBackListener;
    }

    /**
     * 将用户按下返回键时间传递出去
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!isBack){
                if(mOnDialogBackListener!=null){
                    mOnDialogBackListener.onBack();
                }
                return false;
            }else{
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置文字
     * @param message
     */
    public void setMessage(String message){
        bindingView.tvLoadingMsg.setText(message);
    }
    private Handler mHandler=new Handler();

    /**
     * 设置文字，是否完成加载，自动关闭时间
     * @param message
     * @param isFinlish
     * @param duration
     */
    public void setMessage(String message,boolean isFinlish,int duration) {
        bindingView.tvLoadingMsg.setText(message);
        if(isFinlish){
            if(bindingView!=null){
                bindingView.ivLoadingIcon.setVisibility(View.GONE);
                if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, duration);
            }
        }
    }

    /**
     * 设置点击空白处是否关闭Dialog
     */
    public void onSetCancelable(boolean isClose){
        setCancelable(isClose);
    }

    /**
     * 设置返回键是否可用
     */
    public void onSetCanceledOnTouchOutside(boolean isBack){
        this.isBack=isBack;
        setCanceledOnTouchOutside(isBack);
    }

    /**
     * 执行完成播放动画的动作
     */
    public void setResultsCompletes(String message,int textColor,boolean isFinlish,int duration){
        bindingView.tvLoadingMsg.setText(message);
        bindingView.tvLoadingMsg.setTextColor(Color.WHITE);
        if(isFinlish&&null!=bindingView){
            bindingView.ivLoadingIcon.setVisibility(View.GONE);
            if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
            bindingView.finlishView.setVisibility(View.VISIBLE);
            bindingView.finlishView.setmResultType(1);//完成的状态
            bindingView.finlishView.initPath();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadingProgressView.this.dismiss();
                }
            },duration);
        }
    }
}
