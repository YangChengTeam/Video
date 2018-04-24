package com.video.newqu.ui.dialog;

import android.app.Activity;
import android.view.View;
import com.video.newqu.R;
import com.video.newqu.base.BaseDialog;
import com.video.newqu.databinding.DialogScanVideoProgressBinding;
import com.video.newqu.util.Utils;

/**
 * TinyHung@outlook.com
 * 2017/6/19 15:27
 * 本机视频全盘扫描
 */

public class ScanVideoProgress extends BaseDialog<DialogScanVideoProgressBinding> {
    public ScanVideoProgress(Activity context, int menuDialog) {
        super(context, menuDialog);
        setContentView(R.layout.dialog_scan_video_progress);
        setCancelable(false);
    }

    @Override
    public void initViews() {
        Utils.setDialogWidth(ScanVideoProgress.this);
        bindingView.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=mOnDissmissListener){
                    mOnDissmissListener.onDissmiss();
                }
            }
        });

        bindingView.tvTips.setText("全盘扫描比较耗时，请耐心等待");
    }

    public void setProgress(String message){
        if(null!=bindingView) bindingView.tvProgress.setText("扫描中:"+message);
    }

    /**
     * 扫描完成
     * @param size
     */
    public void setFinlish(int size) {
        if(null!=bindingView){
            bindingView.ivIcon.setVisibility(View.INVISIBLE);
            bindingView.ivFinlish.setVisibility(View.VISIBLE);
            bindingView.tvProgress.setText("扫描完成  共发现"+size+"个视频");
            bindingView.tvTips.setText("");
        }
        setCancelable(true);
    }

    @Override
    public void dismiss() {
        if(null!=bindingView){
            bindingView.ivIcon.setVisibility(View.VISIBLE);
            bindingView.ivFinlish.setVisibility(View.GONE);
            bindingView.tvProgress.setText("--");
        }
        super.dismiss();
    }

    public interface  OnDissmissListener{
        void onDissmiss();
    }
    private OnDissmissListener mOnDissmissListener;

    public void setOnDissmissListener(OnDissmissListener onDissmissListener) {
        mOnDissmissListener = onDissmissListener;
    }
}
