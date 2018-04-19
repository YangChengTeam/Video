package com.video.newqu.view.layout;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.video.newqu.R;


/**
 * TinyHung@Outlook.com
 * 2018/3/18
 * 用在RecyclerView上面的加载中、加载失败重试、数据为空状态切换
 */

public class DataChangeMarginView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTextView;
    private AnimationDrawable mAnimationDrawable;

    public DataChangeMarginView(Context context) {
        super(context);
    }

    public DataChangeMarginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_list_margin_empty,this);
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
        showLoadingState("加载中，请稍后",R.drawable.loading_anim);
    }


    private void showLoadingState(String content,int srcResID ){
        this.setEnabled(false);
        stopLoading();
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

    /**
     * 设置为空
     * @param content 要显示的文本
     * @param srcResID icon
     */
    public void showEmptyView(String content,int srcResID){
        showEmptyState(content,srcResID);
    }
    public void showEmptyView(int content,int srcResID){
        showEmptyState(getContext().getResources().getString(content),srcResID);
    }

    public void showEmptyView(){
        showEmptyState("暂时没有数据",R.drawable.ic_list_empty_icon);
    }

    private void showEmptyState(String content,int srcResID ){
        this.setEnabled(false);
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


    public interface OnRefreshListener{
        void onRefresh();
    }
    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void onDestroy(){
        stopLoading();
        mAnimationDrawable=null;mTextView=null;mImageView=null;mOnRefreshListener=null;
    }
}
