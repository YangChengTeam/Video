package com.video.newqu.view.layout;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.util.CommonUtils;

/**
 * TinyHung@Outlook.com
 * 2018/3/18
 * 用在RecyclerView上面的加载中、加载失败重试、数据为空状态切换
 */

public class DataChangeView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTextView;
    private AnimationDrawable mAnimationDrawable;
    private Button mBtnSubmit;
    private int clickType=0;//默认是非登录事件

    public DataChangeView(Context context) {
        super(context);
    }

    public DataChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_list_empty,this);
        mImageView = findViewById(R.id.iv_view_icon);
        mTextView = findViewById(R.id.tv_view_content);
        mImageView.setImageResource(R.drawable.ic_list_empty_icon);
        mTextView.setText("没有数据");
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnRefreshListener){
                    mOnRefreshListener.onRefresh();
                }
            }
        });
        this.setEnabled(false);
        mBtnSubmit = findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnSubmitClickListener){
                    mOnSubmitClickListener.onSubmitClick(clickType);
                }
            }
        });
    }


    //设置背景样式
    public void setSubmitBackGround(int resID){
        if(null!=mBtnSubmit) mBtnSubmit.setBackgroundResource(resID);
    }


    /**
     * 停止加载
     */
    private void stopLoading() {
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
            mAnimationDrawable.stop();
        }
        if(null!=mImageView) mImageView.setImageResource(0);
    }

    /**
     * 设置加载中
     * @param content 要显示的文本
     * @param srcResID icon
     */
    public void showLoadingView(String content,int srcResID){
        showLoadingState(content,srcResID);
    }

    public void showLoadingView(int content,int srcResID){
        showLoadingState(getContext().getResources().getString(content),srcResID);
    }

    public void showLoadingView(){
        showLoadingState("加载中...",R.drawable.loading_anim);
    }


    private void showLoadingState(String content,int srcResID ){
        this.setEnabled(false);
        stopLoading();
        if(null!=mBtnSubmit) mBtnSubmit.setVisibility(GONE);
        if(null!=mTextView) mTextView.setText(content);
        if(null!=mImageView){
            if(0!=srcResID){
                mImageView.setImageResource(srcResID);
            }else{
                mImageView.setImageResource(R.drawable.loading_anim);
            }
            mAnimationDrawable = (AnimationDrawable) mImageView.getDrawable();
            if(null!=mAnimationDrawable&&!mAnimationDrawable.isRunning()){
                mAnimationDrawable.start();
            }
        }
    }

    //显示登录界面
    public void showLoginView(String content,int srcResID,boolean flag ){
        showLoginViewState(content,srcResID,flag);
    }
    public void showLoginView(int content,int srcResID,boolean flag ){
        showLoginViewState(getContext().getResources().getString(content),srcResID,flag);
    }

    public void showLoginView(boolean flag){
        showLoginViewState("登录用户可以发布、订阅、分享视频~",R.drawable.user_not_login_thumb,flag);
    }

    private void showLoginViewState(String content,int srcResID,boolean flag ){
        this.setEnabled(false);
        if(null!=mBtnSubmit) mBtnSubmit.setVisibility(flag?VISIBLE:GONE);
        clickType=1;
        if(flag&&null!=mBtnSubmit){
            mBtnSubmit.setBackgroundResource(R.drawable.btn_media_send_app_style_shape);
            mBtnSubmit.setTextColor(CommonUtils.getColor(R.color.white));
            mBtnSubmit.setText("立即登录");
        }
        stopLoading();
        if(null!=mTextView) mTextView.setText(content);
        if(null!=mImageView){
            if(0!=srcResID){
                mImageView.setImageResource(srcResID);
            }else{
                mImageView.setImageResource(R.drawable.user_not_login_thumb);
            }
        }
    }



    /**
     * 设置为空
     * @param content 要显示的文本
     * @param srcResID icon
     */
    public void showEmptyView(String content,int srcResID,boolean flag ){
        showEmptyState(content,srcResID,flag);
    }
    public void showEmptyView(int content,int srcResID,boolean flag ){
        showEmptyState(getContext().getResources().getString(content),srcResID,flag);
    }

    public void showEmptyView(boolean flag ){
        showEmptyState("没有数据",R.drawable.ic_list_empty_icon,flag);
    }

    private void showEmptyState(String content,int srcResID,boolean flag ){
        this.setEnabled(false);

        clickType=0;
        if(null!=mBtnSubmit){
            mBtnSubmit.setVisibility(flag ? VISIBLE : GONE);
            mBtnSubmit.setText("随便逛逛");
            if(flag) {
                mBtnSubmit.setBackgroundResource(R.drawable.btn_appstyle_transpent_selector);
                mBtnSubmit.setTextColor(CommonUtils.getColor(R.color.app_style));
            }else{
                mBtnSubmit.setTextColor(CommonUtils.getColor(R.color.colorTabText));
            }
        }
        stopLoading();
        if(null!=mTextView) mTextView.setText(content);
        if(null!=mImageView){
            if(0!=srcResID){
                mImageView.setImageResource(srcResID);
            }else{
                mImageView.setImageResource(R.drawable.ic_list_empty_icon);
            }
        }
    }


    /**
     * 设置加载失败
     * @param content 要显示的文本
     * @param srcResID icon
     */
    public void showErrorView(String content,int srcResID){
        showErrorState(content,srcResID);
    }

    public void showErrorView(int content,int srcResID){
        showErrorState(getContext().getResources().getString(content),srcResID);
    }

    public void showErrorView(){
        showErrorState("加载失败，点击重试",R.drawable.ic_net_error);
    }

    private void showErrorState(String content,int srcResID ){
        stopLoading();
        if(null!=mTextView) mTextView.setText(content);
        if(null!=mImageView){
            if(0!=srcResID){
                mImageView.setImageResource(srcResID);
            }else{
                mImageView.setImageResource(R.drawable.ic_net_error);
            }
        }
        this.setEnabled(true);
    }

    public interface OnSubmitClickListener{
        void onSubmitClick(int clickType);
    }
    private OnSubmitClickListener mOnSubmitClickListener;

    public void setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
    }

    public interface OnRefreshListener{
        void onRefresh();
    }
    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void onDestroy(){
        stopLoading();
        mAnimationDrawable=null;mTextView=null;mImageView=null;mOnRefreshListener=null;mBtnSubmit=null;
    }
}
