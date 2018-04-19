package com.video.newqu.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import com.video.newqu.R;
import com.video.newqu.base.BasePopupWindow;
import com.video.newqu.databinding.PopupwindownTakePictureBinding;

/**
 * TinyHung@Outlook.com
 * 2017/8/28
 * 首页带有动画的菜单弹窗
 */

public class TakePicturePopupWindow extends BasePopupWindow<PopupwindownTakePictureBinding>{

    private final int mScreenWidth;

    public interface  OnTakePictureListener{
        void onClick(int type);
    }
    private int clickType=0;//0:关闭 1:录制 2:上传

    private OnTakePictureListener mOnTakePictureListener;

    public void setOnTakePictureListener(OnTakePictureListener onTakePictureListener) {
        mOnTakePictureListener = onTakePictureListener;
    }

    public TakePicturePopupWindow(AppCompatActivity context) {
        super(context);
        bindingView.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(null!=TakePicturePopupWindow.this&&TakePicturePopupWindow.this.isShowing()){
                    stopCloseAnimation();
                }
                return false;
            }
        });

        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
    }


    @Override
    public int setAnimationStyle() {
        return 0;
    }

    @Override
    public int setLayoutID() {
        return R.layout.popupwindown_take_picture;
    }


    @Override
    public void initViews() {

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击某一个按钮后立即禁用点击事件
                bindingView.btnClose.setClickable(false);
                bindingView.btnRecord.setClickable(false);
                bindingView.btnUpload.setClickable(false);
                switch (view.getId()) {
                    //录制
                    case R.id.btn_record:
                        clickType=1;
                        break;
                    //上传
                    case R.id.btn_upload:
                        clickType=2;
                        break;
                    //关闭
                    case R.id.btn_close:
                        clickType=0;
                        break;
                }
                stopCloseAnimation();
            }
        };
        bindingView.btnRecord.setOnClickListener(onClickListener);
        bindingView.btnUpload.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        //防止动画播放中意重复点击 禁止点击
        bindingView.btnClose.setClickable(false);
        bindingView.btnRecord.setClickable(false);
        bindingView.btnUpload.setClickable(false);
    }

    @Override
    public void initData() {

    }


    /**
     * 打开自己动画
     */
    private void startOpenAnimation() {

        if(0==mScreenWidth||null==bindingView) return;
        bindingView.btnClose.setClickable(false);

        RotateAnimation rotate  = new RotateAnimation(0f, -45f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(200);//设置动画持续时间
        rotate.setFillAfter(true);
        bindingView.ivMenuAmera.startAnimation(rotate);

        //摄制录像动画
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(bindingView.llRecord, "translationY",
                0.0F, -mScreenWidth / 3.5f).setDuration(300);//Y方向移动距离
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(bindingView.llRecord, "translationX",
                0.0F, -mScreenWidth / 5f).setDuration(300);//X方向移动距离
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(bindingView.llRecord, "scaleX", 0.8f, 1.0f).setDuration(300);//X方向放大
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(bindingView.llRecord, "scaleY", 0.8f, 1.0f).setDuration(300);//Y方向放大
        AnimatorSet animSet1 = new AnimatorSet();
//        OvershootInterpolator 到达指定位置后继续向前移动一定的距离然后弹回指定位置,达到颤动的特效,BounceInterpolator到达位置后模拟物理现象反复弹起
        animSet1.setInterpolator(new OvershootInterpolator());//
        animSet1.playTogether(animator1, animator2, animator3, animator4);//四个动画同时执行


        //本地视频上传动画
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(bindingView.llUpload, "translationY",
                0.0F, -mScreenWidth / 3.5f).setDuration(300);
        ObjectAnimator animator9 = ObjectAnimator.ofFloat(bindingView.llUpload, "translationX",
                0.0F, mScreenWidth / 5f).setDuration(300);
        ObjectAnimator animator10 = ObjectAnimator.ofFloat(bindingView.llUpload, "scaleX", 0.8f, 1.0f).setDuration(300);
        ObjectAnimator animator11 = ObjectAnimator.ofFloat(bindingView.llUpload, "scaleY", 0.8f, 1.0f).setDuration(300);
        final AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.setInterpolator(new OvershootInterpolator());
        animatorSet2.playTogether(animator8, animator9, animator10, animator11);
        animatorSet2.setStartDelay(120);

        //两个动画结束之后设置所有按钮可点击,点击即收回动画
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bindingView.btnClose.setClickable(true);
                bindingView.btnRecord.setClickable(true);
                bindingView.btnUpload.setClickable(true);
                bindingView.tvUpload.setVisibility(View.VISIBLE);
            }
        });

        //第一个动画开始之后再开启第二个
        animSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animatorSet2.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bindingView.tvRecord.setVisibility(View.VISIBLE);//动画播放完成显示文字
            }
        });
        animSet1.start();//放在最后是为了初始化完毕所有的动画之后才触发第一个控件的动画
    }



    /**
     * 关闭自己动画
     */
    private void stopCloseAnimation() {

        //开始播放底部按钮动画
        RotateAnimation animation =new RotateAnimation(-45f,0f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setDuration(200);//设置动画持续时间
        animation.setFillAfter(true);//执行完后是否停留在执行完的状态
        bindingView.ivMenuAmera.startAnimation(animation);

        //第一个收回动画
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(bindingView.llRecord, "translationY",
                -mScreenWidth / 3.5f, 0.0F).setDuration(300);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(bindingView.llRecord, "translationX",
                -mScreenWidth / 5f, 0.0F).setDuration(300);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(bindingView.llRecord, "scaleX", 1.0f, 0.8f).setDuration(300);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(bindingView.llRecord, "scaleY", 1.0f, 0.8f).setDuration(300);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.playTogether(animator1, animator2, animator3, animator4);

        //第二个收回动画
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(bindingView.llUpload, "translationY",
                -mScreenWidth / 3.5f, 0.0F).setDuration(300);
        ObjectAnimator animator9 = ObjectAnimator.ofFloat(bindingView.llUpload, "translationX",
                mScreenWidth / 5f, 0.0F).setDuration(300);
        ObjectAnimator animator10 = ObjectAnimator.ofFloat(bindingView.llUpload, "scaleX", 1.0f, 0.8f).setDuration(300);
        ObjectAnimator animator11 = ObjectAnimator.ofFloat(bindingView.llUpload, "scaleY", 1.0f, 0.8f).setDuration(300);
        final AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.setInterpolator(new DecelerateInterpolator());
        //两个动画同时执行
        animatorSet2.playTogether(animator8, animator9, animator10, animator11);
        animatorSet2.setStartDelay(120);

        animator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

                animatorSet2.start();
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        TakePicturePopupWindow.this.dismiss();//收回动画结束后finish此页面
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bindingView.tvUpload.setVisibility(View.INVISIBLE);
            }
        });

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animatorSet2.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bindingView.tvRecord.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if(null!=mOnTakePictureListener){
            mOnTakePictureListener.onClick(clickType);
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        clickType=0;
        startOpenAnimation();
    }
}
