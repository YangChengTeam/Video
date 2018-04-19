package com.video.newqu.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import com.video.newqu.R;
import com.video.newqu.util.AnimationUtil;

/**
 * TinyHung@Outlook.com
 * 2018/3/21.
 * 仿系统的加载中，适用于碎片中的局部View中
 */

public class SwipeLoadingProgress extends RelativeLayout {

    private ProgressWheel mProgressWheel;
    private RelativeLayout mReProgress;

    public SwipeLoadingProgress(Context context) {
        super(context);
        initView(context);
    }

    public SwipeLoadingProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.view_swipe_loading_layout,this);
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        mReProgress = (RelativeLayout) findViewById(R.id.re_progress);
    }

    /**
     * 显示加载中进度条
     */
    public void showLoadingProgress(){
        if(null==mReProgress) return;
        if(mReProgress.getVisibility()==VISIBLE) return;
        mReProgress.setVisibility(VISIBLE);
        TranslateAnimation translateAnimation = AnimationUtil.moveToViewTopLocation();
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if(null!=mOnSwipeProgressEndListener){
                    mOnSwipeProgressEndListener.onShowFinlish();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mReProgress.startAnimation(translateAnimation);
    }

    /**
     * 隐藏加载中进度条
     */
    public void hideLoadProgress(){
        if(null==mReProgress) return;
        if(mReProgress.getVisibility()==GONE) return;
        TranslateAnimation translateAnimation = AnimationUtil.moveToViewTop();
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mReProgress.setVisibility(GONE);
                if(null!=mOnSwipeProgressEndListener){
                    mOnSwipeProgressEndListener.onHideFinlish();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mReProgress.startAnimation(translateAnimation);
    }


    public void setBarColor(int barColor){
        if(null!=mProgressWheel) mProgressWheel.setBarColor(barColor);
    }

    public void setBarWidth(int barWidth){
        if(null!=mProgressWheel) mProgressWheel.setBarWidth(barWidth);
    }

    public void setCircleRadius(int circleRadius){
        if(null!=mProgressWheel) mProgressWheel.setCircleRadius(circleRadius);
    }

    public void setLinearProgress(boolean isLinear){
        if(null!=mProgressWheel) mProgressWheel.setLinearProgress(isLinear);
    }

    public void setProgress(float progress){
        if(null!=mProgressWheel) mProgressWheel.setProgress(progress);
    }

    public void setRimColor(int rimColor){
        if(null!=mProgressWheel) mProgressWheel.setRimColor(rimColor);
    }

    public void setRimWidth(int rimWidth){
        if(null!=mProgressWheel) mProgressWheel.setRimWidth(rimWidth);
    }

    public void setSpinSpeed(float spinSpeed){
        if(null!=mProgressWheel) mProgressWheel.setSpinSpeed(spinSpeed);
    }

    public interface OnSwipeProgressEndListener{
        void onShowFinlish();
        void onHideFinlish();
    }

    private OnSwipeProgressEndListener mOnSwipeProgressEndListener;

    public void setOnSwipeProgressEndListener(OnSwipeProgressEndListener onSwipeProgressEndListener) {
        mOnSwipeProgressEndListener = onSwipeProgressEndListener;
    }
}
