package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.CircleProgressView;


/**
 * TinyHung@outlook.com
 * 2017-06-25 19:19
 * 居中样式的下载进度条
 */

public class DownloadProgressDialog extends Dialog {

    private CircleProgressView mCircleProgressbar;
    private boolean isBack=false;

    public DownloadProgressDialog(Context context) {
        super(context, R.style.LoadingProgressDialogStyle);
        setContentView(R.layout.dialog_download_transcoding);
        Utils.setDialogWidth(this);
        setCancelable(false);
        mCircleProgressbar = (CircleProgressView) findViewById(R.id.circleProgressbar);
        mCircleProgressbar.setProgress(0);
    }

    public void setTipsMessage(String tips){
        TextView tv_loading_message = (TextView) findViewById(R.id.tv_loading_message);
        if(null!=tv_loading_message) tv_loading_message.setText(tips);
    }

    public void setProgress(int progress){
        if(null!=mCircleProgressbar)mCircleProgressbar.setProgressNotInUiThread(progress);
    }


    public void  setBack(boolean flag){
        this.isBack=flag;
    }
    @Override
    public void show() {
        super.show();
        if(null!=mCircleProgressbar){
            mCircleProgressbar.setProgress(0);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setMax(int progress) {
        if(null!=mCircleProgressbar) mCircleProgressbar.setMaxProgress(progress);
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
}
