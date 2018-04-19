package com.video.newqu.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 属性动画
 */
public class AnimationUtil {

    private static final String TAG = AnimationUtil.class.getSimpleName();

    /**
     * 从控件所在位置移动到控件的底部
     *  从上往下出场
     * @return
     */
    public static TranslateAnimation moveToViewBottom() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(350);
        return mHiddenAction;
    }

    /**
     * 从控件所在位置移动到控件的底部
     *  从上往下出场
     * @return
     */

    public static TranslateAnimation moveToViewBottom2() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 2.0f);
        mHiddenAction.setDuration(1000);
        return mHiddenAction;
    }


    /**
     * 从控件的顶部移动到控件所在位置
     * 从上往下进场
     * @return
     */
    public static TranslateAnimation moveToViewTopLocation() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(350);
        return mHiddenAction;
    }

    /**
     * 从控件的底部移动到控件所在位置
     * 从下往上进场
     * @return
     */
    public static TranslateAnimation moveToViewLocation() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(350);
        return mHiddenAction;
    }


    /**
     * 从控件的顶部移动到控件所在位置
     * 从上往下进场
     * @return
     */
    public static TranslateAnimation moveToViewTopLocation5() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(450);
        return mHiddenAction;
    }

    /**
     * 从控件的底部移动到控件所在位置
     * 从下往上进场
     * @return
     */
    public static TranslateAnimation moveToViewLocation5() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(450);
        return mHiddenAction;
    }




    /**
     * 从控件的底部移动到控件所在位置
     * 从下往上进场
     * @return
     */

    public static TranslateAnimation moveToViewLocation2() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(1000);
        return mHiddenAction;
    }



    /**
     * 从控件所在的位置移动到控件的右边
     * @return
     */
    public static TranslateAnimation moveToViewRight() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(350);
        return mHiddenAction;
    }

    /**
     * 从控件右边移动到控件的所在位置
     * @return
     */
    public static TranslateAnimation moveLeftToViewLocation() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(350);
        return mHiddenAction;
    }


    /**
     * 从控件所在位置移动到控件的顶部
     *  从下往下上出场
     * @return
     */
    public static TranslateAnimation moveToViewTop() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction.setDuration(350);
        return mHiddenAction;
    }


    /**
     * 相对自身大小缩放至不见
     * @return
     */
    public static ScaleAnimation moveThisScaleViewToDissmes(){
        ScaleAnimation animation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(100);
        return animation;
    }

    /**
     * 相对自身一般缩放到自身大小
     * @return
     */
    public static ScaleAnimation moveThisScaleViewToBig(){
        ScaleAnimation animation =  new ScaleAnimation(0.1f, 1.0f, 0.0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(200);
        return animation;
    }

    public static ScaleAnimation moveThisScaleViewToBigMenu(){
        ScaleAnimation animation =  new ScaleAnimation(0.1f, 1.0f, 0.0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(100);
        return animation;
    }

    /**
     * 点赞的动画小
     * @return
     */
    public static ScaleAnimation followAnimation(){
        ScaleAnimation followScaleAnimation = new ScaleAnimation(1.0f, 1.6f, 1.0f, 1.6f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        followScaleAnimation.setDuration(500);
        return followScaleAnimation;
    }

    public static void displayAnim(View view, Context context, int animId, int targetVisibility){
        view.clearAnimation();
        Animation anim =
                android.view.animation.AnimationUtils.loadAnimation(context, animId);
        view.setVisibility(targetVisibility);
        view.startAnimation(anim);
    }
}
