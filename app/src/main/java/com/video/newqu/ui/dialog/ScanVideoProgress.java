package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.newqu.R;
import com.video.newqu.util.Utils;

/**
 * TinyHung@outlook.com
 * 2017/6/19 15:27
 * 本机视频全盘扫描
 */

public class ScanVideoProgress extends Dialog {

    private final TextView mTv_progress;
    private final ImageView mIv_finlish;
    private final ImageView mIv_icon;
    private final TextView mTv_tips;

    public interface  OnDissmissListener{
        void onDissmiss();
    }
    private OnDissmissListener mOnDissmissListener;

    public void setOnDissmissListener(OnDissmissListener onDissmissListener) {
        mOnDissmissListener = onDissmissListener;
    }

    public ScanVideoProgress(Context context, int menuDialog) {
        super(context, menuDialog);

        setContentView(R.layout.dialog_scan_video_progress);
        Utils.setDialogWidth(ScanVideoProgress.this);
        ((ImageView) findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=mOnDissmissListener){
                    mOnDissmissListener.onDissmiss();
                }
            }
        });
        //iv_directory_scan
        mIv_icon = (ImageView) findViewById(R.id.iv_icon);
        mTv_progress = (TextView) findViewById(R.id.tv_progress);
        mTv_tips = (TextView) findViewById(R.id.tv_tips);
        mIv_finlish = (ImageView) findViewById(R.id.iv_finlish);
        mTv_tips.setText("全盘扫描比较耗时，请耐心等待");
        setCancelable(false);
        //作者封面
//        Glide.with(context)
//                .load(R.drawable.iv_directory_scan)
//                .error(R.drawable.iv_directory_scan)
//                .animate(R.anim.item_alpha_in)//加载中动画
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存源资源和转换后的资源
//                .skipMemoryCache(true)//跳过内存缓存
//                .into(mIv_icon);
    }

    public void setProgress(String message){
        if(null!=mTv_progress) mTv_progress.setText("扫描中:"+message);
    }

    /**
     * 扫描完成
     * @param size
     */
    public void setFinlish(int size) {
        if(null!=mIv_icon) mIv_icon.setVisibility(View.INVISIBLE);
        if(null!=mIv_finlish) mIv_finlish.setVisibility(View.VISIBLE);
        if(null!=mTv_progress) mTv_progress.setText("扫描完成  共发现"+size+"个视频");
        if(null!=mTv_tips) mTv_tips.setText("");
        setCancelable(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mIv_icon) mIv_icon.setVisibility(View.VISIBLE);
        if(null!=mIv_finlish) mIv_finlish.setVisibility(View.GONE);
        if(null!=mTv_progress) mTv_progress.setText("--");
    }
}
