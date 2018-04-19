package com.video.newqu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.video.newqu.R;
import com.video.newqu.util.Utils;
import com.video.newqu.view.widget.CircleProgressView;


/**
 * TinyHung@outlook.com
 * 2017/3/25 15:16
 * 上传圆形进度条
 */

public class UploadProgressView extends Dialog {

    private CircleProgressView mProgressBar;
    private boolean isBack=false;



    public UploadProgressView(Context context, boolean isShowProgress) {
        super(context, R.style.SpinKitViewSaveFileDialogAnimation);
        setContentView(R.layout.dialog_load_progress_layout);
        Utils.setDialogWidth(this);

        mProgressBar = (CircleProgressView) findViewById(R.id.circleProgressbar);

        if(isShowProgress){
            mProgressBar.setVisibility(View.VISIBLE);
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }


    /**
     * 初始化进度条设置
     */
    public void initProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mProgressBar){
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(0);
        }
    }

    /**
     * 设置文字
     * @param message
     */
    public void setMessage(String message){
        TextView  textView = ((TextView) findViewById(R.id.tv_loading_message));
        textView.setText(message);
        if(null!=mProgressBar) mProgressBar.setVisibility(View.VISIBLE);
    }



    /**
     * 设置当前进度
     * @param progress
     */
    public void setProgressNotInUiThread(int progress) {
        if(null!=mProgressBar){
            mProgressBar.setProgressNotInUiThread(progress);
        }
    }

    public void setProgress(int progress){
        if(null!=mProgressBar){
            mProgressBar.setProgress(progress);
        }
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

    public void setMax(int maxProgress) {
        if(null!=mProgressBar) mProgressBar.setMaxProgress(maxProgress);
    }


    public interface  OnDialogBackListener{
        void onBack();
    }

    private OnDialogBackListener mOnDialogBackListener;


    public void setOnDialogBackListener(OnDialogBackListener onDialogBackListener) {
        mOnDialogBackListener = onDialogBackListener;
    }
}
