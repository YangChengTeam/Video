package com.video.newqu.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.video.newqu.R;
import com.video.newqu.listener.OnAnimationListener;
import com.video.newqu.view.widget.LikeView;

/**
 * TinyHung@outlook.com
 * 2017/7/7 9:16
 * 视屏播放器的外层包裹布局，专用于处理双击点赞事件,简单版
 */

public class VideoGroupRelativeLayout extends RelativeLayout implements View.OnTouchListener {

    private static final String TAG = VideoGroupRelativeLayout.class.getSimpleName();
    private final Context context;
    private LikeView mLikeView;
    private long firstClick;//开始点击的时间
    private long lastClick;//结束点击的时间
    // 计算点击的次数
    private int count;
    private boolean isPriceAnimationPlaying;//点赞动画是否正在播放



    public VideoGroupRelativeLayout(Context context) {
        this(context,null);
    }

    public VideoGroupRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoGroupRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.video_group_layout,this);
        mLikeView = (LikeView) findViewById(R.id.like_view);
        this.setOnTouchListener(this);
    }

    /**
     * 外界传值，是否支持点赞动画功能
     * @param isPrice
     */
    public void setIsPrice(boolean isPrice){
        this.isPriceAnimationPlaying =isPrice;
    }


    /**
     * 拦截双击事件
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
                    count = 0;
                }
                count++;
                if (count == 1) {
                    firstClick = System.currentTimeMillis();
                } else if (count == 2) {
                    lastClick = System.currentTimeMillis();
                    // 两次点击小于300ms双击事件
                    if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件
                        if(!isPriceAnimationPlaying){
                            if(null!=mOnDoubleClickListener){
                                mOnDoubleClickListener.onDoubleClick();
                            }
                            return true;
                        }
                        //单击事件
                        if(null!=mOnDoubleClickListener){
                            mOnDoubleClickListener.onClick();
                        }
                    }
                }
                break;
        }
        return false;
    }

    public void setImageVisibility(){
        if(null!=mLikeView){
            mLikeView.setVisibility(GONE);
        }
    }

    /**
     * 播放动画
     */
    public void startPriceAnimation() {
        if(isPriceAnimationPlaying){
            return;
        }
        if(null!=mLikeView){

            mLikeView.startViewMotion(new OnAnimationListener() {
                @Override
                public void onStart() {
                    isPriceAnimationPlaying=true;
                    if(null!=mLikeView){
                        mLikeView.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onStop() {
                    isPriceAnimationPlaying=false;
                    if(null!=mLikeView){
                        mLikeView.setVisibility(GONE);
                    }
                }
            });
        }

    }


    /**
     * 对外接口
     */
    public interface OnDoubleClickListener{
        void onDoubleClick();
        void onClick();
    }

    private OnDoubleClickListener mOnDoubleClickListener;

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }
}
